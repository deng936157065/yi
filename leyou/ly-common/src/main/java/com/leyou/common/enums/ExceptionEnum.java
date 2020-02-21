package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {
    BRAND_NOT_FOUND(404, "品牌查询失败"),
    SPU_NOT_FOUND(404,"查询商品规格参数失败"),
    GOODS_NOT_FOUND(404,"查询sku失败"),
    SPEC_SPECIFICATION_NOT_FOUND(404,"查询规格分组失败"),
    SPEC_PARAM_NOT_FOUND(404,"查询querySpecParam出错"),
    INVALID_PARAM(400,"参数有误"),
    STOCK_NOT_FOUND(500,"STOCK_NOT_FOUND"),
    USERNAME_OR_PASSWORD_ERROR(500,"【授权中心】用户名和密码错误"),
    RECEIVER_ADDRESS_NOT_FOUND(404,"订单商品不存在"),
    ORDER_STATUS_EXCEPTION(404,"订单未付款"),
    CREATE_PAY_URL_ERROR(404,"订单路径错误"),
    ORDER_NOT_FOUND(500,"ORDER_NOT_FOUND"),
    WX_PAY_SIGN_INVALID(500,"【微信支付】检验签名失败"),
    WX_PAY_NOTIFY_PARAM_ERROR(500,"【微信支付回调】支付回调返回数据不正确"),
    STOCK_NOT_ENOUGH(500,"STOCK_NOT_ENOUGH"),
    SKU_NOT_FOUND(404,"SKU_NOT_FOUND"),
    USER_NOT_EXIST(500,"用户不存在"),
    PASSWORD_NOT_MATCHING(500,"密码不正确")
    ;

    int value;
    String message;

    public int value() {
        return this.value;
    }

    public String message() {
        return this.message;
    }
}
