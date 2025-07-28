package org.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.utils.BinarySerializer;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServerPlayerIdDTO {
    @BinarySerializer.FieldOrder(0)
    String playerId;

    @BinarySerializer.FieldOrder(1)
    List<String> playerIds;
}
