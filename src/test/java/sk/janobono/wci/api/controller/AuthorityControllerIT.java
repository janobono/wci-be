package sk.janobono.wci.api.controller;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sk.janobono.wci.BaseIntegrationTest;
import sk.janobono.wci.common.Authority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorityControllerIT extends BaseIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityControllerIT.class);

    @Test
    @WithMockUser(username = "test", authorities = {"lt-admin"})
    public void getAuthoritiesTest() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get("/authorities")).andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        List<Authority> authorities = mapListFromJson(mvcResult.getResponse().getContentAsString(), Authority.class);
        assertThat(authorities.size()).isEqualTo(Authority.values().length);
        LOGGER.debug("{}", authorities);
    }
}
