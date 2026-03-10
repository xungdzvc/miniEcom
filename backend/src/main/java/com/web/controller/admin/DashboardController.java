package com.web.controller.admin;

import com.web.dto.request.user.ChangeStatusRequest;
import com.web.dto.response.common.ApiResponse;
import com.web.dto.response.dashboard.DashboardSummaryResponse;
import com.web.service.ICategoryService;
import com.web.service.IDashBoardSummaryService;
import com.web.service.IProductService;
import com.web.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/home")
@RequiredArgsConstructor
public class DashboardController {
    private final IDashBoardSummaryService dashBoardSummaryService;

    @GetMapping
    public ApiResponse<?> getSummary(){
        return ApiResponse.success(dashBoardSummaryService.getSummary());
    }
    
}
