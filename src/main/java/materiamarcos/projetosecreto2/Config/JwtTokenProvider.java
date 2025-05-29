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
        // Lógica do init
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
//Validação de nome

    // Método para obter o username (subject) a partir do token JWT

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey) // Define a chave
                .build()
                .parseClaimsJws(token) // Faz o parse
                .getBody()
                .getSubject(); // Pega o "subject"
    }

    // Método para validar o token JWT
    public boolean validateToken(String token) {
        try {
            // Tenta fazer o parse do token. Se conseguir e não lançar exceção
            // a assinatura é válida e o token não está expirado (o parseClaimsJws já verifica isso).
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException ex) {
            System.err.println("Assinatura JWT inválida: " + ex.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException ex) {
            System.err.println("Token JWT malformado: " + ex.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            System.err.println("Token JWT expirado: " + ex.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException ex) {
            System.err.println("Token JWT não suportado: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            // Este catch é para o caso de a string do token estar vazia ou nula
            System.err.println("Argumentos da claim JWT estão vazios ou token é nulo: " + ex.getMessage());
        }
        return false;
    }
    }

