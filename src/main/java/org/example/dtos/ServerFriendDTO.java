package org.example.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.enums.FriendStatus;

import java.util.UUID;

@Getter
@Setter
public class ServerFriendDTO {
    private UUID id;

    private UUID userId;
    private UUID friendId;

    private FriendStatus status;
}
