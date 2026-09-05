package PathshalaConnect.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);

        Long uid = (session != null) ? (Long) session.getAttribute("uid") : null;
        String role = (session != null) ? (String) session.getAttribute("role") : null;

        boolean isSchoolRoute = uri.startsWith("/school");
        boolean isDonorRoute = uri.startsWith("/donor") || uri.startsWith("/donate");
        boolean isAdminRoute = uri.startsWith("/admin");
        boolean isDashboardRoute = uri.equals("/dashboard") || uri.startsWith("/profile");

        if (isSchoolRoute || isDonorRoute || isAdminRoute || isDashboardRoute) {
            if (uid == null || role == null) {
                // Not logged in -> redirect to login with original destination
                response.sendRedirect("/login?msg=auth_required");
                return false;
            }

            // Role-based authorization
            if (isSchoolRoute && !"SCHOOL".equals(role)) {
                response.sendRedirect("/403");
                return false;
            }

            if (isDonorRoute && !"DONOR".equals(role)) {
                response.sendRedirect("/403");
                return false;
            }

            if (isAdminRoute && !"ADMIN".equals(role)) {
                response.sendRedirect("/403");
                return false;
            }
        }

        return true;
    }
}
