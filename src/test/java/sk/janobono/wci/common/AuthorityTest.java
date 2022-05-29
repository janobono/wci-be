package sk.janobono.wci.common;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AuthorityTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityTest.class);

    @Test
    public void authoritiesTest() {
        for (Authority authority : Authority.values()) {
            LOGGER.debug("{}", authority);
            Authority parsed = Authority.byValue(authority.toString());
            LOGGER.debug("{}", parsed);
        }
    }
}
