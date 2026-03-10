/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.analysis.TokenChar;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author ZZ
 */
@Service
@RequiredArgsConstructor
public class ElasticIndexService {

    private final ElasticsearchClient client;

    public void createProductIndex() throws IOException {
    client.indices().create(c -> c
        .index("products")
        .mappings(m -> m
            .properties("name", p -> p.searchAsYouType(s -> s)) // ⭐ Quan trọng nhất
            .properties("categoryName", p -> p.searchAsYouType(s -> s))
            .properties("slug", p -> p.keyword(k -> k))
            .properties("price", p -> p.long_(d -> d))
        )
    );
}
}
