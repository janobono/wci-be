package sk.janobono.wci.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import sk.janobono.wci.common.exception.ApplicationExceptionCode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;

@Component
public class Captcha {

    private static final Logger LOGGER = LoggerFactory.getLogger(Captcha.class);

    private final BCryptPasswordEncoder tokenEncoder;

    public Captcha() {
        this.tokenEncoder = new BCryptPasswordEncoder(6);
    }

    public String generateText() {
        LOGGER.debug("generateText()");
        return new StringTokenizer(UUID.randomUUID().toString(), "-").nextToken();
    }

    public byte[] generateImage(String text) {
        LOGGER.debug("generateImage({})", text);
        int w = 180, h = 40;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);
        g.setFont(new Font("Serif", Font.PLAIN, 26));
        g.setColor(Color.blue);
        int start = 10;
        byte[] bytes = text.getBytes();

        Random random = new Random();
        for (int i = 0; i < bytes.length; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.drawString(new String(new byte[]{bytes[i]}), start + (i * 20), (int) (Math.random() * 20 + 20));
        }
        g.setColor(Color.white);
        for (int i = 0; i < 8; i++) {
            g.drawOval((int) (Math.random() * 160), (int) (Math.random() * 10), 30, 30);
        }
        g.dispose();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", bout);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bout.toByteArray();
    }

    public String generateToken(String text) {
        LOGGER.debug("generateToken({})", text);
        return tokenEncoder.encode(text);
    }

    public boolean isTokenValid(String text, String token) {
        LOGGER.debug("isTokenValid({},{})", text, token);
        return tokenEncoder.matches(text, token);
    }

    public void checkTokenValid(String text, String token) {
        LOGGER.debug("isTokenValid({},{})", text, token);
        if (!isTokenValid(text, token)) {
            throw ApplicationExceptionCode.INVALID_CAPTCHA.exception("Invalid captcha!");
        }
    }
}
