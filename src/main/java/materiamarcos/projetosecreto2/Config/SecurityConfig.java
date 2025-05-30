package materiamarcos.projetosecreto2.Config; // Verifique se este é o seu pacote correto

import materiamarcos.projetosecreto2.security.JwtAuthFilter; // Verifique o caminho para seu JwtAuthFilter
import org.springframework.beans.factory.annotation.Autowired;
// A importação do PathRequest não é mais necessária se estamos usando strings diretamente para os matchers comuns
// import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Para .disable() de forma moderna
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer; // Para frameOptions
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// Não precisamos mais de AntPathRequestMatcher aqui se passarmos strings diretamente ao securityMatcher principal

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @Order(1) // Esta cadeia de filtros terá prioridade para os caminhos especificados
    public SecurityFilterChain publicResourcesFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher( // Aplica esta configuração de segurança APENAS aos seguintes caminhos:
                        "/",
                        "/index.html",
                        "/login.html",
                        "/registrar.html",
                        "/dashboard.html", // Se o dashboard for público ou tiver sua própria lógica de redirecionamento
                        "/favicon.ico",
                        "/css/**",
                        "/static/js/**",
                        "/assets/**",
                        "/img/**" // Se você tiver uma pasta /img na raiz de static
                        // Adicione outros arquivos HTML públicos ou pastas de assets aqui
                )
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // Permite todas as requisições que bateram no securityMatcher acima
                )
                .requestCache(AbstractHttpConfigurer::disable) // Desabilita o cache de requisições
                .securityContext(AbstractHttpConfigurer::disable) // Não cria contexto de segurança para esses
                .sessionManagement(AbstractHttpConfigurer::disable) // Não cria sessão para esses
                .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF para esta cadeia
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)); // Útil se usar H2 console

        return http.build();
    }

    @Bean
    @Order(2) // Esta cadeia será avaliada depois da de ordem 1
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                // Aplica esta configuração de segurança APENAS a caminhos que começam com /api/
                // Qualquer caminho não coberto pelo securityMatcher da cadeia @Order(1) ou este aqui,
                // se não houver outras cadeias, pode ser bloqueado por padrão ou necessitar de outra cadeia.
                // No entanto, a ideia é que a cadeia @Order(3) ou uma default pegaria o resto.
                // Para garantir que SÓ /api/** seja tratado aqui:
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll() // Endpoints de autenticação públicos dentro de /api/
                        .anyRequest().authenticated() // Todas as outras requisições DENTRO DE /api/** exigem autenticação
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Poderia ser necessário um filtro default para pegar o que não foi coberto pelas cadeias com @Order,
    // mas o .anyRequest().authenticated() na última cadeia de API efetivamente faz isso para caminhos de API.
    // Para caminhos não-API e não-estáticos que não foram explicitamente permitidos,
    // o comportamento padrão do Spring Security (se nenhuma outra cadeia os pegar) seria negar,
    // ou se você tivesse uma terceira cadeia com .anyRequest().authenticated().
    // No entanto, com as duas cadeias acima, a intenção é:
    // 1. Liberar estáticos e HTMLs públicos.
    // 2. Aplicar segurança JWT para /api/** (com /api/auth/** público dentro disso).
    // Se uma URL não bate em nenhum securityMatcher, ela não é processada por essas cadeias.
    // Se você quiser que *todas* as outras requisições não listadas sejam autenticadas,
    // você pode adicionar uma terceira cadeia com @Order mais baixo e um securityMatcher mais genérico
    // ou ajustar a cadeia `apiFilterChain` para não ter `securityMatcher` e depender apenas da ordem
    // e do `authorizeHttpRequests` para cobrir tudo que não foi pego pela `publicResourcesFilterChain`.
    // A configuração atual com dois SecurityFilterChains bem definidos com securityMatcher é uma boa abordagem.
}