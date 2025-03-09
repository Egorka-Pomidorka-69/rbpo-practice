package ru.mtuci.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mtuci.demo.model.ApplicationDevice;
import ru.mtuci.demo.model.ApplicationUser;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<ApplicationDevice, Long> {

    Optional<ApplicationDevice> findById(Long id);
    Optional<ApplicationDevice> findByNameAndMacAddressAndUser(String name, String mac_address, ApplicationUser applicationUser);
    Optional<ApplicationDevice> findTopByUserOrderByIdDesc(ApplicationUser applicationUser);

}