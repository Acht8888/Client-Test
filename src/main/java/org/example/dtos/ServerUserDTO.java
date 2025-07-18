package org.example.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ServerUserDTO {
    private UUID id;

    // Authentication
    private String username;

    // In-game data
    private String displayName;
    private int level;

}
