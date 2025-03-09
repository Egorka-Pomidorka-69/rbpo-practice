package ru.mtuci.demo.request;

import lombok.*;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    private String username;
    private String email;
    private String password;

}