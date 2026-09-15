package com.sky.controller.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.properties.WeChatProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@RestController
@RequestMapping("/user/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private WeChatProperties weChatProperties;
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(
            @RequestBody OrdersSubmitDTO ordersSubmitDTO
    ) {
        log.info("用户提交订单：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO =
                orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }
    @PutMapping("/payment")
    public Result<OrderPaymentVO> payment(
            @RequestBody OrdersPaymentDTO ordersPaymentDTO
    ) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO =
                orderService.payment(ordersPaymentDTO);
        return Result.success(orderPaymentVO);
    }
    @GetMapping("/historyOrders")
    public Result<PageResult> page(
            int page,
            int pageSize,
            Integer status
    ) {
        log.info(
                "历史订单分页查询：page={}，pageSize={}，status={}",
                page,
                pageSize,
                status
        );
        PageResult pageResult =
                orderService.pageQuery4User(
                        page,
                        pageSize,
                        status
                );
        return Result.success(pageResult);
    }
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> details(
            @PathVariable("id") Long id
    ) {
        log.info("查询订单详情：{}", id);

        OrderVO orderVO =
                orderService.details(id);

        return Result.success(orderVO);
    }
    @PutMapping("/cancel/{id}")
    public Result cancel(
            @PathVariable("id") Long id
    ) throws Exception {
        log.info("用户取消订单：{}", id);
        orderService.userCancelById(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    public Result repetition(
            @PathVariable("id") Long id
    ) {
        log.info("再来一单，原订单ID：{}", id);
        orderService.repetition(id);
        return Result.success();
    }
    @GetMapping("/reminder/{id}")
    public Result reminder(
            @PathVariable("id") Long id
    ) {
        log.info("用户催单，订单ID：{}", id);
        orderService.reminder(id);
        return Result.success();
    }



}


