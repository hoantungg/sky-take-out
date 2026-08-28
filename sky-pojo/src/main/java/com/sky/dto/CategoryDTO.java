package com.sky.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class CategoryDTO implements Serializable {

    private Long id;          // 分类ID（修改时需要）
    private Integer type;     // 类型 1 菜品分类 2 套餐分类
    private String name;      // 分类名称
    private Integer sort;     // 排序

}
