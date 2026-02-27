/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import java.io.IOException;
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
                        .properties("name", p -> p.text(t -> t))
                        .properties("slug", p -> p.text(t -> t))
                        .properties("categoryName", p -> p.text(t -> t))
                        .properties("price", p -> p.long_(d -> d))
                )
        );
    }
}
