/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service.impl;

import com.web.dto.response.dashboard.DashboardSummaryResponse;
import com.web.service.ICategoryService;
import com.web.service.IDashBoardSummaryService;
import com.web.service.IOrderService;
import com.web.service.IProductService;
import com.web.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author ZZ
 */
@Service
@RequiredArgsConstructor
public class DashBoardSummaryService implements IDashBoardSummaryService{
    private final IProductService productService;
    private final ICategoryService categoryService;
    private final IUserService userService;
    private final IOrderService orderService;
    @Override
    public DashboardSummaryResponse getSummary() {
        DashboardSummaryResponse dashboardSummaryResponse = new DashboardSummaryResponse();
        dashboardSummaryResponse.setTotalProducts(productService.getCountTotal());
        dashboardSummaryResponse.setActiveProducts(productService.getCountProductActive());
        dashboardSummaryResponse.setInActiveProducts(productService.getCountProductInActive());
        dashboardSummaryResponse.setTotalCategories(categoryService.getCount());
        dashboardSummaryResponse.setTotalUsers(userService.getCount());
        
        dashboardSummaryResponse.setMonthRevenue(orderService.getMonthRevenue());
        dashboardSummaryResponse.setQuarterRevenue(orderService.getQuarterRevenue());
        dashboardSummaryResponse.setYearRevenue(orderService.getYearRevenue());
        
        dashboardSummaryResponse.setNewUsersToday(userService.countNewUsersToday());
        dashboardSummaryResponse.setNewUsersThisMonth(userService.countNewUsersMonth());
        
        return dashboardSummaryResponse;
    }                                   
    
    
    
}
