package org.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.BinarySerializer;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServerUserDTO {
    @BinarySerializer.FieldOrder(0)
    private UUID id;

    // Authentication
    @BinarySerializer.FieldOrder(1)
    private String username;

    // In-game data
    @BinarySerializer.FieldOrder(2)
    private String displayName;

    @BinarySerializer.FieldOrder(3)
    private int level;

}
