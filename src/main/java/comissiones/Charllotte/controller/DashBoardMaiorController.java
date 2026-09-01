package comissiones.Charllotte.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DashBoardMaiorController {

    @GetMapping("/home")
    public String mostrarDashMenor(){ //mostra o menu de usuario
        return "HomeMenor";
    }

    @GetMapping("/HM")
    public String mostrarDashMaior (){ // mostra o menu de usuario ADM ou RH
        return "HomeMaior";
    }
}
