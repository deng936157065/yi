package com.leyou.cart.interceptor;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.cart.config.JwtProperties;
import com.leyou.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 */
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtProperties props;

    //定义一个线程域，存放登录的对象
    private static final ThreadLocal<UserInfo> threadLocal= new ThreadLocal<>();

    public LoginInterceptor() {
        super();
    }

    public LoginInterceptor(JwtProperties props) {
        this.props = props;
    }

    /**
     *      * 在业务处理器处理请求之前被调用
     *      * 如果返回false
     *      *      则从当前的拦截器往回执行所有拦截器的afterCompletion(),再退出拦截器链
     *      * 如果返回true
     *      *      执行下一个拦截器，直到所有拦截器都执行完毕
     *      *      再执行被拦截的Controller
     *      *      然后进入拦截器链
     *      *      从最后一个拦截器往回执行所有的postHandle()
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //查询Token
        String token = CookieUtils.getCookieValue(request, "LY_TOKEN");
        if (StringUtils.isBlank(token)){
            // 未登录,返回401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        // 有token，查询用户信息
        try {
            UserInfo userInfo = JwtUtils.getUserInfo(props.getPublicKey(), token);
            // 放入线程域
            threadLocal.set(userInfo);
            return true;
        }catch (Exception e){
            // 抛出异常，证明未登录,返回401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }

    /**
     * 在业务处理器处理请求执行完成后，生成视图之前执行的动作
     * 可在modelAndView中加入数据，比如当前时间
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 在DispatcherServlet完全处理完请求后被调用,可用于清理资源等
     * 当有拦截器抛出异常时,会从当前拦截器往回执行所有的拦截器的afterCompletion()
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocal.remove();
    }
    //对外提供了静态的方法：getLoginUser()来获取User信息
    public static UserInfo getLoginUser() {
        return threadLocal.get();
    }

}
