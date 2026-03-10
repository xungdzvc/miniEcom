package com.web.controller.user;

import com.web.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryPublicController {
    private final ICategoryService categoryService;
    @GetMapping()
    public ResponseEntity<?> getAllCategoriesUser(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}
