package ru.mtuci.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.ApplicationProduct;
import ru.mtuci.demo.repository.ProductRepository;
import ru.mtuci.demo.service.ProductService;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService  {

    private final ProductRepository productRepository;

    @Override
    public Optional<ApplicationProduct> getProductById(Long id) {

        return productRepository.findById(id);

    }

    @Override
    public Long createProduct(String name, Boolean isBlocked) {

        ApplicationProduct product = new ApplicationProduct();
        product.setName(name);
        product.setBlocked(isBlocked);

        productRepository.save(product);

        return productRepository.findTopByOrderByIdDesc().get().getId();

    }

}