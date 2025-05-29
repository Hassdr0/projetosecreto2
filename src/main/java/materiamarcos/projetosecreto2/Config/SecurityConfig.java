package materiamarcos.projetosecreto2.Config;

import materiamarcos.projetosecreto2.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Habilita a configuração de segurança web do Spring Security
public class SecurityConfig {

    @Autowired // precisamos dizer ao Spring Security para usar este JwtAuthFilter.
    // Ele deve ser adicionado à cadeia de filtros antes do filtro padrão de autenticação por username/password (UsernamePasswordAuthenticationFilter)
    // porque se o token JWT for válido, queremos que ele já autentique o usuário.
    private JwtAuthFilter jwtAuthFilter;

    @Bean // método como um produtor de bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}