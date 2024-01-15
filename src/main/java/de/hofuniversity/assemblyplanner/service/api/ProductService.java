package de.hofuniversity.assemblyplanner.service.api;

import de.hofuniversity.assemblyplanner.persistence.model.Product;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductUpdateRequest;

import java.util.UUID;

public interface ProductService {
    Iterable<Product> getProducts();

    Product getProduct(UUID productId);

    Product createProduct(ProductCreateRequest createRequest);

    Product patchProduct(UUID productId, ProductUpdateRequest updateRequest);

    Product putProduct(UUID productId, ProductUpdateRequest updateRequest);

    Product deleteProduct(UUID productId);
}
