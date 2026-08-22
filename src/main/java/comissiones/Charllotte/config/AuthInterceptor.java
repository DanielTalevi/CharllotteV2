package comissiones.Charllotte.config;

import comissiones.Charllotte.service.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        if (!AuthUtil.isLogado(session)) {
            response.sendRedirect("/login");
            return false; // bloqueia o acesso à rota
        }

        return true; // usuário logado, segue o fluxo normal
    }
}