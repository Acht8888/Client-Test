package org.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.BinarySerializer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientCreateRoomDTO {
    @BinarySerializer.FieldOrder(0)
    private String roomName;

    @BinarySerializer.FieldOrder(1)
    private short roomType;

    @BinarySerializer.FieldOrder(2)
    private int maxPlayers;
}
