package sk.janobono.wci.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.janobono.wci.api.service.so.CaptchaSO;
import sk.janobono.wci.component.Captcha;

import java.util.Base64;

@Service
public class CaptchaApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaApiService.class);

    private Captcha captcha;

    @Autowired
    public void setCaptcha(Captcha captcha) {
        this.captcha = captcha;
    }

    public CaptchaSO getCaptcha() {
        LOGGER.debug("getCaptcha()");

        String text = captcha.generateText();
        String image = "data:image/png;base64," + Base64.getEncoder().encodeToString(captcha.generateImage(text));
        String token = captcha.generateToken(text);

        CaptchaSO result = new CaptchaSO(token, image);
        LOGGER.debug("getCaptcha({})={}", captcha, result);
        return result;
    }
}
