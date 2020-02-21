package com.leyou.auth.controller;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.properties.JwtProperties;
import com.leyou.auth.service.IAuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private IAuthService authService;

    @Autowired
    private JwtProperties prop;

    /**
     * 登录授权
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("accredit")
    public ResponseEntity<Void> authentication(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response) {
        // 登录校验
        String token = this.authService.authentication(username, password);
        if (StringUtils.isBlank(token)) {
            throw new LyException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        // 将token写入cookie,并指定httpOnly为true，防止通过JS获取和修改
        CookieUtils.newBuilder(response).httpOnly().maxAge(prop.getCookieMaxAge()).request(request).build(prop.getCookieName(), token);
        return ResponseEntity.ok().build();
    }

    /**
     * 验证用户信息
     * @param token
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(@CookieValue("LY_TOKEN")String token,HttpServletRequest request, HttpServletResponse response){
        try {
            //从Token中获取用户信息
            UserInfo userInfo = JwtUtils.getUserInfo(prop.getPublicKey(), token);
            //成功，刷新Token
            String newToken = JwtUtils.generateToken(userInfo, prop.getPrivateKey(), prop.getExpire());
            //将新的Token写入cookie中，并设置httpOnly
            CookieUtils.newBuilder(response).httpOnly().maxAge(prop.getCookieMaxAge()).request(request).build(prop.getCookieName(), newToken);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
            //Token无效
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        }
    }

    /**
     * 注销登录
     *
     * @param token
     * @param response
     * @return
     */
    @GetMapping("logout")
    public ResponseEntity<Void> logout(@CookieValue("LY_TOKEN") String token, HttpServletResponse response) {
        if (StringUtils.isNotBlank(token)) {
            CookieUtils.newBuilder(response).maxAge(0).build(prop.getCookieName(), token);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
