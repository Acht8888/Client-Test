package org.example;


import org.example.dtos.*;
import org.example.enums.ClientMessageType;
import org.example.enums.ServerMessageType;
import org.example.utils.BinarySerializer;
import org.example.utils.JsonUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;

// Test 2
public class Main {
    private static final String HOST = "localhost";
    private static final int PORT = 9000;

    private final String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjFkMmY5MDVlLTRmZTgtNDcwMy1iNTQxLThhYzk0OTY2MDczMiIsInN1YiI6ImpvaG5fZG9lIiwiaWF0IjoxNzUzMTU0OTAyLCJleHAiOjE3NTMyNDEzMDJ9.HiEN03VkfhfLdQv-vctS482vSflaMhDGFSP4ccQfeAo";
    private final String jwtReconnect = "whkvSSubUV_-7i_TmAfOL_OcXhtfPF5y";

    private static final Random random = new Random();
    private static final JsonUtils jsonUtils = new JsonUtils();

    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private boolean connected = false;
    private Thread listenerThread = null;
    private boolean isBusy = false;

    private ServerUserDTO serverUserDTO = new ServerUserDTO();

    public static void main(String[] args) throws Exception {
        Main client = new Main();
        client.start();
    }

    public void start() throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== TCP Binary Client ===");
        System.out.println("Available commands:");
        System.out.println("0. auth <token> - Authenticate with JWT token");
        System.out.println("1. reconnect <token> <reconnect_token> - Reconnect with tokens");
        System.out.println("100. get_user_info - Get this user information");
        System.out.println("101. get_user_by_id - Get another user information");
        System.out.println("1000. chat_with <targetId(s)> <message> - Send a chat message to another user");
        System.out.println("1001. send_friend_request <targetId> - Send a friend request to another use");
        System.out.println("1002. accept_friend_request <requestId> - Accept friend request");
        System.out.println("1003. decline_friend_request <requestId> - Decline friend request");
        System.out.println("1004. get_friend_requests - Get all friend requests");
        System.out.println("1005. get_friend_list - Get friend list");
        System.out.println("2000. create_room <roomName> <roomType> <maxPlayers> - Create a room");
        System.out.println("2101. get_room_by_id <roomId> - Get information for a room");
        System.out.println("2001. get_all_rooms - Get some information for all rooms");
        System.out.println("2002. join_room <roomId> - Join a room");
        System.out.println("2003. leave_room <roomId> - Leave a room");
        System.out.println("9999. disconnect - Disconnect from server");
        System.out.println("10000. exit - Exit application");
        System.out.println();

        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            String[] parts = input.split(" ", 2);
            String command = parts[0].toLowerCase();

