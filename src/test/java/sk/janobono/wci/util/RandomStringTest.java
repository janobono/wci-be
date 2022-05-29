package sk.janobono.wci.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RandomStringTest {

    @Test
    public void generateWrongLengthSettings() {
        assertThrows(
                RuntimeException.class,
                () -> {
                    RandomString.INSTANCE().generate(5, 2);
                }
        );
    }


    @Test
    public void generateWrongMinimalsSettings() {
        assertThrows(
                RuntimeException.class,
                () -> {
                    RandomString.INSTANCE()
                            .alphaNumeric(2, 5, 3)
                            .generate(8);
                }
        );
    }

    public void generateWrongMinimalsSettings01() {
        assertThrows(
                RuntimeException.class,
                () -> {
                    RandomString.INSTANCE()
                            .alphaNumeric(2, 5, 3)
                            .generate(5, 8);
                }
        );
    }

    @Test
    public void generateNumeric() {
        String generated = RandomString.INSTANCE()
                .numeric()
                .generate(20);

        assertThat(generated.length()).isEqualTo(20);
        assertThat(generated).containsPattern("(%d)*");
    }

    @Test
    public void generateAlfa() {
        String generated = RandomString.INSTANCE()
                .alphabet()
                .generate(20);

        assertThat(generated.length()).isEqualTo(20);
        assertThat(generated).containsPattern("[a-z, A-Z]*");
    }

    @Test
    public void generateAlfaNumeric() {
        String generated = RandomString.INSTANCE()
                .alphaNumeric()
                .generate(20);

        assertThat(generated.length()).isEqualTo(20);
        assertThat(generated).containsPattern("[a-z, A-Z, 0-9]*");
    }

    @Test
    public void generateAlfaNumericWithSpecials() {
        String generated = RandomString.INSTANCE()
                .alphaNumericWithSpecial()
                .generate(20);
        assertThat(generated.length()).isEqualTo(20);
    }

    @Test
    public void generateConcrete() {
        String generated = RandomString.INSTANCE()
                .alphaNumeric(5, 10, 5)
                .generate(20);
        assertThat(generated.length()).isEqualTo(20);

        int numbers = 0;
        int characters = 0;
        int capitals = 0;
        char[] chars = generated.toCharArray();
        for (char c : chars) {
            if (c >= 'A' && c <= 'Z') {
                capitals++;
            } else if (c >= 'a' && c <= 'z') {
                characters++;
            } else if (c >= '0' && c <= '9') {
                numbers++;
            }
        }
        assertThat(numbers).isEqualTo(5);
        assertThat(characters).isEqualTo(10);
        assertThat(capitals).isEqualTo(5);
    }

    @Test
    public void generateBetween() {
        String generated = RandomString.INSTANCE()
                .alphaNumeric(5, 10, 5)
                .generate(50, 200);
        assertThat(generated.length()).isBetween(50, 200);

        int numbers = 0;
        int characters = 0;
        int capitals = 0;
        char[] chars = generated.toCharArray();
        for (char c : chars) {
            if (c >= 'A' && c <= 'Z') {
                capitals++;
            } else if (c >= 'a' && c <= 'z') {
                characters++;
            } else if (c >= '0' && c <= '9') {
                numbers++;
            }
        }
        assertThat(numbers >= 5).isTrue();
        assertThat(characters >= 10).isTrue();
        assertThat(capitals >= 5).isTrue();
    }

}
