package ru.mtuci.demo.request;

import lombok.*;
import ru.mtuci.demo.model.ApplicationRole;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestUser {

    private Long id;
    private String username;
    private String email;
    private ApplicationRole role;

}