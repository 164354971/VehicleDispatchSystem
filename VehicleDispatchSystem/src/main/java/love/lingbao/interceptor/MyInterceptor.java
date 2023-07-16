package love.lingbao.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class MyInterceptor implements HandlerInterceptor{

    private final StringRedisTemplate stringRedisTemplate;

    public MyInterceptor(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    //进入controller之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        log.info("preHandle拦截的请求路径是{}",requestURI);

        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        Object userId = request.getSession().getAttribute("userId");
        Object adminId = request.getSession().getAttribute("adminId");
        log.info("拦截器userId = {}, adminId = {}", userId, adminId);
        if (userId == null && adminId == null) {
            //401访问未授权
            response.setStatus(401);
            return false;
        }
        return true;
    }

/*    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        //1 拦截路径
        log.info("preHandle拦截的请求路径是{}",requestURI);

        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String token = request.getHeader("authorization");
        log.info("拦截器token = {}",token);
        if (StringUtils.isEmpty(token)){
            //401访问未授权
            response.setStatus(401);
            throw new IOException("token为空");
        }

        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(RedisConstants.LOGIN_USER_KEY + token);
        if (userMap.isEmpty()){
            response.setStatus(401);
            throw new IOException("token不存在");
        }

        UserVo userVo = BeanUtil.fillBeanWithMap(userMap, new UserVo(), false);
        UserHolder.saveUser(userVo);
        //5 设置token有效期为7天
        stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY + token, RedisConstants.LOGIN_USER_TTL, TimeUnit.DAYS);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        //UserHolder.removeUser();
    }*/
}

