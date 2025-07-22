package org.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.enums.ServerMessageType;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServerMessage {
    private ServerMessageType typeCode;
    private int messageId;
    private short statusCode;
    private Object data;

//    public ServerMessage() {
//    }
//
//    public ServerMessage(ServerMessageType typeCode, int messageId, short statusCode, Map<String, String> data) {
//        this.typeCode = typeCode;
//        this.messageId = messageId;
//        this.statusCode = statusCode;
//        this.data = data;
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder s = new StringBuilder("{");
//        s.append("type:").append(typeCode).append(",");
//        s.append("messageId:").append(messageId).append(",");
//        s.append("statusCode:").append(statusCode).append(",");
//        data.forEach((k, v) -> {
//            s.append(k).append(":").append(v).append(",");
//        });
//        return s.append("}").toString();
//    }
}
