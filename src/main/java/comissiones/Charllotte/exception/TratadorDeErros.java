package comissiones.Charllotte.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(ErroDePermissao.class)
    public String tratarErroDePermissao(
            ErroDePermissao erro,
            Model model) {

        model.addAttribute(
                "mensagem",
                erro.getMessage()
        );

        return "erro/acesso-negado";
    }
}