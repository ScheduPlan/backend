package de.hofuniversity.assemblyplanner.service;

import de.hofuniversity.assemblyplanner.exceptions.ResourceNotFoundException;
import de.hofuniversity.assemblyplanner.persistence.model.Part;
import de.hofuniversity.assemblyplanner.persistence.model.Product;
import de.hofuniversity.assemblyplanner.persistence.model.ProductPart;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductPartAppendRequest;
import de.hofuniversity.assemblyplanner.persistence.model.dto.ProductPartUpdateRequest;
import de.hofuniversity.assemblyplanner.persistence.repository.PartRepository;
import de.hofuniversity.assemblyplanner.persistence.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductPartServiceImpl implements de.hofuniversity.assemblyplanner.service.api.ProductPartService {

    private final PartRepository partRepository;
    private final ProductRepository productRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(PartServiceImpl.class);

    public ProductPartServiceImpl(@Autowired PartRepository partRepository,
                                  @Autowired ProductRepository productRepository) {
        this.partRepository = partRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Iterable<Part> getParts(UUID productId) {
        return partRepository.findPartsByProductId(productId);
    }

    @Override
    public Part getPart(UUID productId, UUID partId) {
        LOGGER.info("retrieving part {} for product {}", partId, productId);
        return partRepository
                .findPartByProductId(productId, partId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Product addPart(UUID productId, ProductPartAppendRequest appendRequest) {
        Product product = productRepository.findById(productId).orElseThrow(ResourceNotFoundException::new);
        Part part = partRepository.findById(appendRequest.partId()).orElseThrow(ResourceNotFoundException::new);
        product.addPart(part, appendRequest.amount());

        LOGGER.info("adding {} parts of type {} to product {}", appendRequest.amount(), appendRequest.partId(), productId);

        partRepository.save(part);
        return productRepository.save(product);
    }

    @Override
    public ProductPart deletePart(UUID productId, UUID partId) {
        Product product = productRepository.findById(productId).orElseThrow(ResourceNotFoundException::new);
        Part part = partRepository.findPartByProductId(productId, partId).orElseThrow(ResourceNotFoundException::new);
        ProductPart association = product.removePart(part);
        partRepository.save(part);
        productRepository.save(product);

        LOGGER.info("deleted part {} on product {}", partId, productId);
        return association;
    }

    @Override
    public ProductPart updateAmount(UUID productId,
                                    UUID partId,
                                    ProductPartUpdateRequest updateRequest) {
        Part part = partRepository.findPartByProductId(productId, partId).orElseThrow(ResourceNotFoundException::new);
        ProductPart productPart = part.getProducts().stream()
                .filter(pp -> pp.getProduct().getId().equals(productId))
                .findAny()
                .orElseThrow(ResourceNotFoundException::new);

        LOGGER.info("updating amount of part {} on product {} to {}", partId, productId, updateRequest.amount());

        productPart.setAmount(updateRequest.amount());
        partRepository.save(part);
        productRepository.save(productPart.getProduct());
        return productPart;
    }
}
