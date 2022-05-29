package sk.janobono.wci.api.service.so;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record UserProfileSO(
        @Size(max = 255) String titleBefore,
        @NotBlank @Size(max = 255) String firstName,
        @Size(max = 255) String midName,
        @NotBlank @Size(max = 255) String lastName,
        @Size(max = 255) String titleAfter
) {
}
