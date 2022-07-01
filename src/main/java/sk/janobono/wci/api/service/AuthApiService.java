package sk.janobono.wci.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wci.api.service.so.*;
import sk.janobono.wci.common.Authority;
import sk.janobono.wci.common.exception.ApplicationExceptionCode;
import sk.janobono.wci.common.properties.GlobalApplicationProperties;
import sk.janobono.wci.common.properties.ResetPasswordMailProperties;
import sk.janobono.wci.common.properties.SignUpMailProperties;
import sk.janobono.wci.component.Captcha;
import sk.janobono.wci.component.jwt.JwtToken;
import sk.janobono.wci.component.mail.*;
import sk.janobono.wci.component.verification.VerificationToken;
import sk.janobono.wci.component.verification.VerificationTokenKey;
import sk.janobono.wci.component.verification.VerificationTokenType;
import sk.janobono.wci.dal.domain.User;
import sk.janobono.wci.dal.repository.UserRepository;
import sk.janobono.wci.service.ApplicationPropertyService;
import sk.janobono.wci.util.RandomString;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AuthApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthApiService.class);

    private PasswordEncoder passwordEncoder;

    private Captcha captcha;

    private JwtToken jwtToken;

    private ApplicationMailSender applicationMailSender;

    private ApplicationMailContentFormatter applicationMailContentFormatter;

    private VerificationToken verificationToken;

    private UserRepository userRepository;

    private ApplicationPropertyService applicationPropertyService;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setCaptcha(Captcha captcha) {
        this.captcha = captcha;
    }

    @Autowired
    public void setJwtToken(JwtToken jwtToken) {
        this.jwtToken = jwtToken;
    }

    @Autowired
    public void setApplicationMailSender(ApplicationMailSender applicationMailSender) {
        this.applicationMailSender = applicationMailSender;
    }

    @Autowired
    public void setApplicationMailContentFormatter(ApplicationMailContentFormatter applicationMailContentFormatter) {
        this.applicationMailContentFormatter = applicationMailContentFormatter;
    }

    @Autowired
    public void setVerificationToken(VerificationToken verificationToken) {
        this.verificationToken = verificationToken;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setApplicationPropertyService(ApplicationPropertyService applicationPropertyService) {
        this.applicationPropertyService = applicationPropertyService;
    }

    @Transactional
    public AuthenticationResponseSO confirm(ConfirmationRequestSO confirmationRequestSO) {
        LOGGER.debug("confirm({})", confirmationRequestSO);
        Map<String, String> data = verificationToken.parseToken(confirmationRequestSO.token());
        User user = switch (VerificationTokenType.valueOf(data.get(VerificationTokenKey.TYPE.name()))) {
            case SIGN_UP -> signUp(
                    UUID.fromString(data.get(VerificationTokenKey.USER_ID.name()))
            );
            case RESET_PASSWORD -> resetPassword(
                    UUID.fromString(data.get(VerificationTokenKey.USER_ID.name())),
                    data.get(VerificationTokenKey.NEW_PASSWORD.name())
            );
        };
        AuthenticationResponseSO authenticationResponse = createAuthenticationResponse(user);
        LOGGER.info("confirm({}) - {}", confirmationRequestSO, authenticationResponse);
        return authenticationResponse;
    }

    @Transactional
    public AuthenticationResponseSO changeEmail(ChangeEmailRequestSO changeEmailRequestSO) {
        LOGGER.debug("changeEmail({})", changeEmailRequestSO);
        captcha.checkTokenValid(changeEmailRequestSO.captchaText(), changeEmailRequestSO.captchaToken());
        if (userRepository.existsByEmail(changeEmailRequestSO.email().toLowerCase())) {
            throw ApplicationExceptionCode.USER_EMAIL_IS_USED.exception("Email is already taken.");
        }
        UserSO userSO = (UserSO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userSO.id()).orElseThrow(
                () -> ApplicationExceptionCode.USER_NOT_FOUND.exception("User with id {0} not found.", userSO.id())
        );
        checkDisabled(user);
        if (!passwordEncoder.matches(changeEmailRequestSO.password(), user.getPassword())) {
            throw ApplicationExceptionCode.INVALID_CREDENTIALS.exception("Invalid credentials.");
        }
        user.setEmail(changeEmailRequestSO.email().toLowerCase());
        user = userRepository.save(user);
        AuthenticationResponseSO authenticationResponse = createAuthenticationResponse(user);
        LOGGER.info("changeEmail({}) - {}", changeEmailRequestSO, authenticationResponse);
        return authenticationResponse;
    }

    @Transactional
    public AuthenticationResponseSO changePassword(ChangePasswordRequestSO changePasswordRequestSO) {
        LOGGER.debug("changePassword({})", changePasswordRequestSO);
        captcha.checkTokenValid(changePasswordRequestSO.captchaText(), changePasswordRequestSO.captchaToken());
        UserSO userSO = (UserSO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userSO.id()).orElseThrow(
                () -> ApplicationExceptionCode.USER_NOT_FOUND.exception("User with id {0} not found.", userSO.id())
        );
        checkDisabled(user);
        if (!passwordEncoder.matches(changePasswordRequestSO.oldPassword(), user.getPassword())) {
            throw ApplicationExceptionCode.INVALID_CREDENTIALS.exception("Invalid credentials.");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequestSO.newPassword()));
        user = userRepository.save(user);
        AuthenticationResponseSO authenticationResponse = createAuthenticationResponse(user);
        LOGGER.info("changePassword({}) - {}", changePasswordRequestSO, authenticationResponse);
        return authenticationResponse;
    }

    public void resetPassword(Locale locale, ResetPasswordRequestSO resetPasswordRequestSO) {
        LOGGER.debug("resetPassword({},{})", locale, resetPasswordRequestSO);
        captcha.checkTokenValid(resetPasswordRequestSO.captchaText(), resetPasswordRequestSO.captchaToken());
        User user = userRepository.findByEmail(resetPasswordRequestSO.email().toLowerCase()).orElseThrow(
                () -> ApplicationExceptionCode.USER_NOT_FOUND.exception("User {0} not found.", resetPasswordRequestSO.email())
        );
        checkDisabled(user);
        sendResetPasswordMail(locale, resetPasswordRequestSO, user);
    }

    public AuthenticationResponseSO signIn(SignInRequestSO signInRequestSO) {
        LOGGER.debug("signIn({})", signInRequestSO);
        User user = userRepository.findByUsername(signInRequestSO.username().toLowerCase()).orElseThrow(
                () -> ApplicationExceptionCode.USER_NOT_FOUND.exception("User {0} not found.", signInRequestSO.username())
        );
        checkDisabled(user);
        if (!passwordEncoder.matches(signInRequestSO.password(), user.getPassword())) {
            throw ApplicationExceptionCode.INVALID_CREDENTIALS.exception("Invalid credentials.");
        }
        AuthenticationResponseSO authenticationResponse = createAuthenticationResponse(user);
        LOGGER.info("authenticate({}) - {}", signInRequestSO, authenticationResponse);
        return authenticationResponse;
    }

    @Transactional
    public AuthenticationResponseSO signUp(Locale locale, SignUpRequestSO signUpRequestSO) {
        LOGGER.debug("signUp({},{})", locale, signUpRequestSO);
        captcha.checkTokenValid(signUpRequestSO.captchaText(), signUpRequestSO.captchaToken());
        if (userRepository.existsByUsername(signUpRequestSO.username().toLowerCase())) {
            throw ApplicationExceptionCode.USER_USERNAME_IS_USED.exception("Username is already taken.");
        }
        if (userRepository.existsByEmail(signUpRequestSO.email().toLowerCase())) {
            throw ApplicationExceptionCode.USER_EMAIL_IS_USED.exception("Email is already taken.");
        }
        if (!signUpRequestSO.gdpr()) {
            throw ApplicationExceptionCode.GDPR.exception("GDPR must be true.");
        }
        User user = new User();
        user.setUsername(signUpRequestSO.username().toLowerCase());
        user.setPassword(passwordEncoder.encode(signUpRequestSO.password()));
        user.setTitleBefore(signUpRequestSO.titleBefore());
        user.setFirstName(signUpRequestSO.firstName());
        user.setMidName(signUpRequestSO.midName());
        user.setLastName(signUpRequestSO.lastName());
        user.setTitleAfter(signUpRequestSO.titleAfter());
        user.setEmail(signUpRequestSO.email().toLowerCase());
        user.setGdpr(signUpRequestSO.gdpr());
        user.setConfirmed(false);
        user.setEnabled(true);
        user = userRepository.save(user);
        sendSignUpMail(locale, signUpRequestSO, user);
        AuthenticationResponseSO authenticationResponse = createAuthenticationResponse(user);
        LOGGER.info("signUp({}) - {}", signUpRequestSO, authenticationResponse);
        return authenticationResponse;
    }

    private AuthenticationResponseSO createAuthenticationResponse(User user) {
        Long issuedAt = System.currentTimeMillis();
        return new AuthenticationResponseSO(jwtToken.generateToken(UserSO.createUserSO(user), issuedAt));
    }

    private User signUp(UUID userId) {
        LOGGER.debug("signUp({})", userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> ApplicationExceptionCode.USER_NOT_FOUND.exception("User with id {0} not found.", userId)
        );
        checkDisabled(user);
        user.setConfirmed(true);
        if (user.getAuthorities().size() == 0) {
            user.getAuthorities().add(Authority.WCI_CUSTOMER);
        }
        return userRepository.save(user);
    }

    private User resetPassword(UUID userId, String newPassword) {
        LOGGER.debug("resetPassword({})", userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> ApplicationExceptionCode.USER_NOT_FOUND.exception("User with id {0} not found.", userId)
        );
        checkDisabled(user);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    void checkDisabled(User user) {
        LOGGER.debug("checkDisabled({})", user);
        if (!user.getEnabled()) {
            throw ApplicationExceptionCode.USER_IS_DISABLED.exception("User is disabled.");
        }
    }

    private void sendResetPasswordMail(Locale locale, ResetPasswordRequestSO resetPasswordRequestSO, User user) {
        LOGGER.debug("sendResetPasswordMail({},{},{})", locale, resetPasswordRequestSO, user);
        GlobalApplicationProperties globalApplicationProperties = applicationPropertyService.getGlobalApplicationProperties(locale);
        ResetPasswordMailProperties resetPasswordMailProperties = applicationPropertyService.getResetPasswordMailProperties(locale);

        Map<String, String> data = new HashMap<>();
        data.put(VerificationTokenKey.TYPE.name(), VerificationTokenType.RESET_PASSWORD.name());
        data.put(VerificationTokenKey.USER_ID.name(), user.getId().toString());
        data.put(VerificationTokenKey.NEW_PASSWORD.name(),
                RandomString.INSTANCE()
                        .alphaNumeric(2, 5, 3)
                        .generate(10)
        );
        long issuedAt = System.currentTimeMillis();
        String token = verificationToken.generateToken(
                data,
                issuedAt,
                issuedAt + TimeUnit.HOURS.toMillis(globalApplicationProperties.resetPasswordTokenExpiration())
        );

        applicationMailSender.sendEmail(new Mail(
                globalApplicationProperties.mail(),
                null,
                List.of(user.getEmail()),
                resetPasswordMailProperties.subject(),
                applicationMailContentFormatter.format(
                        MailTemplate.BASE,
                        new MailBaseContent(
                                resetPasswordMailProperties.title(),
                                Arrays.asList(
                                        resetPasswordMailProperties.message(),
                                        MessageFormat.format(
                                                resetPasswordMailProperties.passwordMessage(),
                                                data.get(VerificationTokenKey.NEW_PASSWORD.name())
                                        )
                                ),
                                new MailLink(
                                        getTokenUrl(globalApplicationProperties.webUrl(), token),
                                        resetPasswordMailProperties.link()
                                )
                        ).getContext()
                ),
                true,
                null
        ));
    }

    private void sendSignUpMail(Locale locale, SignUpRequestSO signUpRequestSO, User user) {
        LOGGER.debug("sendSignUpMail({},{},{})", locale, signUpRequestSO, user);
        GlobalApplicationProperties globalApplicationProperties = applicationPropertyService.getGlobalApplicationProperties(locale);
        SignUpMailProperties signUpMailProperties = applicationPropertyService.getSignUpMailProperties(locale);

        Map<String, String> data = new HashMap<>();
        data.put(VerificationTokenKey.TYPE.name(), VerificationTokenType.SIGN_UP.name());
        data.put(VerificationTokenKey.USER_ID.name(), user.getId().toString());
        long issuedAt = System.currentTimeMillis();
        String token = verificationToken.generateToken(
                data,
                issuedAt,
                issuedAt + TimeUnit.HOURS.toMillis(globalApplicationProperties.signUpTokenExpiration())
        );

        applicationMailSender.sendEmail(new Mail(
                globalApplicationProperties.mail(),
                null,
                List.of(user.getEmail()),
                signUpMailProperties.subject(),
                applicationMailContentFormatter.format(
                        MailTemplate.BASE,
                        new MailBaseContent(
                                signUpMailProperties.title(),
                                Collections.singletonList(signUpMailProperties.message()),
                                new MailLink(
                                        getTokenUrl(globalApplicationProperties.webUrl(), token),
                                        signUpMailProperties.link()
                                )
                        ).getContext()
                ),
                true,
                null
        ));
    }

    private String getTokenUrl(String webUrl, String token) {
        try {
            return webUrl + "/confirm?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
