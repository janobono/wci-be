package sk.janobono.wci.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.janobono.wci.common.Authority;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = "/authorities")
public class AuthorityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityController.class);

    @GetMapping
    @PreAuthorize("hasAnyAuthority('lt-admin')")
    public ResponseEntity<List<Authority>> getAuthorities() {
        LOGGER.debug("getAuthorities()");
        return new ResponseEntity<>(Arrays.asList(Authority.values()), HttpStatus.OK);
    }
}
