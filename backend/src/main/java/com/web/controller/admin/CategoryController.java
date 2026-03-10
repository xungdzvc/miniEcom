package com.web.controller.admin;

import com.web.dto.CategoryDTO;
import com.web.service.ICategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryController {
    @Autowired
    ICategoryService categoryService;

    @PostMapping()
    public ResponseEntity<?> addCategory(@RequestBody CategoryDTO categoryDTO){
        System.out.println(categoryDTO);
        return ResponseEntity.ok(categoryService.addCategory(categoryDTO));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryDTO categoryDTO
    ){
        return ResponseEntity.ok(categoryService.updateCategory(categoryId,categoryDTO));
    }


    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId){
        return ResponseEntity.ok(categoryService.removeCategory(categoryId));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long categoryId){
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }
    @GetMapping
    public ResponseEntity <?> getAllCategories(){

        return ResponseEntity.ok(categoryService.getAllCategories());
    }



}
