package sk.janobono.wci.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScDfTest {

    private static final String TEXT = "ľščťžýáíéňäúô ĽŠČŤŽÝÁÍÉŇÄÚÔ";
    private static final String DF_RESULT = "lsctzyaienauo LSCTZYAIENAUO";
    private static final String SCDF_RESULT = "lsctzyaienauo lsctzyaienauo";

    @Test
    public void fullTest() {
        ScDf scdf = new ScDf();
        assertThat(scdf.toDf(TEXT)).isEqualTo(DF_RESULT);
        assertThat(scdf.toScDf(TEXT)).isEqualTo(SCDF_RESULT);
    }
}
