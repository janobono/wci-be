package sk.janobono.wci.api.controller;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sk.janobono.wci.BaseIntegrationTest;
import sk.janobono.wci.api.service.so.CaptchaSO;

import static org.assertj.core.api.Assertions.assertThat;

class CaptchaControllerIT extends BaseIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaControllerIT.class);

    @Test
    public void captcha() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/captcha")).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        CaptchaSO captchaSO = mapFromJson(mvcResult.getResponse().getContentAsString(), CaptchaSO.class);
        assertThat(captchaSO).isNotNull();
        LOGGER.debug("{}", captchaSO);
    }

}
