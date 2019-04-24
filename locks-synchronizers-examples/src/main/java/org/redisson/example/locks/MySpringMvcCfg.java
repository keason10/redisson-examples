package org.redisson.example.locks;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.ContentCachingRequestWrapper;
import sun.security.provider.MD5;
import sun.security.rsa.RSASignature;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebMvc
public class MySpringMvcCfg implements WebMvcConfigurer {
    @Autowired
    private RedissonClient redisson;
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new HandlerInterceptor() {
                @Override
                public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                    HandlerMethod method = (HandlerMethod) handler;
                    if (method.hasMethodAnnotation(PreventingDuplicateSubmissions.class)) {
                        PreventingDuplicateSubmissions preventingDuplicateSubmissions = method.getMethodAnnotation(PreventingDuplicateSubmissions.class);
                        String submitKey = preventingDuplicateSubmissions.submitedKey();
                        redisson.getLock(submitKey +RSASignature.MD5withRSA.);
                    }
                }
            });
        }
}
