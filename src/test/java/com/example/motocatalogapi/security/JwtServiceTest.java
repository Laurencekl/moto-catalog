// Testes de criação e validação dos tokens JWT, sem iniciar a API.
package com.example.motocatalogapi.security;

import com.example.motocatalogapi.model.Admin;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private static final String EMAIL = "admin@example.com";
    private static final String EMISSOR = "moto-catalog-api";

    private SecretKey chave;
    private JwtService jwtService;

    @BeforeEach
    void prepararCenario() {

        // Cada teste recebe uma chave aleatória, mantida somente na memória.
        // Não usamos JWT_SECRET, senhas reais ou conexão com o PostgreSQL.
        chave = Jwts.SIG.HS256.key().build();

        // O construtor do serviço recebe a chave representada em Base64.
        String chaveBase64 = Base64.getEncoder()
                .encodeToString(chave.getEncoded());

        // Instancia o serviço diretamente, sem carregar o Spring Boot.
        // 900000 milissegundos equivalem a 15 minutos.
        jwtService = new JwtService(chaveBase64, 900000);
    }

    @Test
    void deveGerarTokenValidoERecuperarEmail() {

        // O serviço usa somente o e-mail. Este objeto não é salvo no banco.
        Admin admin = Admin.builder()
                .email(EMAIL)
                .build();

        String token = jwtService.gerarToken(admin);

        String emailRecuperado =
                jwtService.validarEExtrairEmail(token);

        // Verifica a identidade recuperada e a conversão para segundos.
        assertEquals(EMAIL, emailRecuperado);
        assertEquals(900L, jwtService.getExpiracaoSegundos());
    }

    @Test
    void deveRejeitarTokenExpirado() {

        // Cria um token que venceu há um minuto, sem precisar esperar.
        String token = Jwts.builder()
                .issuer(EMISSOR)
                .subject(EMAIL)
                .expiration(
                        Date.from(Instant.now().minusSeconds(60))
                )
                .signWith(chave, Jwts.SIG.HS256)
                .compact();

        // O teste passa somente se o serviço rejeitar o token expirado.
        assertThrows(
                ExpiredJwtException.class,
                () -> jwtService.validarEExtrairEmail(token)
        );
    }

    @Test
    void deveRejeitarTokenAssinadoComOutraChave() {

        SecretKey outraChave = Jwts.SIG.HS256.key().build();

        // Os dados e o prazo são válidos, mas a assinatura usa outra chave.
        String token = Jwts.builder()
                .issuer(EMISSOR)
                .subject(EMAIL)
                .expiration(
                        Date.from(Instant.now().plusSeconds(900))
                )
                .signWith(outraChave, Jwts.SIG.HS256)
                .compact();

        assertThrows(
                SignatureException.class,
                () -> jwtService.validarEExtrairEmail(token)
        );
    }

    @Test
    void deveRejeitarTokenDeOutroEmissor() {

        // A assinatura usa a chave correta, mas o emissor é inesperado.
        String token = Jwts.builder()
                .issuer("outra-aplicacao")
                .subject(EMAIL)
                .expiration(
                        Date.from(Instant.now().plusSeconds(900))
                )
                .signWith(chave, Jwts.SIG.HS256)
                .compact();

        assertThrows(
                IncorrectClaimException.class,
                () -> jwtService.validarEExtrairEmail(token)
        );
    }

    @Test
    void deveRejeitarTokenSemExpiracao() {

        // Omite expiration de propósito para testar a regra do JwtService.
        String token = Jwts.builder()
                .issuer(EMISSOR)
                .subject(EMAIL)
                .signWith(chave, Jwts.SIG.HS256)
                .compact();

        assertThrows(
                MalformedJwtException.class,
                () -> jwtService.validarEExtrairEmail(token)
        );
    }

    // Garante que a correção não passou a aceitar outros algoritmos.
    @Test
    void deveRejeitarAlgoritmoDiferenteDeHs256() {

        SecretKey chaveHs384 = Jwts.SIG.HS384.key().build();

        String token = Jwts.builder()
                .issuer(EMISSOR)
                .subject(EMAIL)
                .expiration(Date.from(Instant.now().plusSeconds(900)))
                .signWith(chaveHs384, Jwts.SIG.HS384)
                .compact();

        SignatureException erro = assertThrows(
                SignatureException.class,
                () -> jwtService.validarEExtrairEmail(token)
        );

        // Na JJWT 0.12.6, algoritmo não permitido gera esta causa interna.
        // Isso diferencia a rejeição do algoritmo de uma simples chave errada.
        assertInstanceOf(UnsupportedJwtException.class, erro.getCause());
    }
}
