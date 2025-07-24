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
public class ServerRoomPlayerDTO {

    @BinarySerializer.FieldOrder(0)
    private UUID playerId;

    @BinarySerializer.FieldOrder(1)
    private String playerDisplayName;

    @BinarySerializer.FieldOrder(2)
    private short playerRole;

    @BinarySerializer.FieldOrder(3)
    private short playerStatus;

}
