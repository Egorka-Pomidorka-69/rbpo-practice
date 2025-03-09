package ru.mtuci.demo.service;

import ru.mtuci.demo.model.ApplicationLicense;
import ru.mtuci.demo.model.ApplicationLicenseHistory;
import ru.mtuci.demo.model.ApplicationUser;

public interface LicenseHistoryService {

    ApplicationLicenseHistory createNewRecord(ApplicationLicense applicationLicense, ApplicationUser applicationUser,
                                              String status, String description);

}