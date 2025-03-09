package ru.mtuci.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.demo.request.LicenseTypeCreateRequest;
import ru.mtuci.demo.service.impl.LicenseTypeServiceImpl;

@RestController
@RequestMapping("/license/type")
@RequiredArgsConstructor
public class LicenseTypeCreateController {

    private final LicenseTypeServiceImpl licenseTypeService;

    @PostMapping("/createadm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createadm(@RequestBody LicenseTypeCreateRequest licenseTypeCreateRequest) {

        try {

            if(licenseTypeCreateRequest.getName() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Имя типа лицензии!");

            if(licenseTypeCreateRequest.getDefaultDuration() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите срок действия лицензии!");

            if (licenseTypeCreateRequest.getDescription() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите описание лицензии!");

            Long id = licenseTypeService.createLicenseType(licenseTypeCreateRequest.getName(),
                    licenseTypeCreateRequest.getDefaultDuration(), licenseTypeCreateRequest.getDescription());

            return ResponseEntity.status(HttpStatus.OK).body("Новый тип лицензии создан!\nID: " + id);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Технические шоколадки...");
        }

    }

}