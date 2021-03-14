package fr.fredos.dvdtheque.dvdtheque.authorization.server.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.fredos.dvdtheque.dvdtheque.authorization.server.jwt.payload.JwtResponse;
import fr.fredos.dvdtheque.dvdtheque.authorization.server.jwt.payload.LoginRequest;
import fr.fredos.dvdtheque.dvdtheque.authorization.server.jwt.security.JwtUtils;

@RestController 
@RequestMapping(path = "/api/auth")
public class AuthenticationController {
	protected Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtUtils jwtUtils;
	
	public static final String SIGNIN_PATH = "/signin";
    //private User user;
	/*
	@GetMapping
	@PreAuthorize("hasRole('ROLE_USER')")
    public Principal retrievePrincipal(Principal principal) {
        return principal;
    }*/
	@GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Principal> get(final Principal principal) {
		//logger.info("************* get="+principal.toString());
        return ResponseEntity.ok(principal);
    }
/*
	@GetMapping("/hello")
	public Map<String, String> hello(final @AuthenticationPrincipal Jwt jwt) {
		System.out.println("headers:\n" + jwt.getHeaders());
		System.out.println("\nclaims:\n" + jwt.getClaims());
		return Collections.singletonMap("message", "Hello " + jwt.getClaimAsString("name"));
	}*/
	
	@PostMapping(SIGNIN_PATH)
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetails user = (UserDetails) authentication.getPrincipal();
		/*List<GrantedAuthority> authorities = user.getAuthorities().stream()
				.map(role -> new SimpleGrantedAuthority(role.getAuthority()))
				.collect(Collectors.toList());*/
/*
		org.springframework.security.core.userdetails.User us = new org.springframework.security.core.userdetails.User(
				user.getUserName(),
				user.getPassword(), 
				authorities);*/
		List<String> roles = user.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt,
				user.getUsername(),roles));
	}
}
