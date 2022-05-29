package sk.janobono.wci.common;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Authority {

    WCI_ADMIN("wci-admin"),
    WCI_MANAGER("wci-manager"),
    WCI_EMPLOYEE("wci-employee"),
    WCI_CUSTOMER("wci-customer");

    private final String value;

    Authority(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }

    public static Authority byValue(String value) {
        Authority result = null;
        for (Authority auth : Authority.values()) {
            if (auth.value.equals(value)) {
                result = auth;
                break;
            }
        }

        return result;
    }
}
