package org.example.dtos;

import com.potionprotocol.application.friend.enums.FriendStatus;
import com.potionprotocol.common.utils.BinarySerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServerFriendDTO {
    @BinarySerializer.FieldOrder(0)
    private UUID id;

    @BinarySerializer.FieldOrder(1)
    private UUID userId;

    @BinarySerializer.FieldOrder(2)
    private UUID friendId;

    @BinarySerializer.FieldOrder(3)
    private FriendStatus status;
}
