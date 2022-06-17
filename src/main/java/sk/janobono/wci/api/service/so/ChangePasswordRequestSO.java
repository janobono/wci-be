package sk.janobono.wci.api.service.so;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record ChangePasswordRequestSO(
        @NotBlank @Size(max = 255) String oldPassword,
        @NotBlank @Size(max = 255) String newPassword,
        @NotBlank String captchaText,
        @NotBlank String captchaToken
) {

    @Override
    public String toString() {
        return "ChangePasswordRequestSO{" +
                "captchaText='" + captchaText + '\'' +
                ", captchaToken='" + captchaToken + '\'' +
                '}';
    }
}
