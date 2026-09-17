package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {
    @AutoFill(OperationType.INSERT)
    void save (Dish dish);

    void insertBatchFlavors(List<DishFlavor> flavors);

    Page<Dish> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    ArrayList<Dish> getByIdBatch(
            ArrayList<Long> ids);

    ArrayList<DishFlavor> getFlavorById(Long id);

    void deleteFlavorByDishId(Long dishId);

    @AutoFill(OperationType.UPDATE)
    void updateDish(Dish dish);

    Integer countMealDish(ArrayList<Long> ids);

    void deleteFlavorBatchByDishIds(ArrayList<Long> ids);

    void deleteBatch(ArrayList<Long> ids);

    ArrayList<Dish> getByCategoryId(
            @Param("categoryId") Long categoryId,
            @Param("status") Integer status
    );

    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    Integer countByMap(
            Map<String, Object> map
    );

}
