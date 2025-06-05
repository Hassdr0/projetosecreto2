package materiamarcos.projetosecreto2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // Retorna 409 Conflict quando esta exceção é lançada
public class EstoqueInsuficienteException extends Exception {
    public EstoqueInsuficienteException(String message) {
        super(message);
    }
}