            try {
                switch (command) {
                    case "a_i":
                        handleAuthInstant();
                        break;
                    case "r_i":
                        handleReconnectInstant();
                        break;
                    case "get_user_info":
                        handleGetUserInfo();
                        break;
                    case "get_user_by_id":
                        String userId = parts[1];

                        handleGetUserById(userId);
                        break;
                    case "chat_with":
                        String[] tokens = parts[1].split(" ", 2);
                        String targetIds = tokens[0];
                        String message = tokens[1];

                        handleChat(targetIds, message);
                        break;
                    case "send_friend_request":
                        String targetId = parts[1];

                        handleFriendRequest(targetId);
                        break;
                    case "accept_friend_request":
                        String requestId = parts[1];

                        handleAcceptFriendRequest(UUID.fromString(requestId));
                        break;
                    case "decline_friend_request":
                        requestId = parts[1];

                        handleDeclineFriendRequest(UUID.fromString(requestId));
                        break;
                    case "get_friend_requests":
                        handleGetFriendRequests();
                        break;
//                    case "get_friend_list":
//                        handleGetFriendList();
//                        break;
//                    case "create_room":
//                        tokens = parts[1].split(" ");
//                        String roomName = tokens[0];
//                        String roomType = tokens[1];
//                        String maxPlayers = tokens[2];
//
//                        handleCreateRoomRequest(roomName, roomType, maxPlayers);
//                        break;
//                    case "get_room_by_id":
//                        String roomId = parts[1];
//
//                        handleGetRoomById(roomId);
//                        break;
//                    case "get_all_rooms":
//                        handleGetRoomsRequest();
//                        break;
//                    case "join_room":
//                        roomId = parts[1];
//
//                        handleJoinRoomRequest(roomId);
//                        break;
//                    case "leave_room":
//                        roomId = parts[1];
//
//                        handleLeaveRoomRequest(roomId);
//                        break;
//                    case "disconnect":
//                        handleDisconnect();
//                        break;
//                    case "test":
//                        handleTest();
//                        break;
//                    case "exit":
//                        if (connected) {
//                            handleDisconnect();
//                        }
//                        closeConnection();
//                        System.out.println("Goodbye!");
//                        return;
                    default:
                        System.out.println("Unknown command.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

            while (isBusy) {
                Thread.sleep(800);
            }
        }
    }

    private void handleAuthInstant() throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.AUTH_REQUEST.ordinal();

        ClientAuthDTO clientAuthDTO = new ClientAuthDTO(jwt);

        byte[] serializedData = BinarySerializer.serializeData(clientAuthDTO);

        sendMessage(methodCode, serializedData);
    }

    private void handleReconnectInstant() throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.RECONNECT.ordinal();
        int messageId = random.nextInt(Integer.MAX_VALUE);

        ClientReconnectDTO clientReconnectDTO = new ClientReconnectDTO(jwt, jwtReconnect);

        byte[] serializedData = BinarySerializer.serializeData(clientReconnectDTO);

        sendMessage(methodCode, serializedData);
    }

    private void handleGetUserInfo() throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.GET_USER_INFO.ordinal();

        sendMessage(methodCode, new byte[0]);
    }

    private void handleGetUserById(String userId) throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.GET_USER_BY_ID.ordinal();

        ClientGetUserByIdDTO clientGetUserByIdDTO = new ClientGetUserByIdDTO(UUID.fromString(userId));

        byte[] serializedData = BinarySerializer.serializeData(clientGetUserByIdDTO);

        sendMessage(methodCode, serializedData);
    }

    private void handleChat(String targetIds, String message) throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.SEND_CHAT_MESSAGE.ordinal();

        String[] participantsStr = targetIds.split(",");
        UUID[] uuidArray = new UUID[participantsStr.length];
        for (int i = 0; i < participantsStr.length; i++) {
            uuidArray[i] = UUID.fromString(participantsStr[i]);
        }

        ClientChatDTO clientChatDTO = new ClientChatDTO(uuidArray, message);

        byte[] serializedData = BinarySerializer.serializeData(clientChatDTO);

        sendMessage(methodCode, serializedData);
    }

    private void handleFriendRequest(String targetId) throws  Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.SEND_FRIEND_REQUEST.ordinal();

        ClientSendFriendRequestDTO clientSendFriendRequestDTO = new ClientSendFriendRequestDTO(UUID.fromString(targetId));

        byte[] serializedData = BinarySerializer.serializeData(clientSendFriendRequestDTO);

        sendMessage(methodCode, serializedData);
    }

    private void handleAcceptFriendRequest(UUID requestId) throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.ACCEPT_FRIEND_REQUEST.ordinal();

        ClientAcceptFriendRequestDTO clientAcceptFriendRequestDTO = new ClientAcceptFriendRequestDTO(requestId);

        byte[] serializedData = BinarySerializer.serializeData(clientAcceptFriendRequestDTO);

        sendMessage(methodCode, serializedData);
    }

    private void handleDeclineFriendRequest(UUID requestId) throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.DECLINE_FRIEND_REQUEST.ordinal();

        ClientDeclineFriendRequestDTO clientDeclineFriendRequestDTO = new ClientDeclineFriendRequestDTO(requestId);

        byte[] serializedData = BinarySerializer.serializeData(clientDeclineFriendRequestDTO);

        sendMessage(methodCode, serializedData);
    }

    private void handleGetFriendRequests() throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.GET_PENDING_FRIEND_REQUESTS.ordinal();

        sendMessage(methodCode, new byte[0]);
    }
