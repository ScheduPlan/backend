package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Product;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductCreateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductUpdateRequest;
import de.hofuniversity.assemblyplanner.persistence.model.embedded.Description;
import de.hofuniversity.assemblyplanner.persistence.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductServiceImpl implements de.hofuniversity.assemblyplanner.service.api.ProductService {

    private final ProductRepository productRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(@Autowired ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Iterable<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProduct(UUID productId) {
        LOGGER.info("retrieving product {}", productId);
        return productRepository
                .findById(productId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Product createProduct(ProductCreateRequest createRequest) {
        Product product = new Product(
                new Description(createRequest.name(), createRequest.description()),
                createRequest.materialWidth(),
                createRequest.materialName(),
                createRequest.materialGroup(),
                createRequest.productGroup()
        );

        LOGGER.info("creating product using create request {}", createRequest);

        return productRepository.save(product);
    }

    @Override
    public Product patchProduct(UUID productId, ProductUpdateRequest updateRequest) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(ResourceNotFoundException::new);

        if(updateRequest.materialGroup() != null)
            product.setMaterialGroup(updateRequest.productGroup());
        if(updateRequest.productGroup() != null)
            product.setProductGroup(updateRequest.productGroup());
        if(updateRequest.materialWidth() != 0.0)
            product.setMaterialWidth(updateRequest.materialWidth());
        if(updateRequest.description() != null)
            product.getDescription().setDescription(updateRequest.description());
        if(updateRequest.name() != null)
            product.getDescription().setName(updateRequest.name());

        LOGGER.info("updating product {} using patch {}", productId, updateRequest);

        return productRepository.save(product);
    }

    @Override
    public Product putProduct(UUID productId, ProductUpdateRequest updateRequest) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(ResourceNotFoundException::new);

        BeanUtils.copyProperties(updateRequest, product, "description");
        product.getDescription().setName(updateRequest.name());
        product.getDescription().setDescription(updateRequest.description());

        LOGGER.info("updating product {} using update {}", productId, updateRequest);

        return productRepository.save(product);
    }

    @Override
    public Product deleteProduct(UUID productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(ResourceNotFoundException::new);

        LOGGER.info("deleting product {}", productId);
        productRepository.delete(product);
        return product;
    }
}
