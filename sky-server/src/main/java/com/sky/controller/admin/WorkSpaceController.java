package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/workspace")
public class WorkSpaceController {
    @Autowired
    private WorkspaceService workspaceService;
    @GetMapping("/businessData")
    public Result<BusinessDataVO> businessData(){
        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(begin, end);
        return Result.success(businessDataVO);
    }
    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> orderOverView() {
        OrderOverViewVO orderOverViewVO =
                workspaceService.getOrderOverView();
        return Result.success(orderOverViewVO);
    }
    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> dishOverView() {
        DishOverViewVO dishOverViewVO =
                workspaceService.getDishOverView();
        return Result.success(dishOverViewVO);
    }
    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO>
    setmealOverView() {
        SetmealOverViewVO setmealOverViewVO =
                workspaceService.getSetmealOverView();
        return Result.success(setmealOverViewVO);
    }
}
