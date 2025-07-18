package org.example.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.enums.RoomStatus;
import org.example.enums.RoomType;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class ServerRoomDTO {
    private UUID id;

    // Room details
    private String name;
    private RoomType type;
    private int maxPlayers;
    private int currentPlayers;
    private RoomStatus status;

    // List of players
    private List<UUID> playerIds;
}

