package ru.mtuci.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.demo.model.*;
import ru.mtuci.demo.repository.DeviceLicenseRepository;
import ru.mtuci.demo.repository.LicenseRepository;
import ru.mtuci.demo.service.LicenseService;
import java.security.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LicenseServiceImpl implements LicenseService {

    private final DeviceLicenseRepository deviceLicenseRepository;
    private final LicenseRepository licenseRepository;
    private final DeviceLicenseServiceImpl deviceLicenseService;
    private final DeviceServiceImpl deviceService;
    private final LicenseHistoryServiceImpl licenseHistoryService;
    private final LicenseTypeServiceImpl licenseTypeService;
    private final ProductServiceImpl productService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public Long createLicense(ApplicationUser applicationUser, Long productId, Long licenseTypeId, Long deviceCount,  Long ownerId) {

        ApplicationProduct applicationProduct = productService.getProductById(productId).get();
        ApplicationLicenseType applicationLicenseType = licenseTypeService.getLicenseTypeById(licenseTypeId).get();
        ApplicationUser applicationOwnerUser = userDetailsService.getUserById(ownerId).get();

        ApplicationLicense applicationLicense = new ApplicationLicense();
        String code = String.valueOf(UUID.randomUUID());

        while (licenseRepository.findByCode(code).isPresent()) code = String.valueOf(UUID.randomUUID());

        applicationLicense.setCode(code);
        applicationLicense.setProduct(applicationProduct);
        applicationLicense.setLicenseType(applicationLicenseType);
        applicationLicense.setBlocked(applicationProduct.isBlocked());
        applicationLicense.setDeviceCount(deviceCount);
        applicationLicense.setOwner(applicationOwnerUser);
        applicationLicense.setDuration(applicationLicenseType.getDefaultDuration());
        applicationLicense.setDescription(applicationLicenseType.getDescription());

        licenseRepository.save(applicationLicense);

        licenseHistoryService.createNewRecord(licenseRepository.findTopByOrderByIdDesc().get(), applicationUser,
                "Не активирована", "Создана новая лицензия");

        return licenseRepository.findTopByOrderByIdDesc().get().getId();

    }

    @Override
    public ApplicationTicket getActiveLicensesForDevice(ApplicationDevice applicationDevice, String code) {

        List<ApplicationDeviceLicense> applicationDeviceLicenses = deviceLicenseService.getAllLicenseById(applicationDevice);

        List<Long> licenseIds = applicationDeviceLicenses.stream()
                .map(license -> license.getLicense() != null ? license.getLicense().getId() : null)
                .collect(Collectors.toList());

        Optional<ApplicationLicense> applicationLicense = licenseRepository.findByIdInAndCode(licenseIds, code);

        ApplicationTicket applicationTicket = new ApplicationTicket();

        if (applicationLicense.isEmpty()) {
            applicationTicket.setStatus("Ошибка");
            applicationTicket.setInfo("Лицензии не найдены");
            return applicationTicket;
        }

        applicationTicket = createTicket("OK", "Информация о лицензиях", applicationLicense.get(),
                applicationLicense.get().getUser(), applicationDevice);

        return applicationTicket;

    }

    @Override
    public ApplicationTicket createTicket(String status, String info, ApplicationLicense applicationLicense,
                                          ApplicationUser applicationUser, ApplicationDevice applicationDevice) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, 3);

        ApplicationTicket applicationTicket = new ApplicationTicket();

        applicationTicket.setStatus(status);
        applicationTicket.setInfo(info);
        applicationTicket.setCurrentDate(calendar.getTime());
        applicationTicket.setLifetime(calendar.getTime());

        if (applicationLicense != null) {
            calendar.setTime(applicationLicense.getFirstActivationDate());
            calendar.add(Calendar.HOUR_OF_DAY, 3);
            applicationTicket.setActivationDate(calendar.getTime());
            calendar.setTime(applicationLicense.getEndingDate());
            calendar.add(Calendar.HOUR_OF_DAY, 3);
            applicationTicket.setExpirationDate(calendar.getTime());
            applicationTicket.setLicenseBlocked(applicationLicense.isBlocked());
        }

        if (applicationUser != null) applicationTicket.setUserId(applicationUser.getId());

        if (applicationDevice != null) applicationTicket.setDeviceId(applicationDevice.getId());

        applicationTicket.setDigitalSignature(makeSignature(applicationTicket));

        return applicationTicket;

    }

    @Override
    public ApplicationTicket activateLicense(String code, ApplicationUser applicationUser,
                                             ApplicationDevice applicationDevice) {

        ApplicationTicket applicationTicket = new ApplicationTicket();
        Optional<ApplicationLicense> applicationLicense = licenseRepository.findByCode(code);

        if (applicationLicense.isEmpty()) {
            applicationTicket.setStatus("Ошибка");
            applicationTicket.setInfo("Неправильный код активации");
            if (deviceLicenseRepository.findByDeviceId(applicationDevice.getId()).isEmpty())
                deviceService.deleteLastDevice(applicationUser);
            return applicationTicket;
        }

        ApplicationLicense applicationLicense1 = applicationLicense.get();

        if (!deviceLicenseRepository.findByDeviceIdAndLicenseId(applicationDevice.getId(),
                applicationLicense1.getId()).isEmpty()) {
            applicationTicket.setStatus("Ошибка");
            applicationTicket.setInfo("Активация невозможна");
            return applicationTicket;
        }

        if (applicationLicense1.isBlocked()
                || (applicationLicense1.getEndingDate() != null && new Date().after(applicationLicense1.getEndingDate()))
                || (applicationLicense1.getUser() != null && !Objects.equals(applicationLicense1.getUser().getId(), applicationUser.getId()))
                || deviceLicenseService.getDeviceCountForLicense(applicationLicense1.getId()) >=
                applicationLicense1.getDeviceCount()) {
            applicationTicket.setStatus("Ошибка");
            applicationTicket.setInfo("Активация невозможна");
            if (deviceLicenseRepository.findByDeviceId(applicationDevice.getId()).isEmpty())
                deviceService.deleteLastDevice(applicationUser);
            return applicationTicket;
        }

        if (applicationLicense1.getFirstActivationDate() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            applicationLicense1.setFirstActivationDate(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, Math.toIntExact(applicationLicense1.getDuration()));
            applicationLicense1.setEndingDate(calendar.getTime());
            applicationLicense1.setUser(applicationUser);
        }

        deviceLicenseService.createDeviceLicense(applicationLicense1, applicationDevice);
        licenseRepository.save(applicationLicense1);
        licenseHistoryService.createNewRecord(applicationLicense1, applicationUser, "Активирована",
                "Действительная лицензия");

        applicationTicket = createTicket("OK", "Лицензия была успешно активирована", applicationLicense1,
                applicationUser, applicationDevice);

        return applicationTicket;

    }

    @Override
    public ApplicationTicket renewalLicense(String code, ApplicationUser applicationUser) {

        ApplicationTicket applicationTicket = new ApplicationTicket();
        Optional<ApplicationLicense> applicationLicense = licenseRepository.findByCode(code);

        if (applicationLicense.isEmpty()) {
            applicationTicket.setStatus("Ошибка");
            applicationTicket.setInfo("Неправильный код активации");
            return applicationTicket;
        }

        ApplicationLicense applicationLicense1 = applicationLicense.get();

        if (applicationLicense1.isBlocked()
                || applicationLicense1.getEndingDate() != null && new Date().after(applicationLicense1.getEndingDate())
                || !Objects.equals(applicationLicense1.getUser().getId(), applicationUser.getId())
                || applicationLicense1.getFirstActivationDate() == null) {
            applicationTicket.setStatus("Ошибка");
            applicationTicket.setInfo("Продление лицензии невозможно");
            return applicationTicket;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(applicationLicense1.getEndingDate());
        calendar.add(Calendar.DAY_OF_MONTH, Math.toIntExact(applicationLicense1.getDuration()));
        applicationLicense1.setEndingDate(calendar.getTime());

        licenseRepository.save(applicationLicense1);
        licenseHistoryService.createNewRecord(applicationLicense1, applicationUser,"Продление",
                "Действительная лицензия");

        applicationTicket = createTicket("OK", "Лицензия успешно продлена", applicationLicense1,
                applicationUser, null);

        return applicationTicket;

    }

    private String makeSignature(ApplicationTicket applicationTicket) {

        try {

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            ObjectMapper objectMapper = new ObjectMapper();
            String res = objectMapper.writeValueAsString(applicationTicket);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(res.getBytes());

            return Base64.getEncoder().encodeToString(signature.sign());

        } catch (Exception e) {

            return "Что-то пошло не так. Подпись не действительна";

        }

    }

}