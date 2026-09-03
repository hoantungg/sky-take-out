package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.ArrayList;
import java.util.List;

public interface DishService {
    void saveWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    DishVO getById(Long id);

    void updateDish(DishDTO dishDTO);

    void startOrStop(Long id, Integer status);

    void deleteBatch(ArrayList<Long> ids);

    ArrayList<Dish> getByCategoryId(Long categoryId);

    List<DishVO> listWithFlavor(Dish dish);

}
