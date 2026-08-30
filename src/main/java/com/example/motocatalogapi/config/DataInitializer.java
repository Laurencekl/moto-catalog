package com.example.motocatalogapi.config;

import com.example.motocatalogapi.model.Admin;
import com.example.motocatalogapi.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Configuration
@Profile("dev")
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner criarAdminInicial(AdminRepository adminRepository, PasswordEncoder passwordEncoder,
            @Value("${APP_ADMIN_EMAIL:}") String email,
            @Value("${APP_ADMIN_PASSWORD:}") String senha,
            @Value("${APP_ADMIN_RESET_PASSWORD:false}") boolean redefinirSenha
    ) {
        return args -> {

            if (email.isBlank()) {
                throw new IllegalStateException("Configure APP_ADMIN_EMAIL no ambiente de desenvolvimento.");
            }

            if (senha.isBlank() || senha.length() < 12) {
                throw new IllegalStateException("Configure APP_ADMIN_PASSWORD com pelo menos 12 caracteres.");
            }

            if (senha.getBytes(StandardCharsets.UTF_8).length > 72) {
                throw new IllegalStateException("APP_ADMIN_PASSWORD deve ter no máximo 72 bytes em UTF-8.");
            }

            Optional<Admin> adminExistente =
                    adminRepository.findByEmail(email);

            if (adminExistente.isPresent()) {

                if (redefinirSenha) {
                    Admin admin = adminExistente.get();
                    admin.setSenha(passwordEncoder.encode(senha));
                    adminRepository.save(admin);

                    log.info("Senha do administrador de desenvolvimento atualizada.");
                } else {
                    log.info("Administrador já existe. Senha mantida.");
                }
                return;
            }

            Admin admin = Admin.builder()
                    .email(email)
                    .senha(passwordEncoder.encode(senha))
                    .role("ROLE_ADMIN")
                    .build();

            adminRepository.save(admin);

            log.info("Administrador de desenvolvimento criado.");
        };
    }
}