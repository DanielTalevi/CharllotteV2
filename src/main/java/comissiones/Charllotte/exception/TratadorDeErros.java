package comissiones.Charllotte.exception;

import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(ErroDePermissao.class)
    public String tratarErroDePermissao(
            ErroDePermissao erro,
            Model model) {

        model.addAttribute("titulo", "Acesso negado");
        model.addAttribute("mensagem", erro.getMessage());
        model.addAttribute("codigo", "403");

        return "erro/403";
    }


    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public String tratarUsuarioNaoEncontrado(
            UsuarioNaoEncontradoException erro,
            Model model) {

        model.addAttribute("titulo", "Usuário não encontrado");
        model.addAttribute("mensagem", erro.getMessage());
        model.addAttribute("codigo", "404");

        return "erro/404";
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public String tratarPaginaNaoEncontrada(
            NoResourceFoundException erro,
            Model model) {

        model.addAttribute("titulo", "Página não encontrada");

        model.addAttribute(
                "mensagem",
                "A página que você está procurando não existe ou não está mais disponível."
        );

        model.addAttribute("codigo", "404");

        return "erro/404";
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String tratarMetodoNaoPermitido(
            HttpRequestMethodNotSupportedException erro,
            Model model) {

        model.addAttribute("titulo", "Método não permitido");
        model.addAttribute(
                "mensagem",
                "Esta ação não pode ser realizada dessa forma."
        );
        model.addAttribute("codigo", "405");

        return "erro/405";
    }
    @ExceptionHandler(Exception.class)
    public String tratarErroInterno(
            Exception erro,
            Model model) {

        model.addAttribute("titulo", "Erro interno");
        model.addAttribute(
                "mensagem",
                "Ocorreu um problema inesperado no sistema. Tente novamente mais tarde."
        );
        model.addAttribute("codigo", "500");

        return "erro/500";
    }

}