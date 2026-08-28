package com.sky.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrdersPageQueryDTO implements Serializable {

    private int page;              // 第几页
    private int pageSize;          // 每页几条
    private String number;         // 按订单号搜索
    private String phone;          // 按手机号搜索
    private Integer status;        // 按状态筛选

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;  // 开始时间

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;    // 结束时间

    private Long userId;           // 哪个用户的订单

}