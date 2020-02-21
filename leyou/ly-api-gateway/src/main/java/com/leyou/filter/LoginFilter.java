package com.leyou.filter;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.properties.FilterProperties;
import com.leyou.properties.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
@EnableConfigurationProperties({JwtProperties.class,FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties properties;

    @Autowired
    private FilterProperties filterProperties;


    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 5;
    }

    @Override
    public boolean shouldFilter() {
        //获取上下文
        RequestContext context = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = context.getRequest();
        //获取路径
        String requestURI = request.getRequestURI();
        System.out.println(requestURI+"requestURI................");
        //判断白名单
        return !isAllowPath(requestURI);
    }

    private boolean isAllowPath(String requestURI) {
        //定义一个标记
        boolean flag = false;
        for (String path : this.filterProperties.getAllowPaths()){
            if (requestURI.startsWith(path)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    @Override
    public Object run() {
        //1.获取上下文
        RequestContext context = RequestContext.getCurrentContext();
        //2.获取request
        HttpServletRequest request = context.getRequest();
        //3.获取token
        String token = CookieUtils.getCookieValue(request,this.properties.getCookieName());
        //4.校验
        try{
            //4.1 校验通过，放行
            JwtUtils.getUserInfo(this.properties.getPublicKey(),token);
            //todo 如果做权限管理的话，在这做权限检验
        }catch (Exception e){
            //4.2 校验不通过，返回403
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.FORBIDDEN.value());

        }
        return null;
    }
}

