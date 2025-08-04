package org.example.enums;

import lombok.Getter;

@Getter
public enum ClientMessageType {
    // ===== Connection 100 =====
    AUTH_REQUEST(100),
    RECONNECT(101),
    DISCONNECT(102),
    HEARTBEAT(103),
    PING(104),

    // ===== User Management 200 =====
    GET_USER_INFO(200),
    GET_USER_BY_ID(201),

    // ===== Matchmaking 300 =====
    QUEUE_MATCH(300),
    CANCEL_QUEUE(301),
    MATCH_FOUND(302),
    ACCEPT_MATCH(303),
    REJECT_MATCH(304),

    // ===== Room 400 =====
    CREATE_ROOM(400),
    JOIN_ROOM(401),
    LEAVE_ROOM(402),
    READY(403),
    UNREADY(404),
    START_GAME(405),
    GET_ROOM_INFO(406),
    GET_ROOM_BY_ID(407),
    GET_ALL_ROOMS(408),
    SEND_ROOM_INVITE(409),
    GET_ROOM_BY_NAME(410),

    // ===== Friend System 500 =====
    SEND_FRIEND_REQUEST(500),
    ACCEPT_FRIEND_REQUEST(501),
    DECLINE_FRIEND_REQUEST(502),
    REMOVE_FRIEND(503),
    GET_FRIEND_REQUESTS(504),
    GET_FRIEND_LIST(505),

    // ===== Player Actions 600 =====
    MOVE(600),
    ATTACK(601),
    DASH(602),
    INTERACT(603),
    CRAFT(604),
    PICKUP_RESOURCE(605),
    DROP_RESOURCE(606),

    // ===== Game Management 700 =====
    CREATE_GAME(700),
    PAUSE_GAME(701),
    RESUME_GAME(702),
    SYNC_STATE(703),
    REQUEST_GAME_STATE(704),

    // ===== Post-game 800 =====
    SUBMIT_SCORE(800),
    GET_RESULTS(801),
    REPORT_PLAYER(802),
    GIVE_FEEDBACK(803),

    // ===== Chat 900 =====
    CHAT_TO_USER(900),
    CHAT_TO_ROOM(901);

    private final int messageCode;

    ClientMessageType(int messageCode) {
        this.messageCode = messageCode;
    }

    public static ClientMessageType fromMessageCode(short code) {
        for (ClientMessageType type : ClientMessageType.values()) {
            if (type.getMessageCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid message code: " + code);
    }

    public short getMessageCode() {
        return (short) messageCode;
    }

}
