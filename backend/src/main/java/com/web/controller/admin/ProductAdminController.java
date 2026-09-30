package com.web.controller.admin;

import com.web.dto.request.product.ProductChangePinStatus;
import com.web.dto.request.product.ProductCreateOrUpdateRequest;
import com.web.dto.request.product.ProductChangeStatus;
import com.web.dto.response.common.ApiResponse;
import com.web.service.IProductService;
import com.web.service.elastic.ProductElasticService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        return ApiResponse.success(productService.addOrUpdateProduct(product, null), "Thêm sản phẩm thành công");
    }

    @PostMapping("/elastic")
    public ResponseEntity<String> syncAll() {
        String message = productElasticService.fullReIndex();
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/{id}")
    public ApiResponse<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductCreateOrUpdateRequest product) {
        return ApiResponse.success(productService.addOrUpdateProduct(product, id), "Cập nhật sản phẩm thành công");
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<?> changeStatusById(@PathVariable Long id, @Valid @RequestBody ProductChangeStatus productChangeStatus) {
        productService.changeStatusProduct(id, productChangeStatus.getStatus());
        return ApiResponse.success(null, "Thay đổi trạng thái thành công");
    }

    @GetMapping
    public ApiResponse<?> getProducts(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<?> result = productService.getProductsForAdmin(pageable);
        return ApiResponse.success(result, "Lấy danh sách sản phẩm thành công");
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getProductById(@PathVariable Long id) {
        return ApiResponse.success(productService.getProductForAdmin(id), "Lấy thông tin sản phẩm thành công");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success(null, "Xoá sản phẩm thành công");
    }

    @PutMapping("/{id}/pin")
    public ApiResponse<?> changePinStatus(@PathVariable Long id, @Valid @RequestBody ProductChangePinStatus productChangePinStatus) {
        productService.changePinStatusProduct(id, productChangePinStatus.getStatus());
        return ApiResponse.success(null, "Thay đổi trạng thái ghim thành công");
    }

}
