/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.web.elastic.document.ProductDocument;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author ZZ
 */
@Service
@RequiredArgsConstructor
public class ProductElasticService {

    private final ElasticsearchClient client;

    public void save(ProductDocument doc) {
        try{
          client.index(i -> i
                .index("products")
                .id(doc.getId().toString())
                .document(doc)
        );  
        }catch(IOException e){
            e.printStackTrace();
        }
        
    }
}
