package love.lingbao.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class MyInterceptor implements HandlerInterceptor{

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        log.info("preHandle拦截的请求路径是{}",requestURI);

        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        /*String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)){
            throw new IOException("token为空");
        }*/
        Object userId = request.getSession().getAttribute("userId");
        Object adminId = request.getSession().getAttribute("adminId");
        //log.info("拦截器token={}，user={}",token,user);
        log.info("拦截器userId={}",userId);
        log.info("拦截器adminId={}",adminId);
        if (userId == null && adminId == null) {
            throw new IOException("用户未登录");
        }else {
            return true;
        }
    }

}

