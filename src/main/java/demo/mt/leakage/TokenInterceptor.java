package demo.mt.leakage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Order(1)
@Component
public class TokenInterceptor implements HandlerInterceptor {

    public TokenInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Autowired
    private JwtUtils jwtUtils;

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("preHandle {}...", request.getRequestURI());
        String token = request.getHeader("token");
        try {
            String userId = jwtUtils.getClaimByToken(token).getSubject();
            logger.info("user is {}",userId);
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
        return true;

//        if (request.getParameter("debug") != null) {
//            PrintWriter pw = response.getWriter();
//            pw.write("<p>DEBUG MODE</p>");
//            pw.flush();
//            return false;
//        }
    }
}
