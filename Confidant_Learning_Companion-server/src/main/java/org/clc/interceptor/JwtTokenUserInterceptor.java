package org.clc.interceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.clc.constant.JwtClaimsConstant;
import org.clc.constant.MessageConstant;
import org.clc.context.BaseContext;
import org.clc.exception.JwtAuthenticationException;
import org.clc.properties.JwtProperties;
import org.clc.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());

        //2、校验令牌
        try {
            log.info("jwt校验:"+token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            String userId = String.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前用户的id："+userId);
            BaseContext.setCurrentId(userId);
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            throw new JwtAuthenticationException(MessageConstant.NEED_USER_PERMISSIONS);
        }
    }
    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler){
        BaseContext.removeCurrentId();
    }
}