//
//    private void handleGetFriendList() throws Exception {
//        if (!ensureConnection()) return;
//
//        short methodCode = (short) ClientMessageType.GET_FRIEND_LIST.ordinal();
//        int messageId = random.nextInt(Integer.MAX_VALUE);
//
//        Map<String, String> payloadMap = new HashMap<>();
//
//        sendMessage(methodCode, messageId, payloadMap);
//    }
//
//    private void handleCreateRoomRequest(String roomName, String roomType, String maxPlayers) throws Exception {
//        if (!ensureConnection()) return;
//
//        short methodCode = (short) ClientMessageType.CREATE_ROOM.ordinal();
//        int messageId = random.nextInt(Integer.MAX_VALUE);
//
//        Map<String, String> payloadMap = new HashMap<>();
//        payloadMap.put("roomName", roomName);
//        payloadMap.put("roomType", roomType);
//        payloadMap.put("maxPlayers", maxPlayers);
//
//        sendMessage(methodCode, messageId, payloadMap);
//    }
//
//    private void handleGetRoomsRequest() throws Exception {
//        if (!ensureConnection()) return;
//
//        short methodCode = (short) ClientMessageType.GET_ALL_ROOMS.ordinal();
//        int messageId = random.nextInt(Integer.MAX_VALUE);
//
//        Map<String, String> payloadMap = new HashMap<>();
//
//        sendMessage(methodCode, messageId, payloadMap);
//    }
//
//    private void handleGetRoomById(String roomId) throws Exception {
//        if (!ensureConnection()) return;
//
//        short methodCode = (short) ClientMessageType.GET_ROOM_BY_ID.ordinal();
//        int messageId = random.nextInt(Integer.MAX_VALUE);
//
//        Map<String, String> payloadMap = new HashMap<>();
//        payloadMap.put("roomId", roomId);
//
//        sendMessage(methodCode, messageId, payloadMap);
//    }
//
//    private void handleJoinRoomRequest(String roomId) throws Exception {
//        if (!ensureConnection()) return;
//
//        short methodCode = (short) ClientMessageType.JOIN_ROOM.ordinal();
//        int messageId = random.nextInt(Integer.MAX_VALUE);
//
//        Map<String, String> payloadMap = new HashMap<>();
//        payloadMap.put("roomId", roomId);
//
//        sendMessage(methodCode, messageId, payloadMap);
//    }
//
//    private void handleLeaveRoomRequest(String roomId) throws Exception {
//        if (!ensureConnection()) return;
//
//        short methodCode = (short) ClientMessageType.LEAVE_ROOM.ordinal();
//        int messageId = random.nextInt(Integer.MAX_VALUE);
//
//        Map<String, String> payloadMap = new HashMap<>();
//        payloadMap.put("roomId", roomId);
//
//        sendMessage(methodCode, messageId, payloadMap);
//    }
//
//    private void handleDisconnect() throws Exception {
//        if (!connected) {
//            System.out.println("Not connected to server");
//            return;
//        }
//
//        short methodCode = (short) ClientMessageType.DISCONNECT.ordinal(); // Disconnect type
//        int messageId = random.nextInt(Integer.MAX_VALUE);
//
//        Map<String, String> payloadMap = new HashMap<>();
//
//        sendMessage(methodCode, messageId, payloadMap);
//
//        closeConnection();
//    }
//
//    private void handleTest() throws Exception {
//        if (!ensureConnection()) return;
//
//        short methodCode = (short) ClientMessageType.HELLO.ordinal();
//        int messageId = random.nextInt(Integer.MAX_VALUE);
//
//        String[] array = {"One", "Two", "Three", "Four"};
//        List<String> arrayListStr = List.of(array);
//        System.out.println("String[] array: " + arrayListStr);
//
//        String arrayConverted = jsonUtils.convertObjectToJsonString(array);
//        System.out.println("arrayConverted: " + arrayConverted);
//
//        Map<String, String> payloadMap = new HashMap<>();
//        payloadMap.put("str", arrayConverted);
//
//        sendMessage(methodCode, messageId, payloadMap);
//    }
//
    private void processServerMessage(short responseType, byte[] payloadBytes) throws Exception {
        if (responseType == ServerMessageType.AUTH_SUCCESS.ordinal()) {
            ServerAuthSuccessDTO serverAuthSuccessDTO = BinarySerializer.deserializeData(payloadBytes, ServerAuthSuccessDTO.class);

            System.out.println("[Server message]");
            System.out.println("- Response: " + serverAuthSuccessDTO.getResponse());
            System.out.println("- Reconnect Token: " + serverAuthSuccessDTO.getReconnectToken());
        } else if (responseType == ServerMessageType.AUTH_FAIL.ordinal()) {
            ServerAuthFailDTO serverAuthFailDTO = BinarySerializer.deserializeData(payloadBytes, ServerAuthFailDTO.class);

            System.out.println("[Server message]");
            System.out.println("- Response: " + serverAuthFailDTO.getResponse());
        } else if (responseType == ServerMessageType.FRIEND_REQUEST.ordinal()) {
            processFriendRequest(payloadBytes);
        }
        else if (responseType == ServerMessageType.FRIEND_ACCEPT.ordinal()) {
            processAcceptFriendRequest(payloadBytes);
        }
        else if (responseType == ServerMessageType.FRIEND_DECLINE.ordinal()) {
            processDeclineFriendRequest(payloadBytes);
        }
        else if (responseType == ServerMessageType.PRIVATE_MESSAGE.ordinal()) {
            processChat(payloadBytes);
        }
        else if (responseType == ServerMessageType.GET_USER_INFO.ordinal()) {
            processGetUserInfo(payloadBytes);
        } else if (responseType == ServerMessageType.GET_USER_BY_ID.ordinal()) {
            processGetUserById(payloadBytes);
        }
//        else if (responseType == ServerMessageType.CREATE_ROOM.ordinal()) {
//            processCreateRoom(payloadStr);
//        } else if (responseType == ServerMessageType.JOIN_ROOM.ordinal()) {
//            processJoinRoom(payloadStr);
//        } else if (responseType == ServerMessageType.GET_ROOM_BY_ID.ordinal()) {
//            processGetRoomById(payloadStr);
//        } else if (responseType == ServerMessageType.GET_ALL_ROOMS.ordinal()) {
//            processGetRooms(payloadStr);
//        } else if (responseType == ServerMessageType.HELLO.ordinal()) {
//            processTest(payloadStr);
//        }
        else if (responseType == ServerMessageType.GET_PENDING_FRIEND_REQUESTS.ordinal()) {
            processGetFriendRequests(payloadBytes);
        }
//        else if (responseType == ServerMessageType.GET_FRIEND_LIST.ordinal()) {
//            processGetFriendList(payloadStr);
//        }
    }

    private void processFriendRequest(byte[] payloadBytes) throws Exception {
        ServerSendFriendRequestDTO serverSendFriendRequestDTO = BinarySerializer.deserializeData(payloadBytes, ServerSendFriendRequestDTO.class);

        System.out.println("[Friend Request]");
        System.out.println("- Request Id: " + serverSendFriendRequestDTO.getRequestId());
        System.out.println("- Requester Username: " + serverSendFriendRequestDTO.getRequesterUsername());

    }

    private void processAcceptFriendRequest(byte[] payloadBytes) throws Exception {
        ServerAcceptFriendRequestDTO serverAcceptFriendRequestDTO = BinarySerializer.deserializeData(payloadBytes, ServerAcceptFriendRequestDTO.class);

        System.out.println("[Accept Friend Request]");
        System.out.println("- Target Username: " + serverAcceptFriendRequestDTO.getTargetUsername());
    }

    private void processDeclineFriendRequest(byte[] payloadBytes) throws Exception {
        ServerDeclineFriendRequestDTO serverDeclineFriendRequestDTO = BinarySerializer.deserializeData(payloadBytes, ServerDeclineFriendRequestDTO.class);

        System.out.println("[Decline Friend Request]");
        System.out.println("- Target Username: " + serverDeclineFriendRequestDTO.getTargetUsername());
    }

    private void processGetFriendRequests(byte[] payloadBytes) throws Exception {
        ServerGetFriendRequestDTO serverGetFriendRequestDTO = BinarySerializer.deserializeData(payloadBytes, ServerGetFriendRequestDTO.class);

        List<ServerFriendDTO> serverFriendDTOList = serverGetFriendRequestDTO.getFriendRequestList();

        System.out.println("[Pending Friend Requests]");

        for (ServerFriendDTO serverFriendDTO : serverFriendDTOList) {
            System.out.println("- " + serverFriendDTO.getId() + " " + serverFriendDTO.getFriendId());
        }
    }
