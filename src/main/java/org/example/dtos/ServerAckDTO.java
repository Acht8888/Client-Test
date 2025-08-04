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
public class ServerAckDTO {
    @BinarySerializer.FieldOrder(0)
    private short serverMessageType;
}
