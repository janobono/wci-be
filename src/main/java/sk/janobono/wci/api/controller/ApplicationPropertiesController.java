package sk.janobono.wci.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wci.api.service.ApplicationPropertiesApiService;
import sk.janobono.wci.api.service.so.ApplicationPropertiesSO;

import java.util.Locale;

@RestController
@RequestMapping("/application-properties")
public class ApplicationPropertiesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationPropertiesController.class);

    private final ApplicationPropertiesApiService applicationPropertiesApiService;

    public ApplicationPropertiesController(ApplicationPropertiesApiService applicationPropertiesApiService) {
        this.applicationPropertiesApiService = applicationPropertiesApiService;
    }

    @GetMapping()
    public ResponseEntity<ApplicationPropertiesSO> getApplicationProperties(Locale locale) {
        LOGGER.debug("getApplicationProperties({})", locale);
        return new ResponseEntity<>(applicationPropertiesApiService.getApplicationProperties(locale), HttpStatus.OK);
    }
}
