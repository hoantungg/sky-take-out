package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.OrderDetail;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrderService {
    OrderSubmitVO submit(
            OrdersSubmitDTO ordersSubmitDTO
    );

    OrderPaymentVO payment(
            OrdersPaymentDTO ordersPaymentDTO
    ) throws Exception;

    void paySuccess(String orderNumber);

    PageResult pageQuery4User(
            int page,
            int pageSize,
            Integer status
    );

    OrderVO details(Long id);

    void userCancelById(Long id) throws Exception;

    void repetition(Long id);

    void reminder(Long id);


    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO statistics();

    void confirm(
            OrdersConfirmDTO ordersConfirmDTO
    );

    void rejection(
            OrdersRejectionDTO ordersRejectionDTO
    ) throws Exception;

    void cancel(
            OrdersCancelDTO ordersCancelDTO
    ) throws Exception;

    void delivery(Long id);

    void complete(Long id);


}
