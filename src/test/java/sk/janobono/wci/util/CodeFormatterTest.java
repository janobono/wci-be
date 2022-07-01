package sk.janobono.wci.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CodeFormatterTest {

    private static final String PREFIX = "CL";
    private static final int LENGTH = 19;
    private static final long NUMBER = 1L;
    private static final String RESULT = "CL0000000000000000001";

    @Test
    void fullTest() {
        CodeFormatter codeFormatter = new CodeFormatter();
        assertThat(codeFormatter.format(PREFIX, LENGTH, NUMBER)).isEqualTo(RESULT);
        assertThat(codeFormatter.format(PREFIX, NUMBER)).isEqualTo(RESULT);
    }
}
