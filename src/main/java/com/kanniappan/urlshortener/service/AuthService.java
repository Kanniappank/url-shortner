package com.kanniappan.urlshortener.service;

import com.kanniappan.urlshortener.dto.LoginRequest;
import com.kanniappan.urlshortener.dto.LoginResponse;
import com.kanniappan.urlshortener.exception.InvalidCredentialsException;
import com.kanniappan.urlshortener.security.JwtService;
import com.kanniappan.urlshortener.util.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            UsernamePasswordAuthenticationToken.unauthenticated(
                                    EmailUtils.normalize(request.email()),
                                    request.password()
                            )
                    );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            return new LoginResponse(token);


        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

    }
}
