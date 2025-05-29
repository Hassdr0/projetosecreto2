package materiamarcos.projetosecreto2.Service;

import materiamarcos.projetosecreto2.Config.JwtTokenProvider;
import materiamarcos.projetosecreto2.DTOs.LoginRequestDTO;
import materiamarcos.projetosecreto2.DTOs.RegistroRequestDTO;
import materiamarcos.projetosecreto2.DTOs.UsuarioResponseDTO;
import materiamarcos.projetosecreto2.Model.User;
import materiamarcos.projetosecreto2.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

@Service
public class AuthService {

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injetar o bean que criamos

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Transactional // Garante que a operação seja atômica
    public UsuarioResponseDTO registrarUsuario(RegistroRequestDTO registroRequestDTO) {
        // 1. Verificar se username já existe
        if (usuarioRepository.existsByUsername(registroRequestDTO.getUsername())) {
            throw new RuntimeException("Erro: Nome de usuário já está em uso!");
            // Em uma aplicação real, crie exceções customizadas e trate-as no ControllerAdvice
        }

        // 2. Verificar se email já existe
        if (usuarioRepository.existsByEmail(registroRequestDTO.getEmail())) {
            throw new RuntimeException("Erro: Email já está em uso!");
        }

        // 3. Criar o novo usuário
        User usuario = new User();
        usuario.setUsername(registroRequestDTO.getUsername());
        usuario.setEmail(registroRequestDTO.getEmail());
        // 4. Criptografar a senha antes de salvar
        usuario.setPassword(passwordEncoder.encode(registroRequestDTO.getPassword()));

        // 5. Salvar o usuário no banco de dados
        User usuarioSalvo = usuarioRepository.save(usuario);

        // 6. Retornar um DTO com os dados do usuário (sem a senha)
        return new UsuarioResponseDTO(usuarioSalvo.getId(), usuarioSalvo.getUsername(), usuarioSalvo.getEmail());
    }

    public String loginUsuarioERetornarToken(LoginRequestDTO loginRequestDTO) {


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getUsername(),
                        loginRequestDTO.getPassword()
                )
        );

        // 2. Se a autenticação for bem-sucedida, definir a autenticação no contexto de segurança
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Gerar o token JWT
        // O 'authentication.getName()' geralmente retorna o username do usuário autenticado.
        String jwt = tokenProvider.generateToken(authentication.getName());

        return jwt; // Retorna o token
    }
}