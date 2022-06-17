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
import sk.janobono.wci.component.Captcha;
import sk.janobono.wci.config.ConfigProperties;
import sk.janobono.wci.service.ApplicationPropertyService;

import javax.mail.internet.MimeMessage;
import java.util.Locale;

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
    public ConfigProperties configProperties;

    @Autowired
    public ApplicationPropertyService applicationPropertyService;

    @Test
    public void fullTest() throws Exception {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.getMessageConverters().add(0, converter);

        Locale locale = new Locale("en");

        String token = signUp(restTemplate, locale);
        LOGGER.debug("sign up token = {}", token);

        AuthenticationResponseSO authenticationResponseSO = confirm(restTemplate, token);
        LOGGER.debug("confirm01 = {}", authenticationResponseSO);

        authenticationResponseSO = signIn(restTemplate, PASSWORD);
        LOGGER.debug("sign in = {}", authenticationResponseSO);

        authenticationResponseSO = changeEmail(restTemplate, authenticationResponseSO);
        LOGGER.debug("change email = {}", authenticationResponseSO);

        authenticationResponseSO = changePassword(restTemplate, authenticationResponseSO);
        LOGGER.debug("change password = {}", authenticationResponseSO);

        String[] data = resetPassword(restTemplate, locale);
        token = data[0];
        LOGGER.debug("reset password token = {}", token);
        authenticationResponseSO = confirm(restTemplate, token);
        LOGGER.debug("confirm02 = {}", authenticationResponseSO);

        authenticationResponseSO = signIn(restTemplate, data[1]);
        LOGGER.debug("sign in = {}", authenticationResponseSO);
    }

    private String signUp(RestTemplate restTemplate, Locale locale) throws Exception {
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
        return getSubstring(mimeMessage, "token=", ">Please click to activate your account.")
                .trim().replaceAll("\"", "");
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

    private AuthenticationResponseSO changePassword(RestTemplate restTemplate, AuthenticationResponseSO authenticationResponseSO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticationResponseSO.bearer());

        String captchaText = captcha.generateText();
        String captchaToken = captcha.generateToken(captchaText);

        restTemplate.exchange(
                getUrl("/auth/change-password"),
                HttpMethod.POST,
                new HttpEntity<>(new ChangePasswordRequestSO(
                        PASSWORD,
                        NEW_PASSWORD,
                        captchaText,
                        captchaToken
                ), headers),
                AuthenticationResponseSO.class
        );

        ResponseEntity<AuthenticationResponseSO> response = restTemplate.exchange(
                getUrl("/auth/change-password"),
                HttpMethod.POST,
                new HttpEntity<>(new ChangePasswordRequestSO(
                        NEW_PASSWORD,
                        PASSWORD,
                        captchaText,
                        captchaToken
                ), headers),
                AuthenticationResponseSO.class
        );
        return response.getBody();
    }

    private String[] resetPassword(RestTemplate restTemplate, Locale locale
    ) throws Exception {
        smtpServer.purgeEmailFromAllMailboxes();

        String captchaText = captcha.generateText();
        String captchaToken = captcha.generateToken(captchaText);

        restTemplate.postForObject(
                getUrl("/auth/reset-password"),
                new ResetPasswordRequestSO(
                        EMAIL,
                        captchaText,
                        captchaToken
                ),
                Void.class
        );

        MimeMessage mimeMessage = smtpServer.getReceivedMessages()[0];
        return new String[]{
                getSubstring(mimeMessage, "token=", ">Please click to activate password.")
                        .trim().replaceAll("\"", ""),
                getSubstring(mimeMessage, "New password: ", "<footer>")
                        .replaceAll("</p>", "").replaceAll("</main>", "").trim()
        };
    }

    private String getSubstring(MimeMessage mimeMessage, String startSequence, String endSequence) throws Exception {
        String result = mimeMessage.getContent().toString();
        result = result.substring(
                result.indexOf(startSequence) + startSequence.length(),
                result.indexOf(endSequence)
        );
        return result;
    }

    private String getUrl(String path) {
        return "http://localhost:" + serverPort + "/api" + path;
    }
}
