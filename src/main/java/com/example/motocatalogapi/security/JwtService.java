// Serviço responsável pela criação e validação dos tokens JWT.
package com.example.motocatalogapi.security;

import com.example.motocatalogapi.model.Admin;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
// Permite configurar os algoritmos antes de construir o parser.
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private static final String EMISSOR = "moto-catalog-api";

    private final SecretKey chave;
    private final long expiracaoMillis;
    private final JwtParser parser;

    public JwtService(
            @Value("${jwt.secret}") String segredoBase64,
            @Value("${jwt.expiration}") long expiracaoMillis
    ) {
        byte[] bytesDaChave = Base64.getDecoder().decode(segredoBase64);

        if (bytesDaChave.length != 32) {
            throw new IllegalArgumentException("JWT_SECRET deve representar uma chave de 32 bytes em Base64.");
        }

        if (expiracaoMillis <= 0) {
            throw new IllegalArgumentException("jwt.expiration deve ser maior que zero.");
        }

        this.chave = Keys.hmacShaKeyFor(bytesDaChave);
        this.expiracaoMillis = expiracaoMillis;

        // Remove os outros algoritmos sem deixar a lista vazia.
        // Na JJWT 0.12.6, clear() falha antes que HS256 possa ser adicionado.
        JwtParserBuilder construtorParser = Jwts.parser()
                .verifyWith(chave)
                .requireIssuer(EMISSOR);

        // var deixa o Java deduzir o tipo da variável pelo valor atribuído.
        var algoritmosPermitidos = construtorParser.sig();

        for (var algoritmo : Jwts.SIG.get().values()) {
            if (!Jwts.SIG.HS256.getId().equals(algoritmo.getId())) {
                algoritmosPermitidos.remove(algoritmo);
            }
        }

        // HS256 continua na lista durante toda a configuração.
        this.parser = algoritmosPermitidos.and().build();
    }

    public String gerarToken(Admin admin) {

        Instant agora = Instant.now();
        Instant vencimento = agora.plusMillis(expiracaoMillis);

        return Jwts.builder().issuer(EMISSOR)
                .subject(admin.getEmail())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(vencimento))
                .signWith(chave, Jwts.SIG.HS256)
                .compact();
    }

    public String validarEExtrairEmail(String token) {

        Claims dados = parser
                .parseSignedClaims(token)
                .getPayload();

        if (dados.getExpiration() == null
                || dados.getSubject() == null
                || dados.getSubject().isBlank()) {

            throw new MalformedJwtException("Token sem os campos obrigatórios.");
        }

        return dados.getSubject();
    }

    public long getExpiracaoSegundos() {
        return expiracaoMillis / 1000;
    }
}
