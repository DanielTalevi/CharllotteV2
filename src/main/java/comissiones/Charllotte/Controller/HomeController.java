package comissiones.Charllotte.Controller;


import comissiones.Charllotte.Model.*;
import comissiones.Charllotte.service.AuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home"; // vai renderizar templates/home.html
    }

}
