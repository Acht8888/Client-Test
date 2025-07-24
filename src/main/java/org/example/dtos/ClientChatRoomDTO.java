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
public class ClientChatRoomDTO {
    @BinarySerializer.FieldOrder(0)
    private UUID roomId;

    @BinarySerializer.FieldOrder(1)
    private String chatMessage;

}
