package org.example.enums;

import lombok.Getter;

@Getter
public enum ServerMessageType {
    // ===== Connection 100 =====
    AUTH_SUCCESS(100),
    AUTH_FAIL(101),
    TOKEN_EXPIRED(102),
    SESSION_RESTORED(103),

    // ===== User Management 200 =====
    GET_USER_INFO(200),
    GET_USER_BY_ID(201),

    // ===== Game State Sync 300 =====
    GAME_CREATED(300),
    GAME_STATE(301),
    PLAYER_JOINED(302),
    PLAYER_LEFT(303),
    OBJECT_SPAWNED(304),
    OBJECT_DESTROYED(305),
    WORLD_UPDATE(306),
    MATCH_STARTED(307),
    MATCH_ENDED(308),

    // ===== Server Instructions 400 =====
    FORCE_DISCONNECT(400),
    ERROR_MESSAGE(401),
    SERVER_NOTICE(402),
    RECONNECT_PROMPT(403),

    // ===== Chat 500 =====
    CHAT_BROADCAST(500),
    CHAT_TO_USER(501),
    CHAT_TO_ROOM(502),

    // ===== Friend System  600 =====
    GET_FRIEND_REQUESTS(600),
    GET_FRIEND_LIST(601),
    SEND_FRIEND_REQUEST(602),
    ACCEPT_FRIEND_REQUEST(603),
    DECLINE_FRIEND_REQUEST(604),
    REMOVE_FRIEND(605),

    // ===== Friend System Error 650 =====
    FRIEND_EXISTS(650),

    // ===== Room System 700 =====
    CREATE_ROOM(700),
    JOIN_ROOM(701),
    LEAVE_ROOM(702),
    READY(703),
    UNREADY(704),
    START_GAME(705),
    MATCH_MAKING(706),
    GET_ROOM_INFO(707),
    GET_ROOM_BY_ID(708),
    GET_ALL_ROOMS(709),
    SEND_ROOM_INVITE(710),
    ACCEPT_ROOM_INVITE(711),
    DECLINE_ROOM_INVITE(712),

    // ===== Room System Error 750 =====
    ROOM_FULL(750),
    PLAYER_NOT_READY(751),
    IN_ROOM(752),
    NOT_IN_ROOM(753),
    ONLY_LEADER(754),
    ROOM_INVALID_PASSWORD(755),

    // ===== Pings 800 =====
    PONG(800),
    SERVER_TICK(801);

    private final int messageCode;

    ServerMessageType(int messageCode) {
        this.messageCode = messageCode;
    }

    public static ServerMessageType fromMessageCode(short code) {
        for (ServerMessageType type : ServerMessageType.values()) {
            if (type.getMessageCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid message code: " + code);
    }

    public short getMessageCode() {
        return (short) messageCode;
    }

//    /**
//     * Messages that should be sent to all connected clients
//     */
//    public static final Set<ServerMessageType> BROADCAST_MESSAGES = Set.of(
//            CHAT_BROADCAST, SERVER_NOTICE, WORLD_UPDATE, MATCH_STARTED, MATCH_ENDED
//    );
//
//    /**
//     * High priority messages that should be processed immediately
//     */
//    public static final Set<ServerMessageType> HIGH_PRIORITY = Set.of(
//            FORCE_DISCONNECT, ERROR_MESSAGE, TOKEN_EXPIRED, RECONNECT_PROMPT
//    );
//
//    /**
//     * Game state messages that need to be synchronized
//     */
//    public static final Set<ServerMessageType> GAME_STATE_MESSAGES = Set.of(
//            GAME_STATE, PLAYER_JOINED, PLAYER_LEFT, OBJECT_SPAWNED,
//            OBJECT_DESTROYED, WORLD_UPDATE, MATCH_STARTED, MATCH_ENDED
//    );
//
//    /**
//     * System/heartbeat messages
//     */
//    public static final Set<ServerMessageType> SYSTEM_MESSAGES = Set.of(
//            PONG, SERVER_TICK, FORCE_DISCONNECT, RECONNECT_PROMPT
//    );
//
//    public boolean isBroadcast() {
//        return BROADCAST_MESSAGES.contains(this);
//    }
//
//    public boolean isHighPriority() {
//        return HIGH_PRIORITY.contains(this);
//    }
//
//    public boolean isGameState() {
//        return GAME_STATE_MESSAGES.contains(this);
//    }
//
//    public boolean isSystemMessage() {
//        return SYSTEM_MESSAGES.contains(this);
//    }
}
