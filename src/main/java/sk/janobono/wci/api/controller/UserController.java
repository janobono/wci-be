package sk.janobono.wci.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sk.janobono.wci.api.service.UserApiService;
import sk.janobono.wci.api.service.so.UserDataSO;
import sk.janobono.wci.api.service.so.UserProfileSO;
import sk.janobono.wci.api.service.so.UserSO;
import sk.janobono.wci.api.service.so.UserSearchCriteriaSO;
import sk.janobono.wci.common.Authority;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserApiService userApiService;

    public UserController(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('lt-admin', 'lt-manager', 'lt-employee')")
    public ResponseEntity<Page<UserSO>> getUsers(Pageable pageable) {
        LOGGER.debug("getUsers({})", pageable);
        return new ResponseEntity<>(userApiService.getUsers(pageable), HttpStatus.OK);
    }

    @GetMapping("/by-search-criteria")
    @PreAuthorize("hasAnyAuthority('lt-admin', 'lt-manager', 'lt-employee')")
    public ResponseEntity<Page<UserSO>> getUsers(
            @RequestParam(value = "search-field", required = false) String searchField,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            Pageable pageable) {
        LOGGER.debug("getUsersBySearchCriteria({},{},{},{})", searchField, username, email, pageable);
        return new ResponseEntity<>(userApiService.getUsers(new UserSearchCriteriaSO(searchField, username, email), pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('lt-admin', 'lt-manager', 'lt-employee')")
    public ResponseEntity<UserSO> getUser(@PathVariable("id") UUID id) {
        LOGGER.debug("getUser({})", id);
        return new ResponseEntity<>(userApiService.getUser(id), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('lt-admin')")
    public ResponseEntity<UserSO> addUser(@Valid @RequestBody UserDataSO userDataSO) {
        LOGGER.debug("addUser({})", userDataSO);
        return new ResponseEntity<>(userApiService.addUser(userDataSO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('lt-admin')")
    public ResponseEntity<UserSO> setUser(@PathVariable("id") UUID id, @Valid @RequestBody UserProfileSO userProfileSO) {
        LOGGER.debug("setUser({},{})", id, userProfileSO);
        return new ResponseEntity<>(userApiService.setUser(id, userProfileSO), HttpStatus.OK);
    }

    @PatchMapping("/{id}/authorities")
    @PreAuthorize("hasAuthority('lt-admin')")
    public ResponseEntity<UserSO> setAuthorities(@PathVariable("id") UUID id, @Valid @RequestBody List<Authority> authorities) {
        LOGGER.debug("setAuthorities({},{})", id, authorities);
        return new ResponseEntity<>(userApiService.setAuthorities(id, authorities), HttpStatus.OK);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('lt-admin')")
    public ResponseEntity<UserSO> setConfirmed(@PathVariable("id") UUID id, @Valid @RequestBody Boolean confirmed) {
        LOGGER.debug("setConfirmed({},{})", id, confirmed);
        return new ResponseEntity<>(userApiService.setConfirmed(id, confirmed), HttpStatus.OK);
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasAuthority('lt-admin')")
    public ResponseEntity<UserSO> setEnabled(@PathVariable("id") UUID id, @Valid @RequestBody Boolean enabled) {
        LOGGER.debug("setEnabled({},{})", id, enabled);
        return new ResponseEntity<>(userApiService.setEnabled(id, enabled), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('lt-admin')")
    public void deleteUser(@PathVariable("id") UUID id) {
        LOGGER.debug("deleteUser({})", id);
        userApiService.deleteUser(id);
    }
}
