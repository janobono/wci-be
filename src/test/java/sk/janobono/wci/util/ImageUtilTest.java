package sk.janobono.wci.util;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Objects;

class ImageUtilTest {

    @Test
    void fullTest() throws Exception {
        LocalStorage localStorage = new LocalStorage();
        localStorage.init();
        ImageUtil imageUtil = new ImageUtil();

        Path pngFilePath = Path.of(Objects.requireNonNull(ImageUtilTest.class.getResource("/Debian_logo_01.png")).toURI());
        byte[] data = localStorage.getFileData(pngFilePath);
        Path tmpPath = localStorage.createTempFile("Test", ".png");
        localStorage.write(tmpPath, imageUtil.scaleImage("image/png", data, 100, 100));

        Path jpgFilePath = Path.of(Objects.requireNonNull(ImageUtilTest.class.getResource("/wallpaper.jpg")).toURI());
        data = localStorage.getFileData(jpgFilePath);
        tmpPath = localStorage.createTempFile("Test", ".jpg");
        localStorage.write(tmpPath, imageUtil.scaleImage("image/jpg", data, 100, 100));

        localStorage.clean();
    }
}
