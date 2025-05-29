package materiamarcos.projetosecreto2.Config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;


@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:DefaultSecretKeyParaDesenvolvimentoQueSejaLongaOSuficienteEComMaisDe32Bytes}")
    private String jwtSecretString;

    private Key jwtSecretKey;

    @Value("${app.jwt.expiration-ms:86400000}") // 24 horas
    private int jwtExpirationMs;

    @PostConstruct
    protected void init() {
        // Lógica do init para criar jwtSecretKey a partir de jwtSecretString
        // (Como mostrado na minha resposta anterior, com a verificação do tamanho da chave)
        if (jwtSecretString.length() < 32 && "DefaultSecretKeyParaDesenvolvimentoQueSejaLongaOSuficienteEComMaisDe32Bytes".equals(jwtSecretString) || jwtSecretString.length() < 32 && "DefaultSecretKeyParaDesenvolvimentoQueSejaLongaO کافی}".equals(jwtSecretString) ) {
            this.jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            System.err.println("**********************************************************************************************************");
            System.err.println("AVISO: Chave JWT de desenvolvimento gerada dinamicamente. Esta chave mudará a cada reinício da aplicação.");
            System.err.println("Configure uma 'app.jwt.secret' forte e persistente em 'application.properties' para produção.");
            System.err.println("**********************************************************************************************************");
        } else if (jwtSecretString.length() < 32) {
            throw new IllegalArgumentException("A chave secreta JWT (app.jwt.secret) deve ter pelo menos 32 bytes (256 bits) para HS256.");
        } else {
            this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecretString.getBytes());
        }
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

        // (Adicionaremos métodos para validar o token e pegar o username depois)
        // public String getUsernameFromToken(String token) { ... }
        // public boolean validateToken(String token) { ... }
    }

