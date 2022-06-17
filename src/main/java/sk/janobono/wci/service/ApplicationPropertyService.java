package sk.janobono.wci.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.janobono.wci.common.properties.GlobalApplicationProperties;
import sk.janobono.wci.common.properties.ResetPasswordMailProperties;
import sk.janobono.wci.common.properties.SignUpMailProperties;
import sk.janobono.wci.dal.domain.ApplicationProperty;
import sk.janobono.wci.dal.domain.ApplicationPropertyGroup;
import sk.janobono.wci.dal.domain.ApplicationPropertyId;
import sk.janobono.wci.dal.domain.ApplicationPropertyKey;
import sk.janobono.wci.dal.repository.ApplicationPropertyRepository;

import java.util.List;
import java.util.Locale;

@Service
public class ApplicationPropertyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationPropertyService.class);

    private ApplicationPropertyRepository applicationPropertyRepository;

    @Autowired
    public void setApplicationPropertyRepository(ApplicationPropertyRepository applicationPropertyRepository) {
        this.applicationPropertyRepository = applicationPropertyRepository;
    }

    public GlobalApplicationProperties getGlobalApplicationProperties(Locale locale) {
        LOGGER.debug("getGlobalApplicationProperties({})", locale);
        List<ApplicationProperty> applicationProperties = applicationPropertyRepository
                .getApplicationPropertiesById_LangAndGroup(locale.getLanguage(), ApplicationPropertyGroup.GLOBAL);
        return new GlobalApplicationProperties(
                getPropertyValue(ApplicationPropertyKey.GLOBAL_WEB_URL, applicationProperties, ""),
                getPropertyValue(ApplicationPropertyKey.GLOBAL_MAIL, applicationProperties, ""),
                getPropertyValue(ApplicationPropertyKey.GLOBAL_SIGN_UP_TOKEN_EXPIRATION, applicationProperties, 0),
                getPropertyValue(ApplicationPropertyKey.GLOBAL_RESET_PASSWORD_TOKEN_EXPIRATION, applicationProperties, 0)
        );
    }

    @Transactional
    public void setGlobalApplicationProperties(Locale locale, GlobalApplicationProperties globalApplicationProperties) {
        LOGGER.debug("setGlobalApplicationProperties({},{})", locale, globalApplicationProperties);

        applicationPropertyRepository.save(new ApplicationProperty(
                new ApplicationPropertyId(ApplicationPropertyKey.GLOBAL_WEB_URL, locale.getLanguage()),
                ApplicationPropertyGroup.GLOBAL,
                globalApplicationProperties.webUrl()
        ));

        applicationPropertyRepository.save(new ApplicationProperty(
                new ApplicationPropertyId(ApplicationPropertyKey.GLOBAL_MAIL, locale.getLanguage()),
                ApplicationPropertyGroup.GLOBAL,
                globalApplicationProperties.mail()
        ));

        applicationPropertyRepository.save(new ApplicationProperty(
                new ApplicationPropertyId(ApplicationPropertyKey.GLOBAL_SIGN_UP_TOKEN_EXPIRATION, locale.getLanguage()),
                ApplicationPropertyGroup.GLOBAL,
                globalApplicationProperties.signUpTokenExpiration().toString()
        ));

        applicationPropertyRepository.save(new ApplicationProperty(
                new ApplicationPropertyId(ApplicationPropertyKey.GLOBAL_RESET_PASSWORD_TOKEN_EXPIRATION, locale.getLanguage()),
                ApplicationPropertyGroup.GLOBAL,
                globalApplicationProperties.resetPasswordTokenExpiration().toString()
        ));
    }

    public ResetPasswordMailProperties getResetPasswordMailProperties(Locale locale) {
        LOGGER.debug("getResetPasswordMailProperties({})", locale);
        List<ApplicationProperty> applicationProperties = applicationPropertyRepository
                .getApplicationPropertiesById_LangAndGroup(locale.getLanguage(), ApplicationPropertyGroup.RESET_PASSWORD_MAIL);
        return new ResetPasswordMailProperties(
                getPropertyValue(ApplicationPropertyKey.RESET_PASSWORD_MAIL_SUBJECT, applicationProperties, ""),
                getPropertyValue(ApplicationPropertyKey.RESET_PASSWORD_MAIL_TITLE, applicationProperties, ""),
                getPropertyValue(ApplicationPropertyKey.RESET_PASSWORD_MAIL_MESSAGE, applicationProperties, ""),
                getPropertyValue(ApplicationPropertyKey.RESET_PASSWORD_MAIL_PASSWORD_MESSAGE, applicationProperties, ""),
                getPropertyValue(ApplicationPropertyKey.RESET_PASSWORD_MAIL_LINK, applicationProperties, "")
        );
    }

    @Transactional
    public void setResetPasswordMailProperties(Locale locale, ResetPasswordMailProperties resetPasswordMailProperties) {
        LOGGER.debug("setResetPasswordMailProperties({},{})", locale, resetPasswordMailProperties);

        applicationPropertyRepository.save(new ApplicationProperty(
                new ApplicationPropertyId(ApplicationPropertyKey.RESET_PASSWORD_MAIL_SUBJECT, locale.getLanguage()),
                ApplicationPropertyGroup.RESET_PASSWORD_MAIL,
                resetPasswordMailProperties.subject()
        ));

        applicationPropertyRepository.save(new ApplicationProperty(
                new ApplicationPropertyId(ApplicationPropertyKey.RESET_PASSWORD_MAIL_TITLE, locale.getLanguage()),
                ApplicationPropertyGroup.RESET_PASSWORD_MAIL,
                resetPasswordMailProperties.title()
        ));

        applicationPropertyRepository.save(new ApplicationProperty(
                new ApplicationPropertyId(ApplicationPropertyKey.RESET_PASSWORD_MAIL_MESSAGE, locale.getLanguage()),
                ApplicationPropertyGroup.RESET_PASSWORD_MAIL,
                resetPasswordMailProperties.message()
        ));

        applicationPropertyRepository.save(new ApplicationProperty(
                new ApplicationPropertyId(ApplicationPropertyKey.RESET_PASSWORD_MAIL_LINK, locale.getLanguage()),
                ApplicationPropertyGroup.RESET_PASSWORD_MAIL,
                resetPasswordMailProperties.link()
        ));
    }

    public SignUpMailProperties getSignUpMailProperties(Locale locale) {
        LOGGER.debug("getResetPasswordMailProperties({})", locale);
        List<ApplicationProperty> applicationProperties = applicationPropertyRepository
                .getApplicationPropertiesById_LangAndGroup(locale.getLanguage(), ApplicationPropertyGroup.SIGN_UP_MAIL);
        return new SignUpMailProperties(
                getPropertyValue(ApplicationPropertyKey.SIGN_UP_MAIL_SUBJECT, applicationProperties, ""),
                getPropertyValue(ApplicationPropertyKey.SIGN_UP_MAIL_TITLE, applicationProperties, ""),
                getPropertyValue(ApplicationPropertyKey.SIGN_UP_MAIL_MESSAGE, applicationProperties, ""),
                getPropertyValue(ApplicationPropertyKey.SIGN_UP_MAIL_LINK, applicationProperties, "")
        );
    }

    @Transactional
    public void setSignUpMailProperties(Locale locale, SignUpMailProperties signUpMailProperties) {
        LOGGER.debug("setResetPasswordMailProperties({})", signUpMailProperties);

        applicationPropertyRepository.save(new ApplicationProperty(
                new ApplicationPropertyId(ApplicationPropertyKey.SIGN_UP_MAIL_SUBJECT, locale.getLanguage()),
                ApplicationPropertyGroup.SIGN_UP_MAIL,
                signUpMailProperties.subject()
        ));

        applicationPropertyRepository.save(new ApplicationProperty(
                new ApplicationPropertyId(ApplicationPropertyKey.SIGN_UP_MAIL_TITLE, locale.getLanguage()),
                ApplicationPropertyGroup.SIGN_UP_MAIL,
                signUpMailProperties.title()
        ));

        applicationPropertyRepository.save(new ApplicationProperty(
                new ApplicationPropertyId(ApplicationPropertyKey.SIGN_UP_MAIL_MESSAGE, locale.getLanguage()),
                ApplicationPropertyGroup.SIGN_UP_MAIL,
                signUpMailProperties.message()
        ));

        applicationPropertyRepository.save(new ApplicationProperty(
                new ApplicationPropertyId(ApplicationPropertyKey.SIGN_UP_MAIL_LINK, locale.getLanguage()),
                ApplicationPropertyGroup.SIGN_UP_MAIL,
                signUpMailProperties.link()
        ));
    }

    private String getPropertyValue(ApplicationPropertyKey key, List<ApplicationProperty> applicationProperties, String defaultValue) {
        return applicationProperties.stream()
                .filter(prop -> prop.getId().getKey() == key)
                .map(ApplicationProperty::getValue)
                .findFirst().orElse(defaultValue);
    }

    private Integer getPropertyValue(ApplicationPropertyKey key, List<ApplicationProperty> applicationProperties, Integer defaultValue) {
        return applicationProperties.stream()
                .filter(prop -> prop.getId().getKey() == key)
                .map(prop -> Integer.valueOf(prop.getValue()))
                .findFirst().orElse(defaultValue);
    }
}
