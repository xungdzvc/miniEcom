package com.web.dto.response.dashboard;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardSummaryResponse {

    private Integer totalProducts;
    private Integer totalCategories;
    private Integer totalUsers;
    private Integer activeProducts;
    private Integer inActiveProducts;

    private Long monthRevenue;
    private Long quarterRevenue;
    private Long yearRevenue;

    private Integer newUsersToday;
    private Integer newUsersThisMonth;

}
