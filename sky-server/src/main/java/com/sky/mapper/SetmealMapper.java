package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface SetmealMapper {
    @AutoFill(OperationType.INSERT)
    void insertSetmeal(Setmeal setmeal);

    void insertBatchSetmealDish(List<SetmealDish> setmealDishes);

    Page<Setmeal> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getDishesBySetmealId(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    @Delete("delete from setmeal_dish " +
            "where setmeal_id = #{setmealId}")
    void deleteDishesBySetmealId(Long setmealId);

    ArrayList<Setmeal> getByIdBatch(ArrayList<Long> ids);

    void deleteDishesBySetmealIds(ArrayList<Long> ids);

    void deleteBatch(ArrayList<Long> ids);

    List<Setmeal> list(Setmeal setmeal);

    @Select(
            "select sd.name, sd.copies, " +
                    "d.image, d.description " +
                    "from setmeal_dish sd " +
                    "left join dish d " +
                    "on sd.dish_id = d.id " +
                    "where sd.setmeal_id = #{setmealId}"
    )
    List<DishItemVO> getDishItemBySetmealId(
            Long setmealId);

}
