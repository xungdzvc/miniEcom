package com.web.controller.admin;

import com.web.dto.request.product.ProductChangePinStatus;
import com.web.dto.request.product.ProductCreateOrUpdateRequest;
import com.web.dto.request.product.ProductChangeStatus;
import com.web.dto.response.common.ApiResponse;
import com.web.service.IProductService;
import com.web.service.elastic.ProductElasticService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductElasticService productElasticService;
    private final IProductService productService;

    @PostMapping()
    public ApiResponse<?> addProduct(@Valid @RequestBody ProductCreateOrUpdateRequest product) {
        return ApiResponse.success(productService.addOrUpdateProduct(product, null));
    }
    
    @PostMapping("/rebuild-elastic")
    public ResponseEntity<String> syncAll() {
        String message = productElasticService.fullReIndex();
        return ResponseEntity.ok(message);
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
    @PutMapping("/pin/{id}")
    public void changePinStatus(@PathVariable Long id,@RequestBody ProductChangePinStatus productChangePinStatus){
        productService.changePinStatusProduct(id, true);
        
    }

}
