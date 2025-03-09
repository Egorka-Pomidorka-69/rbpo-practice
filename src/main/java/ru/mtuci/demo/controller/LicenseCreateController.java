package ru.mtuci.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.demo.configuration.JwtTokenProvider;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.request.LicenseCreateRequest;
import ru.mtuci.demo.service.impl.LicenseServiceImpl;
import ru.mtuci.demo.service.impl.LicenseTypeServiceImpl;
import ru.mtuci.demo.service.impl.ProductServiceImpl;
import ru.mtuci.demo.service.impl.UserDetailsServiceImpl;

@RestController
@RequestMapping("/license")
@RequiredArgsConstructor
public class LicenseCreateController {

    private final JwtTokenProvider jwtTokenProvider;
    private final LicenseServiceImpl licenseService;
    private final LicenseTypeServiceImpl licenseTypeService;
    private final ProductServiceImpl productService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserDetailsServiceImpl userService;

    @PostMapping("/createadm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createadm(@RequestBody LicenseCreateRequest licenseCreateRequest,
                                           HttpServletRequest httpServletRequest) {

        try {

            Long productId = licenseCreateRequest.getProductId();
            Long licenseTypeId = licenseCreateRequest.getLicenseTypeId();
            Long ownerId = licenseCreateRequest.getOwnerId();

            if (productId == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите id продукта!");

            if (licenseTypeId == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите id типа лицензии!");

            if (licenseCreateRequest.getDeviceCount() == null || licenseCreateRequest.getDeviceCount() <= 0)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Введите количество девайсов, на которых может быть размещена лицензия!");

            if (ownerId == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите id владельца!");

            if (productService.getProductById(productId).isEmpty())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Продукт не найден!");

            if (productService.getProductById(productId).get().isBlocked())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Продукт не доступен!");

            if (licenseTypeService.getLicenseTypeById(licenseTypeId).isEmpty())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Тип лицензии не найден!");

            if (userService.getUserById(ownerId).isEmpty())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Владелец не найден!");

            String email = jwtTokenProvider.getUsername(httpServletRequest.getHeader("Authorization").substring(7));
            ApplicationUser applicationUser = userDetailsService.getUserByEmail(email).get();

            Long id = licenseService.createLicense(applicationUser, productId, licenseTypeId,
                    licenseCreateRequest.getDeviceCount(), ownerId);

            return ResponseEntity.status(HttpStatus.OK).body("Лицензия успешно создана!\nID: " + id);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Технические шоколадки...");

        }

    }

}