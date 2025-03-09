package ru.mtuci.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mtuci.demo.configuration.JwtTokenProvider;
import ru.mtuci.demo.model.ApplicationDevice;
import ru.mtuci.demo.model.ApplicationTicket;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.request.LicenseInfoRequest;
import ru.mtuci.demo.service.impl.DeviceServiceImpl;
import ru.mtuci.demo.service.impl.LicenseServiceImpl;
import ru.mtuci.demo.service.impl.UserDetailsServiceImpl;
import java.util.Optional;

@RestController
@RequestMapping("/license")
@RequiredArgsConstructor
public class LicenseInfoController {

    private final JwtTokenProvider jwtTokenProvider;
    private final DeviceServiceImpl deviceService;
    private final LicenseServiceImpl licenseService;
    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping("/info")
    public ResponseEntity<?> info(@RequestBody LicenseInfoRequest licenseInfoRequest, HttpServletRequest httpServletRequest) {

        try {

            if (licenseInfoRequest.getName() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите имя!");

            if (licenseInfoRequest.getMac_address() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите MAC-адрес!");

            if (licenseInfoRequest.getCode() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите код активации!");

            String email = jwtTokenProvider.getUsername(httpServletRequest.getHeader("Authorization").substring(7));
            ApplicationUser applicationUser = userDetailsService.getUserByEmail(email).get();
            Optional<ApplicationDevice> applicationDevice = deviceService.getDeviceByInfo(licenseInfoRequest.getName(),
                    licenseInfoRequest.getMac_address(), applicationUser);

            if (applicationDevice.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Устройство не найдено!");

            ApplicationTicket applicationTicket = licenseService.getActiveLicensesForDevice(applicationDevice.get(),
                    licenseInfoRequest.getCode());

            if (!applicationTicket.getStatus().equals("OK"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(applicationTicket.getInfo());

            return ResponseEntity.status(HttpStatus.OK).body(applicationTicket);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Технические шоколадки...");

        }

    }

}