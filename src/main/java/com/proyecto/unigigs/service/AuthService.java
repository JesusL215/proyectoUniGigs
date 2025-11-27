package com.proyecto.unigigs.service;

import com.proyecto.unigigs.dto.auth.AuthResponse;
import com.proyecto.unigigs.dto.auth.LoginRequest;
import com.proyecto.unigigs.dto.auth.RegisterRequest;
import com.proyecto.unigigs.model.*;
import com.proyecto.unigigs.repository.CompanyProfileRepository;
import com.proyecto.unigigs.repository.StudentProfileRepository;
import com.proyecto.unigigs.repository.UserRepository;
import com.proyecto.unigigs.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        // Create profile based on role
        if (request.getRole() == UserRole.STUDENT) {
            StudentProfile profile = StudentProfile.builder()
                    .user(savedUser)
                    .university("")
                    .career("")
                    .semester(1)
                    .skills(new ArrayList<>())
                    .interests(new ArrayList<>())
                    .applications(new ArrayList<>())
                    .build();
            studentProfileRepository.save(profile);
        } else if (request.getRole() == UserRole.COMPANY) {
            CompanyProfile profile = CompanyProfile.builder()
                    .user(savedUser)
                    .companyName("")
                    .industry("")
                    .description("")
                    .website("")
                    .internships(new ArrayList<>())
                    .build();
            companyProfileRepository.save(profile);
        }

        // Generate token
        String token = jwtService.generateToken(savedUser);

        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role(savedUser.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
}
