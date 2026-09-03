package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
public class DishController {
    private static final String DISH_CACHE_PREFIX = "dish_";
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId){
        log.info(
                "用户端根据分类查询菜品：{}",
                categoryId
        );
        String key = DISH_CACHE_PREFIX + categoryId;
        List<DishVO> dishVOList =
                (List<DishVO>) redisTemplate
                        .opsForValue()
                        .get(key);
        if (dishVOList != null){
            log.info("从Redis缓存中查询菜品");
            return Result.success(dishVOList);
        }
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        dishVOList = dishService.listWithFlavor(dish);
        redisTemplate.opsForValue().set(key,dishVOList);
        log.info("从数据库查询菜品并写入Redis");
        return Result.success(dishVOList);
    }
}
