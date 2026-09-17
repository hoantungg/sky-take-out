package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
public class ReportController {
    @Autowired
    private ReportService reportService;
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(
            @RequestParam("begin")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,

            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end
    ){
        log.info("营业额统计：{}, {}",begin,end);
        return reportService.getTurnoverStatistics(begin,end);
    }

    @GetMapping("/userStatistics")
    public Result<UserReportVO>
            userStatistics(
            @RequestParam("begin")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,

            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end
    ){
        log.info("用户统计：{}, {}",begin,end);
        UserReportVO userReportVO = reportService.userStatistics(begin, end);
        return Result.success(userReportVO);
    }
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> orderStatistics(
            @RequestParam("begin")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,

            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end
    ){
        log.info("订单统计：{}，{}",begin,end);
        OrderReportVO orderReportVO = reportService.orderStatistics(begin, end);
        return Result.success(orderReportVO);
    }
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO>
    salesTop10(
            @RequestParam("begin")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate begin,

            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate end
    ){
        log.info("销量排名前十：{}，{}",begin,end);
        SalesTop10ReportVO salesTop10ReportVO = reportService.salesTop10(begin, end);
        return Result.success(salesTop10ReportVO);
    }
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        log.info("导出运营数据报表");
        reportService.exportBusinessData(response);
    }





}
















