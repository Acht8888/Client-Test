package org.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.BinarySerializer;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServerRoomDTO {

    @BinarySerializer.FieldOrder(0)
    private UUID id;

    // Room details
    @BinarySerializer.FieldOrder(1)
    private String name;

    @BinarySerializer.FieldOrder(2)
    private short mode;

    @BinarySerializer.FieldOrder(3)
    private short type;

    @BinarySerializer.FieldOrder(4)
    private int maxPlayers;

    @BinarySerializer.FieldOrder(5)
    private int currentPlayers;

    // List of players
    @BinarySerializer.FieldOrder(6)
    private List<ServerRoomPlayerDTO> playerList;

    @BinarySerializer.FieldOrder(7)
    private int mapId;

}

