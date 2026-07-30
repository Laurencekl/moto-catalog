package com.example.motocatalogapi.config;

import com.example.motocatalogapi.model.Admin;
import com.example.motocatalogapi.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
 * Cria um admin inicial apenas em ambiente de desenvolvimento.
 *
 * Em produção, essa classe não será executada.
 */
@Configuration
@Profile("dev")
public class DataInitializer {

    @Bean
    public CommandLineRunner criarAdminInicial(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            String email = "admin@email.com";

            if (adminRepository.findByEmail(email).isEmpty()) {
                Admin admin = Admin.builder()
                        .email(email)
                        .senha(passwordEncoder.encode("123456"))
                        .role("ROLE_ADMIN")
                        .build();

                adminRepository.save(admin);

                System.out.println("Admin inicial criado apenas em DEV: admin@email.com / 123456");
            }
        };
    }
}