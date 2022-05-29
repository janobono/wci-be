package sk.janobono.wci.api.service.so;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record ChangeEmailRequestSO(
        @NotBlank @Size(max = 255) @Email String email,
        @NotBlank @Size(max = 255) String password,
        @NotBlank String captchaText,
        @NotBlank String captchaToken
) {
    @Override
    public String toString() {
        return "ChangeEmailRequestSO{" +
                "email='" + email + '\'' +
                ", captchaText='" + captchaText + '\'' +
                ", captchaToken='" + captchaToken + '\'' +
                '}';
    }
}
