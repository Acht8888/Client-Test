package org.example.dtos;

import com.potionprotocol.common.utils.BinarySerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServerGetFriendRequestDTO {
    @BinarySerializer.FieldOrder(0)
    List<ServerFriendDTO> friendRequestList;
}
