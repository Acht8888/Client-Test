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
public class ServerChatRoomDTO {
    @BinarySerializer.FieldOrder(0)
    private UUID requesterId;

    @BinarySerializer.FieldOrder(1)
    private String requesterDisplayName;

    @BinarySerializer.FieldOrder(2)
    private String message;
}
