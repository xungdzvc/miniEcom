/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.web.elastic.document.ProductDocument;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author ZZ
 */
@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ElasticsearchClient client;

    public List<ProductDocument> search(String keyword, String sort) {
        try {
            SearchResponse<ProductDocument> response = client.search(s -> s
                    .index("products")
                    .query(q -> q
                    .multiMatch(mm -> mm
                    .query(keyword)
                    .fields("name", "categoryName")
                    .type(TextQueryType.BoolPrefix) // Hỗ trợ tìm kiếm tiền tố tốt hơn
                    )
                    )
                    .sort(st -> {
                        SortOrder order = "price_desc".equals(sort) ? SortOrder.Desc : SortOrder.Asc;
                        return st.field(f -> f.field("price").order(order));
                    }),
                    ProductDocument.class
            );

            return response.hits().hits().stream()
                    .map(h -> h.source())
                    .toList();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
