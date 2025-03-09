package ru.mtuci.demo.request;

import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseCreateRequest {

    private Long productId;
    private Long licenseTypeId;
    private Long deviceCount;
    private Long ownerId;

}