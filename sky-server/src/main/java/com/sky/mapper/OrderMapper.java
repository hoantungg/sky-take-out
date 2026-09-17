package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);

    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    void update(Orders orders);

    Page<Orders> pageQuery(
            OrdersPageQueryDTO ordersPageQueryDTO
    );

    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    @Select("""
          select count(id)
          from orders
          where status = #{status}
          """)
    Integer countStatus(Integer status);

    List<Orders> getByStatusAndOrderTimeLT(
            @Param("pendingPayment")
            Integer pendingPayment,
            @Param("time")
            LocalDateTime time
    );

    void updateBatchStatus(
            @Param("ordersList")
            List<Orders> ordersList,
            @Param("status")
            Integer status
    );

    Integer countByMap(
            @Param("map")
            Map<String, Object> map
    );

    Double sumByMap(
            @Param("map")
            Map<String, Object> map
    );




}
