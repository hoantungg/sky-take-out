package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }
    @GetMapping("/page")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult =
                dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }
    @GetMapping("/{id}")
    public Result<DishVO> getByDishId(
            @PathVariable Long id) {
        log.info("根据ID查询菜品：{}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }
    @PutMapping
    public Result updateDish(
            @RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        dishService.updateDish(dishDTO);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    public Result startOrStop(
            Long id,
            @PathVariable Integer status) {
        log.info("菜品起售停售，id：{}，status：{}", id, status);
        dishService.startOrStop(id, status);
        return Result.success();
    }
    @DeleteMapping
    public Result deleteBatch(@RequestParam ArrayList<Long> ids){
        log.info("批量删除菜品：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }
    @GetMapping("/list")
    public Result<ArrayList<Dish>> getByCategoryId(
            Long categoryId) {
        log.info("根据分类ID查询菜品：{}", categoryId);
        ArrayList<Dish> dishes =
                dishService.getByCategoryId(categoryId);
        return Result.success(dishes);
    }


}
