package com.example.jwt_autho.bootstrap;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.jwt_autho.dtos.RegisterUserDto;
import com.example.jwt_autho.entities.Role;
import com.example.jwt_autho.entities.RoleEnum;
import com.example.jwt_autho.entities.User;
import com.example.jwt_autho.repositories.RoleRepository;
import com.example.jwt_autho.repositories.UserRepository;


import java.util.*;

@Component
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public AdminSeeder(
        RoleRepository roleRepository,
        UserRepository  userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
        this.createSuperAdministrator();
    }

    // move the Roles Seeder into AdminSeeder
    // cause to make sure the roles are created 
    // befoer the admin user was created
    private void loadRoles() {
        System.out.println("RUNING ROLEeeeeeeeee SEEK.");
        RoleEnum[] roleNames = new RoleEnum[] { RoleEnum.USER, RoleEnum.ADMIN, RoleEnum.SUPER_ADMIN };
        Map<RoleEnum, String> roleDescriptionMap = Map.of(
            RoleEnum.USER, "Default user role",
            RoleEnum.ADMIN, "Administrator role",
            RoleEnum.SUPER_ADMIN, "Super Administrator role"
        );

        Arrays.stream(roleNames).forEach((roleName) -> {
            Optional<Role> optionalRole = roleRepository.findByName(roleName);

            optionalRole.ifPresentOrElse(System.out::println, () -> {
                Role roleToCreate = new Role();

                roleToCreate.setName(roleName);
                roleToCreate.setDescription(roleDescriptionMap.get(roleName));

                roleRepository.save(roleToCreate);
            });
        });
    }

    // auto create admin account when run the application
    private void createSuperAdministrator() {
        System.out.println("RUNING ADMIN SEEK.");
        RegisterUserDto userDto = new RegisterUserDto();
        userDto.setFullName("Super Admin");
        userDto.setEmail("super.admin@email.com");
        userDto.setPassword("123456");

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.SUPER_ADMIN);
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());

        if (optionalRole.isEmpty()) {
            System.out.println("Role SUPER_ADMIN not found.");
            return;
        }
    
        if (optionalUser.isPresent()) {
            System.out.println("User with email already exists.");
            return;
        }

        var user = new User();
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(optionalRole.get());

        userRepository.save(user);
        System.out.println("Super Admin user created successfully.");
    }
}
