package com.leyou.user.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.service.IUserService;
import com.leyou.user.util.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    static final String KEY_PREFIX = "user:code:phone:";

    static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public Boolean checkData(String data, Integer type) {
        User user = new User();
        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_PARAM);
        }
        return (this.userMapper.selectCount(user) == 0);
    }

    public Boolean sendVerifyCode(String phone) {
       // 生成验证码
        String code = NumberUtils.generateCode(6);
        try {
            // 发送短信
            Map<String, String> msg = new HashMap<>();
            msg.put("phone", phone);
            msg.put("code", code);
            System.out.println(msg+"...............");
            amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", msg);
            // 将code存入redis
            this.redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            logger.error("发送短信失败。phone：{}， code：{}", phone, code);
            return false;
        }
    }

    public Boolean register(User user, String code) {
        //1）校验短信验证码
        String key = KEY_PREFIX+user.getPhone();
        // 从redis取出验证码
        String codeCache = this.redisTemplate.opsForValue().get(key);
        //检查验证码是否正确
        if (!code.equals(codeCache)){
            //不正确,返回
            return false;
        }
        user.setId(null);
        user.setCreated(new Date());
        //2）生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //3）对密码加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        //4）写入数据库
        boolean boo = this.userMapper.insertSelective(user)==1;
        //5）删除Redis中的验证码
        if (boo){
            try {
                this.redisTemplate.delete(key);
            }catch (Exception e){
                logger.error("删除缓存验证码失败，code：{}", code, e);
            }
        }
        return boo;
    }

    public User queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);

        //首先根据用户名查询用户
        User user = userMapper.selectOne(record);

        if (user == null) {
            throw new LyException(ExceptionEnum.USER_NOT_EXIST);
        }

        //检验密码是否正确
        if (!StringUtils.equals(CodecUtils.md5Hex(password, user.getSalt()), user.getPassword())) {
            //密码不正确
            throw new LyException(ExceptionEnum.PASSWORD_NOT_MATCHING);
        }

        return user;
    }
}
