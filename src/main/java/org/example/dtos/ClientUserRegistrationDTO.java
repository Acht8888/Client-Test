package org.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientUserRegistrationDTO {
    private String username;
    private String password;
    private String confirmPassword;
    private String displayName;
}
