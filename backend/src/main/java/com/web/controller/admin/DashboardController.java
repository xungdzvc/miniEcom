package com.web.controller.admin;

import com.web.dto.request.user.ChangeStatusRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.dto.response.dashboard.DashboardSummaryResponse;
import com.web.service.ICategoryService;
import com.web.service.IProductService;
import com.web.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/home")
@RequiredArgsConstructor
public class DashboardController {

    private final IUserService userService;
    private final IProductService productService;
    private final ICategoryService categoryService;

    @GetMapping
    public ApiResponse<?> getSummary(){
        DashboardSummaryResponse dashboardSummaryResponse = new DashboardSummaryResponse();
        dashboardSummaryResponse.setProductsCount(productService.getCountTotal());
        dashboardSummaryResponse.setTotalProductActive(productService.getCountProductActive());
        dashboardSummaryResponse.setTotalProductInActive(productService.getCountProductInActive());
        dashboardSummaryResponse.setCategoriesCount(categoryService.getCount());
        dashboardSummaryResponse.setUsersCount(userService.getCount());
        dashboardSummaryResponse.setTotalClickToWebsite(9999);
        return ApiResponse.success(dashboardSummaryResponse);
    }
    
    @PutMapping("/{id}/status")
    public void changeStatus(@PathVariable Long id,@RequestBody ChangeStatusRequest req){
         userService.changeStatus(id, req);
    }
}
