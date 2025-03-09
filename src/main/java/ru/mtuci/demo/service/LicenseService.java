package ru.mtuci.demo.service;

import ru.mtuci.demo.model.ApplicationDevice;
import ru.mtuci.demo.model.ApplicationLicense;
import ru.mtuci.demo.model.ApplicationTicket;
import ru.mtuci.demo.model.ApplicationUser;

public interface LicenseService {

    Long createLicense(ApplicationUser applicationUser, Long productId, Long licenseTypeId, Long deviceCount,  Long ownerId);
    ApplicationTicket getActiveLicensesForDevice(ApplicationDevice applicationDevice, String code);
    ApplicationTicket createTicket(String status, String info, ApplicationLicense applicationLicense,
                                   ApplicationUser applicationUser, ApplicationDevice applicationDevice);
    ApplicationTicket activateLicense(String code, ApplicationUser applicationUser, ApplicationDevice applicationDevice);
    ApplicationTicket renewalLicense(String code, ApplicationUser applicationUser);

}