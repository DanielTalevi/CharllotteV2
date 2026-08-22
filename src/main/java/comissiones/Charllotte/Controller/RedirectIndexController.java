package comissiones.Charllotte.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RedirectIndexController {
    @GetMapping("/")
    public String toLogin(){
        return "redirect:/login";
    }
}
