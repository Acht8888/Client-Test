package org.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.enums.ClientMessageType;
import org.example.utils.BinarySerializer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientMessage {
    @BinarySerializer.FieldOrder(0)
    private ClientMessageType type;

    @BinarySerializer.FieldOrder(1)
    private Object payload;

//    public ClientMessage() {
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder s = new StringBuilder("{");
//        s.append("type:").append(type).append(",");
//        s.append("messageId:").append(messageId).append(",");
//        payload.forEach((k, v) -> {
//            s.append(k).append(":").append(v).append(",");
//        });
//        return s.append("}").toString();
//    }
}
