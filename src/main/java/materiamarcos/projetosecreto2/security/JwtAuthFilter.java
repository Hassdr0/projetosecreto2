package materiamarcos.projetosecreto2.security; // Crie este pacote ou use o de config

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import materiamarcos.projetosecreto2.Config.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import materiamarcos.projetosecreto2.security.CustomUserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter { // Garante que o filtro seja executado apenas uma vez por requisição

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // Seu serviço que implementa UserDetailsService

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            // Verifica se o token existe e é válido
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Extrai o username do token
                String username = tokenProvider.getUsernameFromToken(jwt);

                // Carrega os detalhes do usuário (UserDetails) a partir do username
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                // Cria um objeto de autenticação
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, // O principal (usuário)
                        null,        // Credenciais (não são necessárias aqui, pois já validamos o token)
                        userDetails.getAuthorities() // As permissões/roles do usuário
                );

                // Define detalhes da requisição web no objeto de autenticação
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Define o usuário autenticado no contexto de segurança do Spring
                // A partir daqui, o Spring Security considera o usuário como autenticado

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Não foi possível definir a autenticação do usuário no contexto de segurança", ex);
        }

        // Continua para o próximo filtro
        filterChain.doFilter(request, response);
    }

    // Método helper para extrair o token "Bearer" do cabeçalho Authorization

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Verifica se o cabeçalho existe e se começa com "Bearer "

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Retorna apenas token
        }
        return null;
    }
}