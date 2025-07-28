package org.example.enums;

import java.util.Set;

public enum ServerMessageType {
    // ===== Connection =====
    AUTH_SUCCESS,
    AUTH_FAIL,
    TOKEN_EXPIRED,
    SESSION_RESTORED,

    // ===== User Management =====
    GET_USER_INFO,
    GET_USER_BY_ID,

    // Game state sync
    GAME_CREATED,
    GAME_STATE,
    PLAYER_JOINED,
    PLAYER_LEFT,
    OBJECT_SPAWNED,
    OBJECT_DESTROYED,
    WORLD_UPDATE,
    MATCH_STARTED,
    MATCH_ENDED,

    // Server instructions
    FORCE_DISCONNECT,
    ERROR_MESSAGE,
    SERVER_NOTICE,
    RECONNECT_PROMPT,

    // ===== Chat =====
    CHAT_BROADCAST,
    CHAT_TO_USER,
    CHAT_TO_ROOM,

    // ===== Friend System =====
    GET_FRIEND_REQUESTS,
    GET_FRIEND_LIST,

    // ===== Friend System Error =====
    FRIEND_EXISTS,

    // ===== Room System =====
    CREATE_ROOM,
    START_GAME,
    MATCH_MAKING,
    GET_ROOM_INFO,
    GET_ROOM_BY_ID,
    GET_ALL_ROOMS,

    // ===== Room System Error =====
    ROOM_FULL,
    PLAYER_NOT_READY,
    IN_ROOM,
    ONLY_LEADER,

    // Pings
    PONG,
    SERVER_TICK,

    // Test
    HELLO,


    ERROR;

    /**
     * Messages that should be sent to all connected clients
     */
    public static final Set<ServerMessageType> BROADCAST_MESSAGES = Set.of(
            CHAT_BROADCAST, SERVER_NOTICE, WORLD_UPDATE, MATCH_STARTED, MATCH_ENDED
    );

    /**
     * High priority messages that should be processed immediately
     */
    public static final Set<ServerMessageType> HIGH_PRIORITY = Set.of(
            FORCE_DISCONNECT, ERROR_MESSAGE, TOKEN_EXPIRED, RECONNECT_PROMPT
    );

    /**
     * Game state messages that need to be synchronized
     */
    public static final Set<ServerMessageType> GAME_STATE_MESSAGES = Set.of(
            GAME_STATE, PLAYER_JOINED, PLAYER_LEFT, OBJECT_SPAWNED,
            OBJECT_DESTROYED, WORLD_UPDATE, MATCH_STARTED, MATCH_ENDED
    );

    /**
     * System/heartbeat messages
     */
    public static final Set<ServerMessageType> SYSTEM_MESSAGES = Set.of(
            PONG, SERVER_TICK, FORCE_DISCONNECT, RECONNECT_PROMPT
    );

    public boolean isBroadcast() {
        return BROADCAST_MESSAGES.contains(this);
    }

    public boolean isHighPriority() {
        return HIGH_PRIORITY.contains(this);
    }

    public boolean isGameState() {
        return GAME_STATE_MESSAGES.contains(this);
    }

    public boolean isSystemMessage() {
        return SYSTEM_MESSAGES.contains(this);
    }
}