//
//    private void processGetFriendList(Map<String, String> payloadStr) throws Exception {
//        String serverFriendDTOListJsonString = payloadStr.get("friendList");
//
//        List<ServerFriendDTO> serverFriendDTOList = jsonUtils.convertJsonStringToObject(serverFriendDTOListJsonString, TypeFactory.defaultInstance().constructCollectionType(List.class, ServerFriendDTO.class));
//
//        System.out.println("[Friend List]");
//
//        for (ServerFriendDTO serverFriendDTO : serverFriendDTOList) {
//            System.out.println("- " + serverFriendDTO.getId() + " " + serverFriendDTO.getFriendId());
//        }
//    }
//
//    private void processChat(Map<String, String> payloadStr) throws Exception {
//        String chatMessage = payloadStr.get("chatMessage");
//
//        System.out.println("Chat: " + chatMessage);
//    }
//
    private void processGetUserInfo(byte[] payloadBytes) throws Exception {
        ServerUserDTO serverUserDTO = BinarySerializer.deserializeData(payloadBytes, ServerUserDTO.class);

        System.out.println("[User Info]");
        System.out.println("- Id: " + serverUserDTO.getId());
        System.out.println("- Username: " + serverUserDTO.getUsername());
        System.out.println("- Display Name: " + serverUserDTO.getDisplayName());
        System.out.println("- Level: " + serverUserDTO.getLevel());
    }

    private void processGetUserById(byte[] payloadBytes) throws Exception {
        ServerUserDTO serverUserDTO = BinarySerializer.deserializeData(payloadBytes, ServerUserDTO.class);

        System.out.println("[User Info]");
        System.out.println("- Id: " + serverUserDTO.getId());
        System.out.println("- Username: " + serverUserDTO.getUsername());
        System.out.println("- Display Name: " + serverUserDTO.getDisplayName());
        System.out.println("- Level: " + serverUserDTO.getLevel());
    }

    private void processChat(byte[] payloadBytes) throws Exception {
        ServerChatDTO serverChatDTO = BinarySerializer.deserializeData(payloadBytes, ServerChatDTO.class);

        System.out.println("[Chatting]");
        System.out.println("- Message: " + serverChatDTO.getChatMessage());
    }
