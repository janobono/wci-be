package sk.janobono.wci.api.service.so;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record SignUpRequestSO(
        @NotBlank @Size(max = 255) String username,
        @NotBlank @Size(max = 255) String password,
        @Size(max = 255) String titleBefore,
        @NotBlank @Size(max = 255) String firstName,
        @Size(max = 255) String midName,
        @NotBlank @Size(max = 255) String lastName,
        @Size(max = 255) String titleAfter,
        @NotBlank @Size(max = 255) @Email String email,
        @NotNull Boolean gdpr,
        @NotBlank String captchaText,
        @NotBlank String captchaToken
) {
    @Override
    public String toString() {
        return "SignUpRequestSO{" +
                "username='" + username + '\'' +
                ", titleBefore='" + titleBefore + '\'' +
                ", firstName='" + firstName + '\'' +
                ", midName='" + midName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", titleAfter='" + titleAfter + '\'' +
                ", email='" + email + '\'' +
                ", gdpr=" + gdpr +
                ", captchaText='" + captchaText + '\'' +
                ", captchaToken='" + captchaToken + '\'' +
                '}';
    }
}
