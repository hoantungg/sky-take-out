package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result save(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }
    @GetMapping("/page")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分类分页查询：{}", categoryPageQueryDTO);
        PageResult pageResult =
                categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }
    @PutMapping
    public Result updateCategory(
            @RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类：{}", categoryDTO);
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    public Result startOrStop(
            @PathVariable Integer status,
            Long id) {
        log.info("启用禁用分类，status：{}，id：{}", status, id);
        categoryService.startOrStop(status, id);
        return Result.success();
    }
    @GetMapping("/list")
    public Result<List<Category>> query(Integer type) {
        log.info("根据类型查询分类：{}", type);
        List<Category> categories =
                categoryService.query(type);
        return Result.success(categories);
    }
    @DeleteMapping
    public Result deleteCategory(Long id) {
        log.info("删除分类：{}", id);
        categoryService.delete(id);
        return Result.success();
    }


}
