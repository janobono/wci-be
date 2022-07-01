package sk.janobono.wci.util;


import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class LocalStorageTest {
    @Test
    public void fullTest() throws Exception {
        LocalStorage localStorage = new LocalStorage();
        localStorage.init();

        Path src = Path.of(Objects.requireNonNull(LocalStorageTest.class.getResource("/Debian_logo_01.png")).toURI());
        Path target = localStorage.createTempFile("Test", ".png");

        localStorage.copy(src, target);
        assertThat(localStorage.getFileData(src)).isEqualTo(localStorage.getFileData(target));

        localStorage.write(target, localStorage.getFileData(src));
        assertThat(localStorage.getFileData(src)).isEqualTo(localStorage.getFileData(target));

        Path dir = localStorage.createDirectory("test", "01");
        localStorage.delete(dir);
        localStorage.clean();
    }
}
