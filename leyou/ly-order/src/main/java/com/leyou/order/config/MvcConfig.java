package com.leyou.order.config;

import com.leyou.order.filter.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bystander
 * @date 2018/10/3
 */
//@Configuration
//@EnableConfigurationProperties(JwtProperties.class)
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtProperties props;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        List<String> excludePath = new ArrayList<>();
        excludePath.add("/swagger-ui.html");
        excludePath.add("/swagger-resources/**");
        excludePath.add("/webjars/**");

        //配置登录拦截器
        registry.addInterceptor(new LoginInterceptor(props)).addPathPatterns("/**").excludePathPatterns(excludePath);
    }
}
