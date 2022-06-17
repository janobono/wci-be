package sk.janobono.wci.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wci.api.service.AuthApiService;
import sk.janobono.wci.api.service.so.*;

import javax.validation.Valid;
import java.util.Locale;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthApiService authApiService;

    public AuthController(AuthApiService authApiService) {
        this.authApiService = authApiService;
    }

    @PostMapping("/confirm")
    public ResponseEntity<AuthenticationResponseSO> confirm(@Valid @RequestBody ConfirmationRequestSO confirmationRequestSO) {
        LOGGER.debug("confirm({})", confirmationRequestSO);
        return new ResponseEntity<>(authApiService.confirm(confirmationRequestSO), HttpStatus.OK);
    }

    @PostMapping("/change-email")
    public ResponseEntity<AuthenticationResponseSO> changeEmail(@Valid @RequestBody ChangeEmailRequestSO changeEmailRequestSO) {
        LOGGER.debug("changeEmail({})", changeEmailRequestSO);
        return new ResponseEntity<>(authApiService.changeEmail(changeEmailRequestSO), HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<AuthenticationResponseSO> changePassword(@Valid @RequestBody ChangePasswordRequestSO changePasswordRequestSO) {
        LOGGER.debug("changePassword({})", changePasswordRequestSO);
        return new ResponseEntity<>(authApiService.changePassword(changePasswordRequestSO), HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public void resetPassword(Locale locale, @Valid @RequestBody ResetPasswordRequestSO resetPasswordRequestSO) {
        LOGGER.debug("resetPassword({},{})", locale, resetPasswordRequestSO);
        authApiService.resetPassword(locale, resetPasswordRequestSO);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthenticationResponseSO> signIn(@Valid @RequestBody SignInRequestSO signInRequestSO) {
        LOGGER.debug("signIn({})", signInRequestSO);
        return new ResponseEntity<>(authApiService.signIn(signInRequestSO), HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthenticationResponseSO> signUp(Locale locale, @Valid @RequestBody SignUpRequestSO signUpRequestSO) {
        LOGGER.debug("signUp({},{})", locale, signUpRequestSO);
        return new ResponseEntity<>(authApiService.signUp(locale, signUpRequestSO), HttpStatus.OK);
    }
}
