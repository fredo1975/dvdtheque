package fr.fredos.dvdtheque.dvdtheque.authorization.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RestController;

import fr.fredos.dvdtheque.dvdtheque.authorization.server.model.object.User;

@RestController 
//@RequestMapping(path = "api/public")
public class AuthenticationController {
	@Autowired
	private AuthenticationManager authenticationManager;
	//@Autowired
    //private JwtTokenUtil jwtTokenUtil;
    //private User user;

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
