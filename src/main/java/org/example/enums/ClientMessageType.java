package org.example.enums;

public enum ClientMessageType {
    // ===== Connection =====
    AUTH_REQUEST,
    RECONNECT,
    DISCONNECT,
    HEARTBEAT,
    PING,

    // ===== User Management =====
    GET_USER_INFO,
    GET_USER_BY_ID,

    // ===== Matchmaking =====
    QUEUE_MATCH,
    CANCEL_QUEUE,
    MATCH_FOUND,
    ACCEPT_MATCH,
    REJECT_MATCH,

    // ===== Room =====
    CREATE_ROOM,
    JOIN_ROOM,
    LEAVE_ROOM,
    READY,
    UNREADY,
    START_GAME,
    GET_ROOM_INFO,
    GET_ROOM_BY_ID,
    GET_ALL_ROOMS,

    // ===== Friend System =====
    SEND_FRIEND_REQUEST,
    ACCEPT_FRIEND_REQUEST,
    DECLINE_FRIEND_REQUEST,
    REMOVE_FRIEND,
    GET_FRIEND_REQUESTS,
    GET_FRIEND_LIST,

    // ===== Player Actions =====
    MOVE,
    ATTACK,
    DASH,
    INTERACT,
    CRAFT,
    PICKUP_RESOURCE,
    DROP_RESOURCE,

    // ===== Game Management =====
    CREATE_GAME,
    PAUSE_GAME,
    RESUME_GAME,
    SYNC_STATE,
    REQUEST_GAME_STATE,

    // ===== Post-game =====
    SUBMIT_SCORE,
    GET_RESULTS,
    REPORT_PLAYER,
    GIVE_FEEDBACK,

    // ===== Chat =====
    CHAT_TO_USER,
    CHAT_TO_ROOM,

    // ===== Test =====
    HELLO
}