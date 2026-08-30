package com.example.motocatalogapi.service;

import com.example.motocatalogapi.dto.LoginRequest;
import com.example.motocatalogapi.model.Admin;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public Admin autenticar(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken credenciais = new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getSenha()
                );

        Authentication autenticacao = authenticationManager.authenticate(credenciais);
        return (Admin) autenticacao.getPrincipal();
    }
}
