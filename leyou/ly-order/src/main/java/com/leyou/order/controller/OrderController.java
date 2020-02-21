package com.leyou.order.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.order.dto.OrderDto;
import com.leyou.order.pojo.Order;
import com.leyou.order.service.OrderService;
import com.leyou.order.service.PayLogService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author bystander
 * @date 2018/10/4
 */
@RestController
@RequestMapping("order")
@Api("订单服务接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PayLogService payLogService;

    /**
     * 创建订单
     *
     * @param orderDto 订单对象
     * @return 订单编号
     */
    @PostMapping
/*    @ApiOperation(value = "创建订单接口，返回订单编号", notes = "创建订单")
    @ApiImplicitParam(name = "order",required = true,value = "订单的json对象，包含订单条目和物流信息")*/
    public ResponseEntity<Long> createOrder(@RequestBody @Valid OrderDto orderDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderDto));

    }

    /**
     * 生成微信支付链接
     *
     * @param orderId
     * @return
     */
    @GetMapping("url/{id}")
/*    @ApiOperation(value = "生成微信扫描支付付款链接",notes = "生成付款链接")
    @ApiImplicitParam(name = "id",value = "订单编号",required = true, paramType = "path" ,dataType  = "Long")
    @ApiResponses({
            @ApiResponse(code = 200,message = "根据订单编号生成的微信支付地址"),
            @ApiResponse(code = 404,message = "生成链接失败"),
            @ApiResponse(code = 500,message = "服务器异常")
    })*/
    public ResponseEntity<String> generateUrl(@PathVariable("id") Long orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.generateUrl(orderId));
    }

    /**
     * 根据订单ID查询订单详情
     *
     * @param orderId
     * @return
     */
    @GetMapping("{id}")
    @ApiOperation(value = "根据订单编号查询订单，返回订单对象",notes = "查询订单")
    @ApiImplicitParam(name = "id",required = true,value = "订单编号",paramType = "path",dataType = "Long")
    public ResponseEntity<Order> queryOrderById(@PathVariable("id") Long orderId)  {
        return ResponseEntity.ok(orderService.queryById(orderId));
    }

    /**
     * 查询订单支付状态
     *
     * @param orderId
     * @return
     */
    @GetMapping("state/{id}")
/*    @ApiOperation(value = "查询扫码支付的付款状态",notes = "查询付款状态")
    @ApiImplicitParam(name = "id",value = "订单编号",type = "Long")
    @ApiResponses({
            @ApiResponse(code = 200, message = "0, 未查询到支付信息 1,支付成功 2,支付失败"),
            @ApiResponse(code = 500, message = "服务器异常"),
    })*/
    public ResponseEntity<Integer> queryOrderStateByOrderId(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(payLogService.queryOrderStateByOrderId(orderId));
    }

    /**
     * 分页查询所有订单
     *
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("list")
/*    @ApiOperation(value = "分页查询当前用户订单，并且可以根据订单状态过滤",
            notes = "分页查询当前用户订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页",
                    defaultValue = "1", type = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页大小",
                    defaultValue = "5", type = "Integer"),
            @ApiImplicitParam(
                    name = "status",
                    value = "订单状态：1未付款，2已付款未发货，3已发货未确认，4已确认未评价，5交易关闭，6交易成功，已评价", type = "Integer"),
    })*/
    public ResponseEntity<PageResult<Order>> queryOrderByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                              @RequestParam(value = "rows", defaultValue = "5") Integer rows) {
        return ResponseEntity.ok(orderService.queryOrderByPage(page, rows));
    }
}
