package ru.mtuci.demo.service;

import ru.mtuci.demo.model.ApplicationLicenseType;
import java.util.Optional;

public interface LicenseTypeService {

    Optional<ApplicationLicenseType> getLicenseTypeById(Long id);
    Long createLicenseType(String name, Long defaultDuration, String description);

}