package com.example.motocatalogapi.service;

import com.example.motocatalogapi.dto.LoginRequest;
import com.example.motocatalogapi.model.Admin;
import com.example.motocatalogapi.repository.AdminRepository;
import com.example.motocatalogapi.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private static final String EMAIL = "admin@example.com";
    private static final String SENHA = "SenhaSomenteParaTeste!";

    private AdminRepository adminRepository;
    private AuthService authService;
    private Admin admin;

    @BeforeEach
    void prepararCenario() {

        adminRepository = mock(AdminRepository.class);

        SecurityConfig securityConfig =
                new SecurityConfig(adminRepository);

        admin = Admin.builder()
                .id(1L)
                .email(EMAIL)
                .senha(securityConfig.passwordEncoder().encode(SENHA))
                .role("ROLE_ADMIN")
                .build();

        when(adminRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(admin));

        ProviderManager authenticationManager =
                new ProviderManager(
                        securityConfig.authenticationProvider()
                );

        authService = new AuthService(authenticationManager);
    }

    @Test
    void deveAutenticarComEmailESenhaCorretos() {

        LoginRequest loginRequest = criarLogin(EMAIL, SENHA);

        Admin resultado = authService.autenticar(loginRequest);

        assertSame(admin, resultado);
    }

    @Test
    void deveRejeitarSenhaIncorreta() {

        LoginRequest loginRequest =
                criarLogin(EMAIL, "SenhaErrada!");

        assertThrows(
                BadCredentialsException.class,
                () -> authService.autenticar(loginRequest)
        );
    }

    @Test
    void deveRejeitarEmailInexistente() {

        String emailInexistente = "inexistente@example.com";

        when(adminRepository.findByEmail(emailInexistente))
                .thenReturn(Optional.empty());

        LoginRequest loginRequest =
                criarLogin(emailInexistente, SENHA);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.autenticar(loginRequest)
        );
    }

    private LoginRequest criarLogin(String email, String senha) {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setSenha(senha);

        return loginRequest;
    }
}
