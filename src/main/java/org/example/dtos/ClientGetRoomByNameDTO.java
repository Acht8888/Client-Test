package org.example.dtos;

import com.potionprotocol.common.utils.BinarySerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientGetRoomByNameDTO {
    @BinarySerializer.FieldOrder(0)
    private String roomDisplayName;
}
