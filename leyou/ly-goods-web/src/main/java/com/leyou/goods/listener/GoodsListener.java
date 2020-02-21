package com.leyou.goods.listener;

import com.leyou.goods.service.IGoodsHtmlService;
import com.leyou.goods.service.impl.GoodsHtmlServiceImpl;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {

    @Autowired
    private IGoodsHtmlService goodsHtmlService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.create.web.queue",durable = "true"),
            exchange = @Exchange(
                    value = "leyou.item.exchange",
                    type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"),
            key = {"item.insert", "item.update"}
    ))
    public void listenCreate(Long id){
        if (id != null) {
            //新增或修改
            goodsHtmlService.createHtml(id);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.delete.web.queue", durable = "true"),
            exchange = @Exchange(
                    value = "leyou.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = "item.delete"))
    public void listenDelete(Long id) {
        if (id != null) {
            //删除
            goodsHtmlService.deleteHtml(id);
        }
    }
}
