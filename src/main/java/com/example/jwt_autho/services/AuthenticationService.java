package com.example.jwt_autho.services;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jwt_autho.dtos.LoginUserDto;
import com.example.jwt_autho.dtos.RegisterUserDto;
import com.example.jwt_autho.entities.Role;
import com.example.jwt_autho.entities.RoleEnum;
import com.example.jwt_autho.entities.User;
import com.example.jwt_autho.repositories.RoleRepository;
import com.example.jwt_autho.repositories.UserRepository;
import com.example.jwt_autho.services.EmailService;

import jakarta.mail.MessagingException;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authenticationManager;

    private final EmailService mailService;

    public AuthenticationService(
        UserRepository userRepository,
        RoleRepository roleRepository,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder,
        EmailService mailService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    public User signup(RegisterUserDto input) {
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
            
        if (optionalRole.isEmpty()) {
            return null;
        }
            
        var user = new User();
        user.setFullName(input.getFullName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRole(optionalRole.get());

        User savedUser = userRepository.save(user);

        // Send a confirmation email to the user
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("userName", savedUser.getFullName()); 
        templateModel.put("email", savedUser.getEmail());    

        try {
            mailService.sendEmail(savedUser.getEmail(), "Welcome to Our Platform", "confirm-signup-email", templateModel);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return savedUser;
    }

    public void deleteUserById(Integer userId) {
        userRepository.deleteById(userId);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}