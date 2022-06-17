package sk.janobono.wci.api.service.so;

import java.util.List;

public record ApplicationPropertiesSO(
        String applicationName,
        List<String> locales
) {
}
