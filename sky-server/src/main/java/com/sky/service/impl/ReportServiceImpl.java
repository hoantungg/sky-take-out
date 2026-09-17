package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private WorkspaceService workspaceService;

    @Override
    public Result<TurnoverReportVO> getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = begin;
        while (!currentDate.isAfter(end)){
            dateList.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList){
            HashMap<String, Object> map = new HashMap<>();
            map.put("status", Orders.COMPLETED);
            map.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            map.put("end",LocalDateTime.of(date,LocalTime.MAX));
            Double turnover = reportMapper.sumByMap(map);
            if (turnover == null){
                turnover = 0.0;
            }
            turnoverList.add(turnover);
        }
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
        return Result.success(turnoverReportVO  );
    }
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = begin;
        while(!currentDate.isAfter(end)){
            dateList.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateList){
            HashMap<String, Object> map = new HashMap<>();
            map.put("begin",LocalDateTime.of(date,LocalTime.MIN));
            map.put("end",LocalDateTime.of(date,LocalTime.MAX));
            Integer newUsers = reportMapper.countNewUsers(map);
            Integer totalUsers = reportMapper.countTotalUsers(map);
            newUserList.add(newUsers);
            totalUserList.add(totalUsers);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }
    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = begin;
        while(!currentDate.isAfter(end)){
            dateList.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        int totalOrderCount = 0;
        int validOrderCount = 0;
        for (LocalDate date : dateList) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("begin",LocalDateTime.of(date,LocalTime.MIN));
            map.put("end",LocalDateTime.of(date,LocalTime.MAX));
            map.put("status",Orders.COMPLETED);
            Integer orderCount = reportMapper.countOrders(map);
            Integer validCount = reportMapper.countValidOrders(map);
            orderCountList.add(orderCount);
            validOrderCountList.add(validCount);
            totalOrderCount += orderCount;
            validOrderCount += validCount;
        }
        double orderCompletionRate = 0.0;
        if (totalOrderCount != 0){
            orderCompletionRate = (double) validOrderCount / totalOrderCount;
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList , ","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }
    @Override
    public SalesTop10ReportVO salesTop10(LocalDate begin, LocalDate end) {
        Map<String, Object> map = new HashMap<>();
        map.put("begin",LocalDateTime.of(begin,LocalTime.MIN));
        map.put("end",LocalDateTime.of(end,LocalTime.MAX));
        map.put("status",Orders.COMPLETED);
        List<Map<String, Object>> result = reportMapper.salesTop10(map);
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for (Map<String,Object> row : result){
            nameList.add(row.get("name").toString());
            Number number = (Number)row.get("number");
            numberList.add(number.intValue());
        }
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
    }
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        // 1. 统计截至昨天的最近30个完整自然日
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 2. 查询30天整体的运营数据，用于填写报表上方的数据概览
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(beginTime, endTime);

        // 3. 从classpath读取Excel模板
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream("template/运营数据报表模板.xlsx");
        if (inputStream == null) {
            throw new RuntimeException("未找到运营数据报表模板");
        }
        try (
                inputStream;
                XSSFWorkbook excel = new XSSFWorkbook(inputStream)
        ) {
            // 4. 获取工作表并填写报表日期范围
            XSSFSheet sheet = excel.getSheet("Sheet1");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
            sheet.getRow(1).getCell(1).setCellValue(
                    begin.format(formatter) + " 至 " + end.format(formatter)
            );
            // 5. 填写30天营业数据概览
            XSSFRow row4 = sheet.getRow(3);
            row4.getCell(2).setCellValue(businessDataVO.getTurnover());
            row4.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row4.getCell(6).setCellValue(businessDataVO.getNewUsers());

            XSSFRow row5 = sheet.getRow(4);
            row5.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row5.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            // 6. 逐日查询并填写30行运营明细
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                LocalDateTime dayBegin = LocalDateTime.of(date, LocalTime.MIN);
                LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

                BusinessDataVO dayData = workspaceService.getBusinessData(dayBegin, dayEnd);
                XSSFRow row = sheet.getRow(7 + i);

                row.getCell(1).setCellValue(date.format(formatter));
                row.getCell(2).setCellValue(dayData.getTurnover());
                row.getCell(3).setCellValue(dayData.getValidOrderCount());
                row.getCell(4).setCellValue(dayData.getOrderCompletionRate());
                row.getCell(5).setCellValue(dayData.getUnitPrice());
                row.getCell(6).setCellValue(dayData.getNewUsers());
            }

            // 7. 将内存中的Excel文件写入HTTP响应，交给浏览器下载
            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=business_data.xlsx"
            );

            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            outputStream.flush();

        } catch (IOException e) {
            throw new RuntimeException("导出运营数据报表失败", e);
        }
    }


}
