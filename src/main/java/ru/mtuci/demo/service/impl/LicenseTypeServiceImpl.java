package ru.mtuci.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.ApplicationLicenseType;
import ru.mtuci.demo.repository.LicenseTypeRepository;
import ru.mtuci.demo.service.LicenseTypeService;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LicenseTypeServiceImpl implements LicenseTypeService {

    private final LicenseTypeRepository licenseTypeRepository;

    @Override
    public Optional<ApplicationLicenseType> getLicenseTypeById(Long id) {

        return licenseTypeRepository.findById(id);

    }

    @Override
    public Long createLicenseType(String name, Long defaultDuration, String description) {

        ApplicationLicenseType licenseType = new ApplicationLicenseType();
        licenseType.setName(name);
        licenseType.setDefaultDuration(defaultDuration);
        licenseType.setDescription(description);

        licenseTypeRepository.save(licenseType);

        return licenseTypeRepository.findTopByOrderByIdDesc().get().getId();

    }

}