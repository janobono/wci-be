package sk.janobono.wci.api.service.so;

import sk.janobono.wci.common.Authority;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public record UserDataSO(
        @NotBlank @Size(max = 255) String username,
        @Size(max = 255) String titleBefore,
        @NotBlank @Size(max = 255) String firstName,
        @Size(max = 255) String midName,
        @NotBlank @Size(max = 255) String lastName,
        @Size(max = 255) String titleAfter,
        @NotBlank @Size(max = 255) @Email String email,
        @NotNull Boolean gdpr,
        @NotNull Boolean confirmed,
        @NotNull Boolean enabled,
        List<Authority> authorities
) {
    @Override
    public String toString() {
        return "UserDataSO{" +
                "username='" + username + '\'' +
                ", titleBefore='" + titleBefore + '\'' +
                ", firstName='" + firstName + '\'' +
                ", midName='" + midName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", titleAfter='" + titleAfter + '\'' +
                ", email='" + email + '\'' +
                ", gdpr=" + gdpr +
                ", confirmed=" + confirmed +
                ", enabled=" + enabled +
                ", authorities=" + authorities +
                '}';
    }
}
