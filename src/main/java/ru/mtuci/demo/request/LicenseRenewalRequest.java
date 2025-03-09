package ru.mtuci.demo.request;

import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenseRenewalRequest {

    private String code;

}