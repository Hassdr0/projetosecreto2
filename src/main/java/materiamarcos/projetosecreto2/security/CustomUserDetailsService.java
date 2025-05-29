package materiamarcos.projetosecreto2.security;

import materiamarcos.projetosecreto2.Model.User;
import materiamarcos.projetosecreto2.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca o usuário no seu banco de dados pelo nome de usuário
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o nome: " + username));

        // Cria um objeto UserDetails
        // O Spring Security usará isso para comparar
        // A senha retornada aqui DEVE ser a senha criptografada que está no banco
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // A senha já deve estar criptografada no seu objeto User vindo do banco
                new ArrayList<>() // Lista de GrantedAuthority (permissões/roles), vazia.
                // Futuramente,as roles do usuário.
        );
    }
}