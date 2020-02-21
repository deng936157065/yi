package com.leyou.auth.service.impl;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.properties.JwtProperties;
import com.leyou.auth.service.IAuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.user.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties props;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    public String authentication(String username, String password) {
        try {
            User user = userClient.queryUser(username, password);
            if (user == null) {
                return null;
            }
            UserInfo userInfo = new UserInfo(user.getId(), user.getUsername());
            //生成Token
            String token = JwtUtils.generateToken(userInfo, props.getPrivateKey(), props.getExpire());
            return token;
        } catch (Exception e) {
            LOGGER.error("【授权中心】用户名和密码错误，用户名：{}", username,e);
            throw new LyException(ExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
        }
    }
}
