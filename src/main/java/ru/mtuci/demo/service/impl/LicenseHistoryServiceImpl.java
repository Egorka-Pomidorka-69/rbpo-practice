package ru.mtuci.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.ApplicationLicense;
import ru.mtuci.demo.model.ApplicationLicenseHistory;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.repository.LicenseHistoryRepository;
import ru.mtuci.demo.service.LicenseHistoryService;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class LicenseHistoryServiceImpl implements LicenseHistoryService {

    private final LicenseHistoryRepository licenseHistoryRepository;

    @Override
    public ApplicationLicenseHistory createNewRecord(ApplicationLicense applicationLicense, ApplicationUser applicationUser,
                                                     String status, String description) {

        ApplicationLicenseHistory applicationLicenseHistory = new ApplicationLicenseHistory();
        applicationLicenseHistory.setLicense(applicationLicense);
        applicationLicenseHistory.setUser(applicationUser);
        applicationLicenseHistory.setStatus(status);
        applicationLicenseHistory.setChangeDate(new Date());
        applicationLicenseHistory.setDescription(description);

        return licenseHistoryRepository.save(applicationLicenseHistory);

    }

}