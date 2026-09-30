/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service.impl;

import com.web.dto.response.dashboard.DashboardSummaryResponse;
import com.web.repository.OrderRepository;
import com.web.service.ICategoryService;
import com.web.service.IDashBoardSummaryService;
import com.web.service.IProductService;
import com.web.service.IUserService;
import com.web.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author ZZ
 */
@Service
@RequiredArgsConstructor
public class DashBoardSummaryService implements IDashBoardSummaryService {

    private final IProductService productService;
    private final ICategoryService categoryService;
    private final IUserService userService;
    private final OrderRepository orderRepository;

    @Override
    public DashboardSummaryResponse getSummary() {
        DashboardSummaryResponse dashboardSummaryResponse = new DashboardSummaryResponse();
        dashboardSummaryResponse.setTotalProducts(productService.getCountTotal());
        dashboardSummaryResponse.setActiveProducts(productService.getCountProductActive());
        dashboardSummaryResponse.setInActiveProducts(productService.getCountProductInActive());
        dashboardSummaryResponse.setTotalCategories(categoryService.getCount());
        dashboardSummaryResponse.setTotalUsers(userService.getCount());

        dashboardSummaryResponse.setMonthRevenue(Utils.getMonthRevenue(orderRepository));
        dashboardSummaryResponse.setQuarterRevenue(Utils.getQuarterRevenue(orderRepository));
        dashboardSummaryResponse.setYearRevenue(Utils.getYearRevenue(orderRepository));

        dashboardSummaryResponse.setNewUsersToday(userService.countNewUsersToday());
        dashboardSummaryResponse.setNewUsersThisMonth(userService.countNewUsersMonth());

        return dashboardSummaryResponse;
    }

}
