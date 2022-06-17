package sk.janobono.wci.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sk.janobono.wci.BaseIntegrationTest;
import sk.janobono.wci.api.service.so.ApplicationPropertiesSO;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationPropertiesControllerIT extends BaseIntegrationTest {

    @Test
    public void getApplicationProperties() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/application-properties")).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(mvcResult.getResponse().getContentAsString()).isNotNull();
        ApplicationPropertiesSO applicationPropertiesSO = mapFromJson(mvcResult.getResponse().getContentAsString(), ApplicationPropertiesSO.class);
        assertThat(applicationPropertiesSO.applicationName()).isEqualTo("WCI");
        assertThat(applicationPropertiesSO.locales().size()).isEqualTo(2);
        assertThat(applicationPropertiesSO.locales().contains("sk")).isTrue();
        assertThat(applicationPropertiesSO.locales().contains("en")).isTrue();
    }
}
