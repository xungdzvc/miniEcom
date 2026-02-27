package com.web.dto.response.dashboard;

import lombok.Data;

@Data
public class DashboardSummaryResponse {
    private int productsCount;
    private int categoriesCount;
    private int usersCount;
    private int totalProduct;
    private int totalProductActive;
    private int totalProductInActive;
    private int totalClickToWebsite;

}
