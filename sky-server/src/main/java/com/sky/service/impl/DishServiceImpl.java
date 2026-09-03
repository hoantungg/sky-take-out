package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Override
    @Transactional
    public void saveWithFlavor (DishDTO dishDTO){
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dish.setStatus(StatusConstant.ENABLE);
        dishMapper.save(dish);

        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if (flavors != null && !flavors.isEmpty()){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            dishMapper.insertBatchFlavors(flavors);
        }
    }
    @Override
    public PageResult pageQuery(
            DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(
                dishPageQueryDTO.getPage(),
                dishPageQueryDTO.getPageSize()
        );
        Page<Dish> page =
                dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(
                page.getTotal(),
                page.getResult()
        );
    }
    @Override
    public DishVO getById(Long id) {
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(id);
        ArrayList<Dish> dishes =
                dishMapper.getByIdBatch(ids);
        Dish dish = dishes.get(0);
        ArrayList<DishFlavor> flavors =
                dishMapper.getFlavorById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }
    @Override
    @Transactional
    public void updateDish (DishDTO dishDTO){
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.updateDish(dish);
        dishMapper.deleteFlavorByDishId(dish.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dish.getId()));
            dishMapper.insertBatchFlavors(flavors);
        }
    }
    @Override
    public void startOrStop(Long id, Integer status) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.updateDish(dish);
    }
    @Override
    @Transactional
    public void deleteBatch(ArrayList<Long> ids){
        ArrayList<Dish> dishes =
                dishMapper.getByIdBatch(ids);
        for (Dish dish : dishes){
            if (dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        Integer count =
                dishMapper.countMealDish(ids);
        if (count > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        dishMapper.deleteFlavorBatchByDishIds(ids);
        dishMapper.deleteBatch(ids);
    }
    @Override
    public ArrayList<Dish> getByCategoryId(Long categoryId){
        return dishMapper.getByCategoryId(categoryId, null);
    }
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        ArrayList<Dish> dishes =
                dishMapper.getByCategoryId(
                        dish.getCategoryId(),
                        dish.getStatus()
                );
        ArrayList<DishVO> dishVOList = new ArrayList<>();
        for (Dish currentDish : dishes){
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(currentDish,dishVO);
            ArrayList<DishFlavor> flavors = dishMapper.getFlavorById(currentDish.getId());
            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }
}










