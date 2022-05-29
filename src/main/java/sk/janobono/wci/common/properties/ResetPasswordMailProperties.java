package sk.janobono.wci.common.properties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public record ResetPasswordMailProperties(
        @NotEmpty @Size(max = 255) String subject,
        @NotEmpty @Size(max = 255) String title,
        @NotEmpty String message,
        @NotEmpty @Size(max = 255) String link
) {
}
