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
public class ServerLeaveRoomDTO {
    @BinarySerializer.FieldOrder(0)
    private UUID userId;

    @BinarySerializer.FieldOrder(1)
    private String userDisplayName;

    @BinarySerializer.FieldOrder(2)
    private UUID leaderId;
}
