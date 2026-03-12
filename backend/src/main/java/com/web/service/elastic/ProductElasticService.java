package com.web.service.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.web.elastic.document.ProductDocument;
import com.web.entity.ProductEntity;
import com.web.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductElasticService {

    private final ElasticsearchClient client;
    private final ProductRepository productRepository;
    private final ElasticIndexService elasticIndexService;
    private static final String INDEX = "products";

    private ProductDocument map(ProductEntity p) {
        ProductDocument doc = new ProductDocument();
        doc.setId(p.getId());
        doc.setName(p.getName());
        doc.setCategoryName(p.getCategory().getName());
        doc.setPrice(p.getPrice());
        doc.setSlug(p.getSlug());
        doc.setThumbnail(p.getThumbnail());
        return doc;
    }

    public void indexProduct(ProductEntity p) {

        ProductDocument doc = map(p);
        try {
            client.index(i -> i
                    .index(INDEX)
                    .id(p.getId().toString())
                    .document(doc)
            );
        } catch (IOException e) {

        }
    }

    public void updateProduct(ProductEntity p) {

        ProductDocument doc = map(p);

        try {
            client.index(i -> i
                    .index(INDEX)
                    .id(p.getId().toString())
                    .document(doc)
            );
        } catch (IOException e) {

        }
    }

    public void deleteProduct(Long id) {
        try {
            client.delete(d -> d
                    .index(INDEX)
                    .id(id.toString())
            );
        } catch (IOException e) {

        }
    }

    public String fullReIndex() {
        try {
            // Bước 1: Xóa index cũ nếu tồn tại
            if (client.indices().exists(e -> e.index(INDEX)).value()) {
                client.indices().delete(d -> d.index(INDEX));
                System.out.println("Đã xóa index cũ.");
            }

            // Bước 2: Tạo index mới với Mapping (Quan trọng!)
            // Phải chắc chắn hàm này chạy thành công
            elasticIndexService.createProductIndex();
            System.out.println("Đã tạo index mới với mapping.");

            // Bước 3: Lấy dữ liệu từ DB và đẩy vào ES
            List<ProductEntity> products = productRepository.findAll();
            if (products.isEmpty()) {
                return "DB trống, không có gì để reindex!";
            }

            BulkResponse bulkResponse = client.bulk(b -> {
                for (ProductEntity p : products) {
                    b.operations(op -> op
                            .index(idx -> idx.index(INDEX).id(p.getId().toString()).document(map(p)))
                    );
                }
                return b;
            });

            if (bulkResponse.errors()) {
                return "Reindex có lỗi ở một số bản ghi!";
            }

            return "Thành công! Đã reindex " + products.size() + " sản phẩm.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

}
