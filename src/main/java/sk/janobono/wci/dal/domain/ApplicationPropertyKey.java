package sk.janobono.wci.dal.domain;

import java.util.ArrayList;
import java.util.List;

public enum ApplicationPropertyKey {

    GLOBAL_WEB_URL(ApplicationPropertyGroup.GLOBAL),
    GLOBAL_MAIL(ApplicationPropertyGroup.GLOBAL),
    GLOBAL_SIGN_UP_TOKEN_EXPIRATION(ApplicationPropertyGroup.GLOBAL),
    GLOBAL_RESET_PASSWORD_TOKEN_EXPIRATION(ApplicationPropertyGroup.GLOBAL),

    RESET_PASSWORD_MAIL_SUBJECT(ApplicationPropertyGroup.RESET_PASSWORD_MAIL),
    RESET_PASSWORD_MAIL_TITLE(ApplicationPropertyGroup.RESET_PASSWORD_MAIL),
    RESET_PASSWORD_MAIL_MESSAGE(ApplicationPropertyGroup.RESET_PASSWORD_MAIL),
    RESET_PASSWORD_MAIL_LINK(ApplicationPropertyGroup.RESET_PASSWORD_MAIL),

    SIGN_UP_MAIL_SUBJECT(ApplicationPropertyGroup.SIGN_UP_PASSWORD_MAIL),
    SIGN_UP_MAIL_TITLE(ApplicationPropertyGroup.SIGN_UP_PASSWORD_MAIL),
    SIGN_UP_MAIL_MESSAGE(ApplicationPropertyGroup.SIGN_UP_PASSWORD_MAIL),
    SIGN_UP_MAIL_LINK(ApplicationPropertyGroup.SIGN_UP_PASSWORD_MAIL);

    private final ApplicationPropertyGroup group;

    ApplicationPropertyKey(ApplicationPropertyGroup group) {
        this.group = group;
    }

    public static List<ApplicationPropertyKey> byGroup(ApplicationPropertyGroup group) {
        List<ApplicationPropertyKey> result = new ArrayList<>();
        for (ApplicationPropertyKey key : ApplicationPropertyKey.values()) {
            if (key.group == group) {
                result.add(key);
            }
        }
        return result;
    }
}
