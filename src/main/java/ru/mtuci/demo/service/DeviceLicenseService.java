package ru.mtuci.demo.service;

import ru.mtuci.demo.model.ApplicationDevice;
import ru.mtuci.demo.model.ApplicationDeviceLicense;
import ru.mtuci.demo.model.ApplicationLicense;
import java.util.List;

public interface DeviceLicenseService {

    Long getDeviceCountForLicense(Long licenseId);
    List<ApplicationDeviceLicense> getAllLicenseById(ApplicationDevice applicationDevice);
    ApplicationDeviceLicense createDeviceLicense(ApplicationLicense applicationLicense, ApplicationDevice applicationDevice);

}