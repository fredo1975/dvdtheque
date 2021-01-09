package fr.fredos.dvdtheque.dvdtheque.resource.server.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController {
	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Principal> get(final Principal principal) {
		LOG.info("***** principal: {}"+principal.toString());
        return ResponseEntity.ok(principal);
    }
}
