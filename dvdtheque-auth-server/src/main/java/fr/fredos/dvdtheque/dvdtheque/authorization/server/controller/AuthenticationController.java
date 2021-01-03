package fr.fredos.dvdtheque.dvdtheque.authorization.server.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties.Jwt;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
//@RequestMapping(path = "api/public")
public class AuthenticationController {
	@Autowired
	private AuthenticationManager authenticationManager;
	//@Autowired
    //private JwtTokenUtil jwtTokenUtil;
    //private User user;
/*
	@GetMapping("/hello")
	public Map<String, String> hello(final @AuthenticationPrincipal Jwt jwt) {
		System.out.println("headers:\n" + jwt.getHeaders());
		System.out.println("\nclaims:\n" + jwt.getClaims());
		return Collections.singletonMap("message", "Hello " + jwt.getClaimAsString("name"));
	}*/
	
/*
    @PostMapping("login")
    public ResponseEntity<User> login(@RequestBody @Valid AuthRequest request) {
        try {
            Authentication authenticate = authenticationManager
                .authenticate(
                    new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                    )
                );

            User user = (User) authenticate.getPrincipal();

            return ResponseEntity.ok()
                .header(
                    HttpHeaders.AUTHORIZATION,
                    jwtTokenUtil.generateAccessToken(user)
                )
                .body(user);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }*/
}