//
//    private void processCreateRoom(Map<String, String> payloadStr) {
//        UUID roomId = UUID.fromString(payloadStr.get("roomId"));
//
//        System.out.println("The new server Id is: " + roomId);
//    }
//
//    private void processJoinRoom(Map<String, String> payloadStr) {
//        UUID roomId = UUID.fromString(payloadStr.get("roomId"));
//
//        System.out.println("The server Id is: " + roomId);
//    }
//
//    private void processGetRoomById(Map<String, String> payloadStr) {
//        String serverRoomDTOJsonString = payloadStr.get("room");
//
//        ServerRoomDTO serverRoomDTO = jsonUtils.convertJsonStringToObject(serverRoomDTOJsonString, TypeFactory.defaultInstance().constructType(ServerRoomDTO.class));
//
//        System.out.println("[Room]");
//
//        System.out.println("- " + serverRoomDTO.getId() + " " + serverRoomDTO.getName());
//    }
//
//    private void processGetRooms(Map<String, String> payloadStr) {
//        String serverRoomDTOListJsonString = payloadStr.get("rooms");
//
//        List<ServerRoomDTO> serverRoomDTOList = jsonUtils.convertJsonStringToObject(serverRoomDTOListJsonString, TypeFactory.defaultInstance().constructCollectionType(List.class, ServerRoomDTO.class));
//
//        System.out.println("[Rooms]");
//
//        for (ServerRoomDTO serverRoomDTO : serverRoomDTOList) {
//            System.out.println("- " + serverRoomDTO.getId() + " " + serverRoomDTO.getName());
//        }
//    }
//
//    private void processTest(Map<String, String> payloadStr) throws Exception {
//        String str = payloadStr.get("str");
//
//        List<String> arrayReconverted = jsonUtils.convertJsonStringToObject(str, TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
//        System.out.println("arrayReconverted: " + arrayReconverted);
//    }

    private Thread createListenerThread() {
        return new Thread(() -> {
            try {
                System.out.println("Listener Thread created");

                while (connected && socket != null && !socket.isClosed()) {
                    byte[] lengthBytes = in.readNBytes(2);

                    if (lengthBytes.length < 2) {
                        System.out.println("Connection closed by server.");
                        closeConnection();
                        break;
                    }

                    int totalLength = ByteBuffer.wrap(lengthBytes).getShort() & 0xFFFF;

                    System.out.println("totalLength: " + totalLength);

                    byte[] responseBytes = in.readNBytes(totalLength);

                    isBusy = true;

                    ByteBuffer buffer = ByteBuffer.wrap(responseBytes);
                    short responseType = buffer.getShort();
                    short statusCode = buffer.getShort();

                    byte[] payloadBytes = new byte[totalLength - 4];
                    buffer.get(payloadBytes);

                    System.out.println("[Server Message]");
                    System.out.println("- Type: " + responseType);
                    System.out.println("- Status: " + statusCode);
                    System.out.println("- Payload Bytes: " + Arrays.toString(payloadBytes));

                    processServerMessage(responseType, payloadBytes);

                    isBusy = false;
                }
            } catch (Exception e) {
                System.out.println("Error in server listener: " + e.getMessage());
                e.printStackTrace();
                closeConnection();
            } finally {
                System.out.println("Listener thread ended.");
            }
        });
    }

    private void sendMessage(short methodCode, byte[] serializedData) throws Exception {
        System.out.println("Sending message");

        int totalLength = 2 + serializedData.length;    // length field = method code + data

        ByteBuffer buffer = ByteBuffer.allocate(totalLength + 2);   // totalLength + totalLength.size
        buffer.putShort((short) totalLength);
        buffer.putShort(methodCode);
        buffer.put(serializedData);

        System.out.println("Payload Bytes: " + Arrays.toString(buffer.array()));

        out.write(buffer.array());
        out.flush();

        Thread.sleep(800);
    }

    private boolean ensureConnection() throws Exception {
        if (connected && socket != null && !socket.isClosed()) {
            return true;
        }

        try {
            socket = new Socket(HOST, PORT);
            out = socket.getOutputStream();
            in = socket.getInputStream();
            connected = true;
            System.out.println("Connected to " + HOST + ":" + PORT);

            if (listenerThread == null || !listenerThread.isAlive()) {
                listenerThread = createListenerThread();
                listenerThread.start();
            } else {
                System.out.println("Failed to create thread");
            }

            return true;
        } catch (Exception e) {
            System.out.println("Failed to connect: " + e.getMessage());
            return false;
        }
    }

    private void closeConnection() {
        connected = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Connection closed");
        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

}