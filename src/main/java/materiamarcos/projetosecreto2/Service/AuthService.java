package materiamarcos.projetosecreto2.Service;

import materiamarcos.projetosecreto2.Config.JwtTokenProvider;
import materiamarcos.projetosecreto2.DTOs.LoginRequestDTO;
import materiamarcos.projetosecreto2.DTOs.RegistroRequestDTO;
import materiamarcos.projetosecreto2.DTOs.UsuarioResponseDTO;
import materiamarcos.projetosecreto2.Model.Role;       // Import da entidade Role
import materiamarcos.projetosecreto2.Model.User;
import materiamarcos.projetosecreto2.Model.enums.ERole; // Import do Enum ERole
import materiamarcos.projetosecreto2.Repository.roleRepository;
import materiamarcos.projetosecreto2.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository usuarioRepository;
    @Autowired
    private roleRepository roleRepository; // Já está injetado, ótimo!
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Transactional
    public UsuarioResponseDTO registrarUsuario(RegistroRequestDTO registroRequestDTO) {
        if (usuarioRepository.existsByUsername(registroRequestDTO.getUsername())) {
            throw new IllegalArgumentException("Erro: Nome de usuário já está em uso!");
        }

        if (usuarioRepository.existsByEmail(registroRequestDTO.getEmail())) {
            throw new IllegalArgumentException("Erro: Email já está em uso!");
        }

        User novoUsuario = new User(
                registroRequestDTO.getUsername(),
                registroRequestDTO.getEmail(),
                passwordEncoder.encode(registroRequestDTO.getPassword())
        );

        // --- AJUSTE PARA ATRIBUIR PAPEL (ROLE) PADRÃO ---
        Set<Role> roles = new HashSet<>();
        Role clienteRole = roleRepository.findByNome(ERole.ROLE_CLIENTE)
                .orElseThrow(() -> new RuntimeException("Erro: Papel padrão 'ROLE_CLIENTE' não encontrado no banco de dados."));
        roles.add(clienteRole);
        novoUsuario.setRoles(roles);
        // --- FIM DO AJUSTE ---

        User usuarioSalvo = usuarioRepository.save(novoUsuario);

        return new UsuarioResponseDTO(usuarioSalvo.getId(), usuarioSalvo.getUsername(), usuarioSalvo.getEmail());
    }

    public String loginUsuarioERetornarToken(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getUsername(),
                        loginRequestDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication.getName());

        return jwt;
    }
}