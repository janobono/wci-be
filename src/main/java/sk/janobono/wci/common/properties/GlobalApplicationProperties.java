package sk.janobono.wci.common.properties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record GlobalApplicationProperties(
        @NotEmpty @Size(max = 255) String webUrl,
        @NotEmpty @Size(max = 255) String mail,
        @NotNull Integer signUpTokenExpiration,
        @NotNull Integer resetPasswordTokenExpiration
) {
}
