package com.leyou.cart.service;

import com.leyou.cart.pojo.Cart;

import java.util.List;

public interface ICartService {

    /**
     * 添加购物车
     * @param cart
     */
    void addCart(Cart cart);

    List<Cart> queryCartList();

    void updateNum(Long skuId, Integer num);

    void deleteCart(String skuId);

    void deleteCarts(List<Object> ids, Integer userId);
}
