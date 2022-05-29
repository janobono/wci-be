package sk.janobono.wci.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wci.api.service.CaptchaApiService;
import sk.janobono.wci.api.service.so.CaptchaSO;

@RestController
@RequestMapping(value = "/captcha")
public class CaptchaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaController.class);

    private final CaptchaApiService captchaApiService;

    public CaptchaController(CaptchaApiService captchaApiService) {
        this.captchaApiService = captchaApiService;
    }

    @GetMapping
    public ResponseEntity<CaptchaSO> getCaptcha() {
        LOGGER.debug("getCaptcha()");
        return new ResponseEntity<>(captchaApiService.getCaptcha(), HttpStatus.OK);
    }
}
