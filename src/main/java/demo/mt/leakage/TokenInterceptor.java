package demo.mt.leakage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

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
        if (isStaticResource(request)) {
            return true;
        }
        String token = request.getParameter("token");
        if (StringUtils.isBlank(token)) {
            token = request.getHeader("token");
        }
        if (StringUtils.isBlank(token)) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                }
            }
        }
        logger.info(token);
        try {
            String userId = jwtUtils.getClaimByToken(token).getSubject();
            logger.info("user is {}", userId);
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     *      * 判断是否请求的静态资源
     *      * TODO 待补充
     *      * @param request
     *      * @return
     *     
     */
    public boolean isStaticResource(HttpServletRequest request) {
        String uri = request.getRequestURI();
        List<String> statics=Arrays.asList(".css",".js",".png",".jpg",".jpeg",".svg",".woff",".woff2",".ttf");
        for(String str:statics){
            if(uri.lastIndexOf(str)>-1){
                return true;
            }
        }
        return false;

    }
}
