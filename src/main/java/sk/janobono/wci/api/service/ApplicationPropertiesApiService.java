package sk.janobono.wci.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.janobono.wci.api.service.so.ApplicationPropertiesSO;
import sk.janobono.wci.config.ConfigProperties;

import java.util.Locale;

@Service
public class ApplicationPropertiesApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationPropertiesApiService.class);

    private ConfigProperties configProperties;

    @Autowired
    public void setConfigProperties(ConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    public ApplicationPropertiesSO getApplicationProperties(Locale locale) {
        LOGGER.debug("getApplicationProperties()");
        return new ApplicationPropertiesSO(configProperties.applicationName(), configProperties.languages());
    }
}
