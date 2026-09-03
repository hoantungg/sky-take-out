package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    @Transactional
    public void insert(SetmealDTO setmealDTO){
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.insertSetmeal(setmeal);
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes =
                setmealDTO.getSetmealDishes();
        if (setmealDishes != null
                && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(setmealDish ->
                    setmealDish.setSetmealId(setmealId)
            );
            setmealMapper.insertBatchSetmealDish(
                    setmealDishes
            );
        }
    }
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        PageHelper.startPage(
                setmealPageQueryDTO.getPage(),
                setmealPageQueryDTO.getPageSize()
        );
        Page<Setmeal> page =
                setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(
                page.getTotal(),
                page.getResult()
        );
    }
    @Override
    public SetmealVO getById(Long id){
        Setmeal setmeal =
                setmealMapper.getById(id);
        List<SetmealDish> setmealDishes =
                setmealMapper.getDishesBySetmealId(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(
                setmeal,
                setmealVO
        );
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }
    @Override
    @Transactional
    public void update (SetmealDTO setmealDTO){
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        Long setmealId = setmealDTO.getId();
        setmealMapper.deleteDishesBySetmealId(setmealId);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()){
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
            setmealMapper.insertBatchSetmealDish(setmealDishes);
        }
    }
    @Override
    public void startOrStop(Long id,Integer status){
        Setmeal setmeal = new Setmeal();
        setmeal.setId(id);
        setmeal.setStatus(status);
        setmealMapper.update(setmeal);
    }
    @Override
    @Transactional
    public void deleteBatch(ArrayList<Long> ids){
        ArrayList<Setmeal> setmeals =
                setmealMapper.getByIdBatch(ids);
        for (Setmeal setmeal : setmeals){
            if(StatusConstant.ENABLE.equals(setmeal.getStatus())){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        setmealMapper.deleteDishesBySetmealIds(ids);
        setmealMapper.deleteBatch(ids);
    }

    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return setmealMapper.list(setmeal);
    }

    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }


}
