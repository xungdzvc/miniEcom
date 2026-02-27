package com.web.controller.admin;

import com.web.dto.request.product.ProductCreateOrUpdateRequest;
import com.web.dto.request.product.ProductChangeStatus;
import com.web.dto.response.common.ApiResponse;
import com.web.service.IProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.ResponseEntity.status;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/products")
public class ProductAdminController {

    @Autowired
    IProductService productService;

    @PostMapping()
    public ApiResponse<?> addProduct(@Valid @RequestBody ProductCreateOrUpdateRequest product) {
        return ApiResponse.success(productService.addOrUpdateProduct(product, null));
    }

    @PutMapping("/{id}")
    public ApiResponse<?> updateProduct(@PathVariable Long id, @RequestBody ProductCreateOrUpdateRequest product) {
        return ApiResponse.success(productService.addOrUpdateProduct(product, id));
    }
    
    @PutMapping("/change-status/{id}")
    public ApiResponse<?> changeStatusById(@PathVariable Long id, @RequestBody ProductChangeStatus productChangeStatus){
        return productService.changeStatusProduct(id,productChangeStatus.isStatus());
    }

    @GetMapping
    public ApiResponse<?> getProducts() {
        return ApiResponse.success(productService.getProductsForAdmin());
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getProductById(@PathVariable Long id) {
        return ApiResponse.success(productService.getProduct(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

}
