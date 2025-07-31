package org.example.dtos;

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
public class ServerUnreadyDTO {
    @BinarySerializer.FieldOrder(0)
    private UUID userId;
}
