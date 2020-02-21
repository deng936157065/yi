package com.leyou.cart.service.impl;

import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.ICartService;
import com.leyou.common.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements ICartService {


    @Autowired
    private StringRedisTemplate redisTemplate;

    static final String KEY_PREFIX = "ly:cart:uid:";

    static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    /**
     * 添加购物车到Redis中
     *
     * @param cart
     */
    public void addCart(Cart cart) {
        //获取用户信息
        UserInfo loginUser = LoginInterceptor.getLoginUser();

        String key = KEY_PREFIX + loginUser.getId();

        //获取商品ID
        String hashKey = cart.getSkuId().toString();

        //获取数量
        Integer num = cart.getNum();

        //获取hash操作的对象
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);

        if (hashOps.hasKey(hashKey)) {
            //Redis中有该商品，修改数量
            cart = JsonUtils.toBean(hashOps.get(hashKey).toString(), Cart.class);
            cart.setNum(num + cart.getNum());
        }
        //存入Redis中
        hashOps.put(hashKey, JsonUtils.toString(cart));
    }

    public List<Cart> queryCartList() {
        // 获取登录用户
        UserInfo user = LoginInterceptor.getLoginUser();

        // 判断是否存在购物车
        String key = KEY_PREFIX + user.getId();
        if(!this.redisTemplate.hasKey(key)){
            // 不存在，直接返回
            return null;
        }
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        List<Object> carts = hashOps.values();
        // 判断是否有数据
        if(CollectionUtils.isEmpty(carts)){
            return null;
        }
        // 查询购物车数据
        return carts.stream().map(o -> JsonUtils.toBean(o.toString(), Cart.class)).collect(Collectors.toList());
    }

    public void updateNum(Long skuId, Integer num) {
        // 获取登录用户
        UserInfo user = LoginInterceptor.getLoginUser();
        String key = KEY_PREFIX + user.getId();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        // 获取购物车
        String json = hashOps.get(skuId.toString()).toString();
        Cart cart = JsonUtils.toBean(json, Cart.class);
        cart.setNum(num);
        // 写入购物车
        hashOps.put(skuId.toString(), JsonUtils.toString(cart));
    }

    public void deleteCart(String skuId) {
        // 获取登录用户
        UserInfo user = LoginInterceptor.getLoginUser();
        String key = KEY_PREFIX + user.getId();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        hashOps.delete(skuId);
    }

    @Transactional
    public void deleteCarts(List<Object> ids, Integer userId) {
        String key = KEY_PREFIX + userId;
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);


        for (Object id : ids) {
            hashOps.delete(id.toString());
        }
    }

}
