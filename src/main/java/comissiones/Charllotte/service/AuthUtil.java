package comissiones.Charllotte.service;

import jakarta.servlet.http.HttpSession;

public class AuthUtil {

    private static final String CHAVE_LOGADO = "logado";
    private static final String CHAVE_USUARIO = "usuarioLogado";

    // Chamar no login, depois de validar usuário e senha
    public static void marcarLogado(HttpSession session, String usuario) {
        session.setAttribute(CHAVE_LOGADO, true);
        session.setAttribute(CHAVE_USUARIO, usuario);
    }

    // Chamar sempre que precisar verificar se o usuário está logado
    public static boolean isLogado(HttpSession session) {
        if (session == null) {
            return false;
        }
        Object valor = session.getAttribute(CHAVE_LOGADO);
        return Boolean.TRUE.equals(valor);
    }

    // Retorna o nome/identificador do usuário logado, ou null se não houver
    public static String getUsuarioLogado(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(CHAVE_USUARIO);
    }

    // Chamar no logout, pra encerrar a sessão do usuário
    public static void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}