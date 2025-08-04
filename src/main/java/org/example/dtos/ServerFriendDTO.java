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
public class ServerFriendDTO {
    @BinarySerializer.FieldOrder(0)
    private UUID id;

    @BinarySerializer.FieldOrder(1)
    private UUID friendId;

    @BinarySerializer.FieldOrder(2)
    private String friendDisplayName;

    @BinarySerializer.FieldOrder(3)
    private boolean isOnline;

}
