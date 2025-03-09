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
import ru.mtuci.demo.model.ApplicationTicket;
import ru.mtuci.demo.model.ApplicationUser;
import ru.mtuci.demo.request.LicenseRenewalRequest;
import ru.mtuci.demo.service.impl.LicenseServiceImpl;
import ru.mtuci.demo.service.impl.UserDetailsServiceImpl;

@RestController
@RequestMapping("/license")
@RequiredArgsConstructor
public class LicenseRenewalController {

    private final JwtTokenProvider jwtTokenProvider;
    private final LicenseServiceImpl licenseService;
    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping("/renewal")
    public ResponseEntity<?> renewal(@RequestBody LicenseRenewalRequest licenseRenewalRequest,
                                            HttpServletRequest httpServletRequest) {

        try {

            if (licenseRenewalRequest.getCode() == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Введите код активации!");

            String email = jwtTokenProvider.getUsername(httpServletRequest.getHeader("Authorization").substring(7));
            ApplicationUser applicationUser = userDetailsService.getUserByEmail(email).get();

            ApplicationTicket applicationTicket = licenseService.renewalLicense(licenseRenewalRequest.getCode(),
                    applicationUser);

            if (!applicationTicket.getStatus().equals("OK"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(applicationTicket.getInfo());

            return ResponseEntity.status(HttpStatus.OK).body(applicationTicket);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Технические шоколадки...");

        }

    }

}