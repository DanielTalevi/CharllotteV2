package comissiones.Charllotte.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectIndexController {
    @GetMapping("/")
    public String toLogin(){
        return "redirect:/login";
    }

    @GetMapping("/teste-500")
    public String testarErro500() {
        throw new RuntimeException("Erro proposital para teste");
    }
}

