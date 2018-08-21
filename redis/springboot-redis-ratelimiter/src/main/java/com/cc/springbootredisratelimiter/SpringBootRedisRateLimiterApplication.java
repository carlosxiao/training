package com.cc.springbootredisratelimiter;

import com.cc.springbootredisratelimiter.annotation.RateLimiter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

@SpringBootApplication
@Slf4j
public class SpringBootRedisRateLimiterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRedisRateLimiterApplication.class, args);
    }

    @Resource
    private JedisPool jedisPool;

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void configurePathMatch(PathMatchConfigurer pathMatchConfigurer) {

            }

            @Override
            public void configureContentNegotiation(ContentNegotiationConfigurer contentNegotiationConfigurer) {

            }

            @Override
            public void configureAsyncSupport(AsyncSupportConfigurer asyncSupportConfigurer) {

            }

            @Override
            public void configureDefaultServletHandling(DefaultServletHandlerConfigurer defaultServletHandlerConfigurer) {

            }

            @Override
            public void addFormatters(FormatterRegistry formatterRegistry) {

            }

            @Override
            public void addInterceptors(InterceptorRegistry interceptorRegistry) {
                interceptorRegistry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
                        HandlerMethod handlerMethod = (HandlerMethod) o;
                        Method method = handlerMethod.getMethod();
                        RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);
                        if (null != rateLimiter) {
                            int limit = rateLimiter.limit();
                            int timeout = rateLimiter.timeout();
                            Jedis jedis = jedisPool.getResource();
                            String token = RedisRateLimiter.getTokenFromBucket(jedis, limit, timeout);
                            if (null == token) {
                                httpServletResponse.sendError(500);
                                return false;
                            }
                            log.info("token : {}", token);
                        }
                        return true;
                    }

                    @Override
                    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

                    }

                    @Override
                    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

                    }
                }).addPathPatterns("/*");
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {

            }

            @Override
            public void addCorsMappings(CorsRegistry corsRegistry) {

            }

            @Override
            public void addViewControllers(ViewControllerRegistry viewControllerRegistry) {

            }

            @Override
            public void configureViewResolvers(ViewResolverRegistry viewResolverRegistry) {

            }

            @Override
            public void addArgumentResolvers(List<HandlerMethodArgumentResolver> list) {

            }

            @Override
            public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> list) {

            }

            @Override
            public void configureMessageConverters(List<HttpMessageConverter<?>> list) {

            }

            @Override
            public void extendMessageConverters(List<HttpMessageConverter<?>> list) {

            }

            @Override
            public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> list) {

            }

            @Override
            public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> list) {

            }

            @Override
            public Validator getValidator() {
                return null;
            }

            @Override
            public MessageCodesResolver getMessageCodesResolver() {
                return null;
            }
        };
    }
}
