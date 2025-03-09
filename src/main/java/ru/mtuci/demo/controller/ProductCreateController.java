package ru.mtuci.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.demo.request.ProductCreateRequest;
import ru.mtuci.demo.service.impl.ProductServiceImpl;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductCreateController {

    private final ProductServiceImpl productService;

    @PostMapping("/createadm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createadm(@RequestBody ProductCreateRequest productCreateRequest) {

        try {

            if (productCreateRequest.getName() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите наименование продукта!");

            if (productCreateRequest.getIsBlocked() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите состояние продукта!");

            Long id = productService.createProduct(productCreateRequest.getName(), productCreateRequest.getIsBlocked());

            return ResponseEntity.status(HttpStatus.OK).body("Продукт успешно создан!\nID: " + id);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Технические шоколадки...");

        }

    }

}