package ru.mtuci.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.ApplicationDevice;
import ru.mtuci.demo.model.ApplicationDeviceLicense;
import ru.mtuci.demo.model.ApplicationLicense;
import ru.mtuci.demo.repository.DeviceLicenseRepository;
import ru.mtuci.demo.service.DeviceLicenseService;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceLicenseServiceImpl implements DeviceLicenseService {

    private final DeviceLicenseRepository deviceLicenseRepository;

    @Override
    public Long getDeviceCountForLicense(Long licenseId) {

        return deviceLicenseRepository.countByLicenseId(licenseId);

    }

    @Override
    public List<ApplicationDeviceLicense> getAllLicenseById(ApplicationDevice applicationDevice) {

        return deviceLicenseRepository.findByDeviceId(applicationDevice.getId());

    }

    @Override
    public ApplicationDeviceLicense createDeviceLicense(ApplicationLicense applicationLicense,
                                                        ApplicationDevice applicationDevice) {

        ApplicationDeviceLicense applicationDeviceLicense = new ApplicationDeviceLicense();
        applicationDeviceLicense.setLicense(applicationLicense);
        applicationDeviceLicense.setDevice(applicationDevice);
        applicationDeviceLicense.setActivationDate(new Date());

        return deviceLicenseRepository.save(applicationDeviceLicense);

    }

}