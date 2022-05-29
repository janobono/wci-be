package sk.janobono.wci.api.service.so;

import javax.validation.constraints.NotBlank;

public record ConfirmationRequestSO(@NotBlank String token) {
}
