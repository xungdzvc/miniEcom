package com.web.service.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
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

            // 1. Xóa index cũ
            try {
                client.indices().delete(d -> d.index(INDEX));
            } catch (Exception e) {
                System.out.println("Index chưa tồn tại, bỏ qua.");
            }

            // 2. Tạo index mới
            elasticIndexService.createProductIndex();

            // 3. Lấy dữ liệu DB
            List<ProductEntity> products = productRepository.findAll();

            if (products.isEmpty()) {
                return "Database trống!";
            }

            // 4. Bulk index
            client.bulk(b -> {

                for (ProductEntity p : products) {

                    ProductDocument doc = map(p);

                    b.operations(op -> op
                            .index(i -> i
                            .index(INDEX)
                            .id(p.getId().toString())
                            .document(doc)
                            )
                    );
                }

                return b;
            });

            // 5. Refresh index để search thấy ngay
            client.indices().refresh(r -> r.index(INDEX));

            return "Đã reindex " + products.size() + " sản phẩm!";

        } catch (Exception e) {
            return "Reindex thất bại: " + e.getMessage();
        }
    }

}
