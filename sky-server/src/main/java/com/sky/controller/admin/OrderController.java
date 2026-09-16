package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info(
                "管理端订单搜索：{}",
                ordersPageQueryDTO
        );
        PageResult pageResult =
                orderService.conditionSearch(
                        ordersPageQueryDTO
                );
        return Result.success(pageResult);
    }
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics() {
        OrderStatisticsVO orderStatisticsVO =
                orderService.statistics();
        return Result.success(
                orderStatisticsVO
        );
    }
    @GetMapping("/details/{id}")
    public Result<OrderVO> details(
            @PathVariable("id") Long id
    ) {
        log.info("管理端查询订单详情：{}", id);
        OrderVO orderVO =
                orderService.details(id);
        return Result.success(orderVO);
    }
    @PutMapping("/confirm")
    public Result confirm(
            @RequestBody
            OrdersConfirmDTO ordersConfirmDTO
    ) {
        log.info("商家接单：{}", ordersConfirmDTO);
        orderService.confirm(
                ordersConfirmDTO
        );
        return Result.success();
    }
    @PutMapping("/rejection")
    public Result rejection(
            @RequestBody
            OrdersRejectionDTO ordersRejectionDTO
    ) throws Exception {
        log.info("商家拒单：{}", ordersRejectionDTO);
        orderService.rejection(
                ordersRejectionDTO
        );
        return Result.success();
    }
    @PutMapping("/cancel")
    public Result cancel(
            @RequestBody
            OrdersCancelDTO ordersCancelDTO
    ) throws Exception {
        log.info("商家取消订单：{}", ordersCancelDTO);
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }
    @PutMapping("/delivery/{id}")
    public Result delivery(
            @PathVariable("id") Long id
    ) {
        log.info("派送订单：{}", id);
        orderService.delivery(id);
        return Result.success();
    }
    @PutMapping("/complete/{id}")
    public Result complete(
            @PathVariable("id") Long id
    ) {
        log.info("完成订单：{}", id);
        orderService.complete(id);
        return Result.success();
    }



}
