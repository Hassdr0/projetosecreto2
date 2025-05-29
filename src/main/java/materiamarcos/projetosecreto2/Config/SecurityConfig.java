package materiamarcos.projetosecreto2.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
@EnableWebSecurity // Habilita a configuração de segurança web do Spring Security
public class SecurityConfig {

    // Dentro da classe SecurityConfig
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilitar CSRF para APIs RESTful (JWT lidará com isso)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Não criar sessões HTTP
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll() // Permite acesso aos endpoints de autenticação
                        .requestMatchers(
                                "/login.html", "/registrar.html", "/dashboard.html", // páginas HTML
                                "/css/**", "/js/**", "/assets/**", // arquivos estáticos
                                "/index.html", "/" // página inicial
                        ).permitAll()
                        .anyRequest().authenticated() // Qualquer outra requisição precisa de autenticação
                );
        // Configuração para JWT viria aqui depois (filtro de autenticação JWT)
        return http.build();
    }
}