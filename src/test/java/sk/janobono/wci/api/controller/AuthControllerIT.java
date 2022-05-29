package sk.janobono.wci.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import sk.janobono.wci.BaseIntegrationTest;
import sk.janobono.wci.api.service.so.*;
import sk.janobono.wci.common.properties.GlobalApplicationProperties;
import sk.janobono.wci.common.properties.ResetPasswordMailProperties;
import sk.janobono.wci.common.properties.SignUpMailProperties;
import sk.janobono.wci.component.Captcha;
import sk.janobono.wci.service.ApplicationPropertyService;

import javax.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;

class AuthControllerIT extends BaseIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthControllerIT.class);

    public static final String USERNAME = "jimbop";
    public static final String PASSWORD = "pass123";
    public static final String TITLE_BEFORE = "Phdr.";
    public static final String FIRST_NAME = "Jimbo";
    public static final String MID_NAME = "Lol";
    public static final String LAST_NAME = "Pytlik";
    public static final String TITLE_AFTER = "Csc.";
    public static final String EMAIL = "jimbo.pytlik@domain.com";
    public static final String PHONE_NUMBER = "+999 999 999 999";
    public static final String COMPANY = "Pytlik Wood Inc.";
    public static final String ADDRESS = "Underwood Street";
    public static final String CITY = "Sincity";
    public static final String POSTAL_CODE = "99999";
    public static final String VAT_ID = "12345678";
    public static final String CONTRACTOR_ID = "1234567890";
    public static final String NEW_PASSWORD = "newPass123";

    @Autowired
    public RestTemplateBuilder restTemplateBuilder;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public Captcha captcha;

    @Autowired
    public ApplicationPropertyService applicationPropertyService;

    @Test
    public void fullTest() throws Exception {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.getMessageConverters().add(0, converter);

        prepareData();

        String token = signUp(restTemplate);
        LOGGER.debug("sign up token = {}", token);

        AuthenticationResponseSO authenticationResponseSO = confirm(restTemplate, token);
        LOGGER.debug("confirm01 = {}", authenticationResponseSO);

        authenticationResponseSO = signIn(restTemplate, PASSWORD);
        LOGGER.debug("sign in = {}", authenticationResponseSO);

        authenticationResponseSO = changeEmail(restTemplate, authenticationResponseSO);
        LOGGER.debug("change email = {}", authenticationResponseSO);

        token = resetPassword(restTemplate);
        LOGGER.debug("reset password token = {}", token);
        authenticationResponseSO = confirm(restTemplate, token);
        LOGGER.debug("confirm02 = {}", authenticationResponseSO);

        authenticationResponseSO = signIn(restTemplate, NEW_PASSWORD);
        LOGGER.debug("sign in = {}", authenticationResponseSO);
    }

    private String signUp(RestTemplate restTemplate) throws Exception {
        smtpServer.purgeEmailFromAllMailboxes();

        String captchaText = captcha.generateText();
        String captchaToken = captcha.generateToken(captchaText);

        restTemplate.postForObject(
                getUrl("/auth/sign-up"),
                new SignUpRequestSO(
                        USERNAME,
                        PASSWORD,
                        TITLE_BEFORE,
                        FIRST_NAME,
                        MID_NAME,
                        LAST_NAME,
                        TITLE_AFTER,
                        EMAIL,
                        PHONE_NUMBER,
                        COMPANY,
                        ADDRESS,
                        CITY,
                        POSTAL_CODE,
                        VAT_ID,
                        CONTRACTOR_ID,
                        captchaText,
                        captchaToken
                ),
                AuthenticationResponseSO.class
        );

        MimeMessage mimeMessage = smtpServer.getReceivedMessages()[0];
        return getToken(mimeMessage, ">Please click to confirm to enable account.");
    }

    private AuthenticationResponseSO confirm(RestTemplate restTemplate, String token) throws Exception {
        return restTemplate.postForObject(
                getUrl("/auth/confirm"),
                new ConfirmationRequestSO(
                        token
                ),
                AuthenticationResponseSO.class
        );
    }

    private AuthenticationResponseSO signIn(RestTemplate restTemplate, String password) throws Exception {
        return restTemplate.postForObject(
                getUrl("/auth/sign-in"),
                new SignInRequestSO(
                        USERNAME,
                        password
                ),
                AuthenticationResponseSO.class
        );
    }

    private AuthenticationResponseSO changeEmail(RestTemplate restTemplate, AuthenticationResponseSO authenticationResponseSO) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticationResponseSO.bearer());

        String captchaText = captcha.generateText();
        String captchaToken = captcha.generateToken(captchaText);

        restTemplate.exchange(
                getUrl("/auth/change-email"),
                HttpMethod.POST,
                new HttpEntity<>(new ChangeEmailRequestSO(
                        "a" + EMAIL,
                        PASSWORD,
                        captchaText,
                        captchaToken
                ), headers),
                AuthenticationResponseSO.class
        );

        ResponseEntity<AuthenticationResponseSO> response = restTemplate.exchange(
                getUrl("/auth/change-email"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new ChangeEmailRequestSO(
                                EMAIL,
                                PASSWORD,
                                captchaText,
                                captchaToken
                        ), headers),
                AuthenticationResponseSO.class
        );
        return response.getBody();
    }

    private String resetPassword(RestTemplate restTemplate) throws Exception {
        smtpServer.purgeEmailFromAllMailboxes();

        String captchaText = captcha.generateText();
        String captchaToken = captcha.generateToken(captchaText);

        restTemplate.postForObject(
                getUrl("/auth/reset-password"),
                new ResetPasswordRequestSO(
                        EMAIL,
                        NEW_PASSWORD,
                        captchaText,
                        captchaToken
                ),
                AuthenticationResponseSO.class
        );

        MimeMessage mimeMessage = smtpServer.getReceivedMessages()[0];
        return getToken(mimeMessage, ">Please click to confirm password reset.");
    }

    private String getToken(MimeMessage mimeMessage, String endSequence) throws Exception {
        String token = mimeMessage.getContent().toString();
        token = token.substring(
                token.indexOf("token=") + 6,
                token.indexOf(endSequence)
        ).trim().replaceAll("\"", "");
        return token;
    }

    private String getUrl(String path) {
        return "http://localhost:" + serverPort + "/api" + path;
    }

    private void prepareData() {
        applicationPropertyService.setGlobalApplicationProperties(new GlobalApplicationProperties(
                "https://wci.com",
                "wci@wci.com",
                Long.valueOf(TimeUnit.DAYS.toMillis(1)).intValue(),
                Long.valueOf(TimeUnit.DAYS.toMillis(1)).intValue()
        ));
        applicationPropertyService.setSignUpMailProperties(new SignUpMailProperties(
                "Sign Up",
                "Sign Up",
                "Account creation message.",
                "Please click to confirm to enable account."
        ));
        applicationPropertyService.setResetPasswordMailProperties(new ResetPasswordMailProperties(
                "Reset Password",
                "Reset Password",
                "Password reset message.",
                "Please click to confirm password reset."
        ));
    }
}
