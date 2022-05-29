package sk.janobono.wci;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import sk.janobono.wci.config.ConfigProperties;

@SpringBootApplication(scanBasePackages = {
        "sk.janobono.wci"
})
@EnableConfigurationProperties(ConfigProperties.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
