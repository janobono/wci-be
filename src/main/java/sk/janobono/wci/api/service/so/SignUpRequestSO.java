package sk.janobono.wci.api.service.so;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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
        @NotBlank @Size(max = 255) String phoneNumber,
        @NotBlank @Size(max = 255) String company,
        @NotBlank @Size(max = 255) String address,
        @NotBlank @Size(max = 255) String city,
        @NotBlank @Size(max = 255) String postalCode,
        @NotBlank @Size(max = 255) String vatId,
        @NotBlank @Size(max = 255) String contractorId,
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
                ", phoneNumber='" + phoneNumber + '\'' +
                ", company='" + company + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", vatId='" + vatId + '\'' +
                ", contractorId='" + contractorId + '\'' +
                ", captchaText='" + captchaText + '\'' +
                ", captchaToken='" + captchaToken + '\'' +
                '}';
    }
}
