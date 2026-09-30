package com.web.dto.response.dashboard;

import java.math.BigDecimal;
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

    private BigDecimal monthRevenue;
    private BigDecimal quarterRevenue;
    private BigDecimal yearRevenue;

    private Integer newUsersToday;
    private Integer newUsersThisMonth;

}
