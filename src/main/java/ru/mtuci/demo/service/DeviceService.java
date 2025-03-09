package ru.mtuci.demo.service;

import ru.mtuci.demo.model.ApplicationDevice;
import ru.mtuci.demo.model.ApplicationUser;
import java.util.Optional;

public interface DeviceService {

    Optional<ApplicationDevice> getDeviceByInfo(String name, String mac_address, ApplicationUser applicationUser);
    void deleteLastDevice(ApplicationUser applicationUser);
    ApplicationDevice registerDevice(String name, String mac_address, ApplicationUser applicationUser);

}