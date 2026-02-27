package com.web.controller.user;

import com.web.elastic.document.ProductDocument;
import com.web.service.elastic.ProductSearchService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final ProductSearchService searchService;

    @GetMapping
    public List<ProductDocument> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "price_asc") String sort
    ) throws IOException {

        return searchService.search(keyword, sort);
    }
}
