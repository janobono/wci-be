package sk.janobono.wci.api.service.so;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record SignInRequestSO(
        @NotBlank @Size(max = 255) String username,
        @NotBlank @Size(max = 255) String password
) {
    @Override
    public String toString() {
        return "SignInRequestSO{" +
                "username='" + username + '\'' +
                '}';
    }
}
