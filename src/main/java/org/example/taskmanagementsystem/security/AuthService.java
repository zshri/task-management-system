package org.example.taskmanagementsystem.security;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanagementsystem.exception.UserAlreadyExistsException;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthResponse authenticate(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
            log.info("User with email: {} - successfully authenticated", authRequest.getEmail());
        } catch (BadCredentialsException e) {
            log.info("User with email: {} - not authenticated", authRequest.getEmail());
            throw new BadCredentialsException("Bad credentials");
        }
        User user = userRepository.findByEmail(authRequest.getEmail());
        String accessToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) throws UserAlreadyExistsException {

        if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(bCryptPasswordEncoder.encode(registerRequest.getPassword()))
                .build();

        userRepository.save(user);
        log.info("User successfully saved in database -> {}", registerRequest.getUsername());

        String accessToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .build();
    }

}
