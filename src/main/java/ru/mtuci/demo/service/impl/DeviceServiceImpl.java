package ru.mtuci.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.ApplicationDevice;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.repository.DeviceRepository;
import ru.mtuci.demo.service.DeviceService;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;

    @Override
    public Optional<ApplicationDevice> getDeviceByInfo(String name, String mac_address, ApplicationUser applicationUser) {

        return deviceRepository.findByNameAndMacAddressAndUser(name, mac_address, applicationUser);

    }

    @Override
    public void deleteLastDevice(ApplicationUser applicationUser) {

        Optional<ApplicationDevice> applicationDevice = deviceRepository.findTopByUserOrderByIdDesc(applicationUser);
        applicationDevice.ifPresent(deviceRepository::delete);

    }

    @Override
    public ApplicationDevice registerDevice(String name, String mac_address, ApplicationUser applicationUser) {

        if (getDeviceByInfo(name, mac_address, applicationUser).isPresent())
            return getDeviceByInfo(name, mac_address, applicationUser).get();

        ApplicationDevice applicationDevice = new ApplicationDevice();
        applicationDevice.setName(name);
        applicationDevice.setMacAddress(mac_address);
        applicationDevice.setUser(applicationUser);

        return deviceRepository.save(applicationDevice);

    }

}