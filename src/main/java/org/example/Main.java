package org.example;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dtos.*;
import org.example.enums.*;
import org.example.utils.BinarySerializer;
import org.example.utils.JsonUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

// Test 2
public class Main {
    private static final String HOST = "localhost";
    private static final int PORT = 9000;

    private String jwt = "";
    private String jwtReconnect = "";

    private static final Random random = new Random();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private boolean connected = false;
    private Thread listenerThread = null;
    private boolean isBusy = false;

    public static void main(String[] args) throws Exception {
        Main client = new Main();
        client.start();
    }

    public void start() throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== TCP Binary Client ===");

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
                    case "register":
                        String[] tokens = parts[1].split(" ");
                        String username = tokens[0];
                        String password = tokens[1];
                        String confirmPassword = tokens[2];
                        String displayName = tokens[3];

                        handleRegister(username, password, confirmPassword, displayName);
                        break;
                    case "login":
                        tokens = parts[1].split(" ");
                        username = tokens[0];
                        password = tokens[1];

                        handleLogin(username, password);
                        break;
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

                        handleGetUserById(UUID.fromString(userId));
                        break;
                    case "send_friend_request":
                        String targetId = parts[1];

                        handleFriendRequest(UUID.fromString(targetId));
                        break;
                    case "accept_friend_request":
                        String requestId = parts[1];

                        handleAcceptFriendRequest(UUID.fromString(requestId));
                        break;
                    case "decline_friend_request":
                        requestId = parts[1];

                        handleDeclineFriendRequest(UUID.fromString(requestId));
                        break;
                    case "remove_friend":
                        requestId = parts[1];

                        handleRemoveFriend(UUID.fromString(requestId));
                        break;
                    case "get_friend_requests":
                        handleGetFriendRequests();
                        break;
                    case "get_friend_list":
                        handleGetFriendList();
                        break;
                    case "create_room":
                        tokens = parts[1].split(" ");
                        String roomName = tokens[0];
                        String gameMode = tokens[1];
                        String roomType = tokens[2];
                        String maxPlayers = tokens[3];


                        handleCreateRoomRequest(roomName, gameMode, roomType, maxPlayers);
                        break;
                    case "get_room_info":
                        handleGetRoomInfo();
                        break;
                    case "get_room_by_id":
                        String roomId = parts[1];

                        handleGetRoomById(UUID.fromString(roomId));
                        break;
                    case "get_all_rooms":
                        handleGetRoomsRequest();
                        break;
                    case "join_room":
                        roomId = parts[1];

                        handleJoinRoomRequest(UUID.fromString(roomId));
                        break;
                    case "leave_room":
                        handleLeaveRoomRequest();
                        break;
                    case "ready":
                        handleReady();
                        break;
                    case "unready":
                        handleUnready();
                        break;
                    case "start_game":
                        handleStartGame();
                        break;
                    case "chat_to_user":
                        tokens = parts[1].split(" ", 2);
                        targetId = tokens[0];
                        String message = tokens[1];

                        handleChatUser(UUID.fromString(targetId), message);
                        break;
                    case "chat_to_room":
                        message = parts[1];

                        handleChatRoom(message);
                        break;
                    case "disconnect":
                        handleDisconnect();
                        break;
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

    private void handleRegister(String username, String password, String confirmPassword, String displayName) throws Exception {
        URL url = new URL("http://localhost:8080/users/register");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        ClientUserRegistrationDTO registrationDTO = new ClientUserRegistrationDTO(
                username,
                password,
                confirmPassword,
                displayName
        );

        String jsonInput = objectMapper.writeValueAsString(registrationDTO);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        System.out.println("HTTP Status: " + responseCode);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                responseCode >= 200 && responseCode < 300 ? connection.getInputStream() : connection.getErrorStream()
        ));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        JsonNode jsonResponse = objectMapper.readTree(response.toString());

        String message = jsonResponse.path("message").asText();
        System.out.println("Response message: " + message);
    }

    private void handleLogin(String username, String password) throws Exception {
        URL url = new URL("http://localhost:8080/users/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        ClientUserLoginDTO loginDTO = new ClientUserLoginDTO(
          username,
          password
        );

        String jsonInput = objectMapper.writeValueAsString(loginDTO);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        System.out.println("HTTP Status: " + responseCode);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                responseCode >= 200 && responseCode < 300 ? connection.getInputStream() : connection.getErrorStream()
        ));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        JsonNode jsonResponse = objectMapper.readTree(response.toString());

        String message = jsonResponse.path("message").asText();
        System.out.println("Response message: " + message);

        String token = jsonResponse.path("data").path("token").asText();
        System.out.println("Token: " + token);

        this.jwt = token;
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

    private void handleGetUserById(UUID userId) throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.GET_USER_BY_ID.ordinal();

        ClientGetUserByIdDTO clientGetUserByIdDTO = new ClientGetUserByIdDTO(userId);

        byte[] serializedData = BinarySerializer.serializeData(clientGetUserByIdDTO);

        sendMessage(methodCode, serializedData);
    }

    private void handleFriendRequest(UUID targetId) throws  Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.SEND_FRIEND_REQUEST.ordinal();

        ClientSendFriendRequestDTO clientSendFriendRequestDTO = new ClientSendFriendRequestDTO(targetId);

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

    private void handleRemoveFriend(UUID requestId) throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.REMOVE_FRIEND.ordinal();

        ClientRemoveFriendDTO clientRemoveFriendDTO = new ClientRemoveFriendDTO(requestId);

        byte[] serializedData = BinarySerializer.serializeData(clientRemoveFriendDTO);

        sendMessage(methodCode, serializedData);
    }

    private void handleGetFriendRequests() throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.GET_FRIEND_REQUESTS.ordinal();

        sendMessage(methodCode, new byte[0]);
    }

    private void handleGetFriendList() throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.GET_FRIEND_LIST.ordinal();

        sendMessage(methodCode, new byte[0]);
    }

    private void handleCreateRoomRequest(String roomName, String gameMode, String roomType, String maxPlayers) throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.CREATE_ROOM.ordinal();

        short gameModeShort = 0;
        if (gameMode.equalsIgnoreCase("co_op")) {
            gameModeShort = 0;
        } else if (gameMode.equalsIgnoreCase("pvp")) {
            gameModeShort = 1;
        }

        short roomTypeShort = 0;
        if (roomType.equalsIgnoreCase("public")) {
            roomTypeShort = 0;
        } else if (roomType.equalsIgnoreCase("private")) {
            roomTypeShort = 1;
        }

        ClientCreateRoomDTO clientCreateRoomDTO = new ClientCreateRoomDTO(roomName, gameModeShort, roomTypeShort, Integer.parseInt(maxPlayers));

        byte[] serializedData = BinarySerializer.serializeData(clientCreateRoomDTO);

        sendMessage(methodCode, serializedData);
    }

    private void handleGetRoomInfo() throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.GET_ROOM_INFO.ordinal();

        sendMessage(methodCode, new byte[0]);
    }

    private void handleGetRoomById(UUID roomId) throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.GET_ROOM_BY_ID.ordinal();

        ClientGetRoomByIdDTO getRoomByIdDTO = new ClientGetRoomByIdDTO(roomId);

        byte[] serializedData = BinarySerializer.serializeData(getRoomByIdDTO);

        sendMessage(methodCode, serializedData);
    }

    private void handleGetRoomsRequest() throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.GET_ALL_ROOMS.ordinal();

        sendMessage(methodCode, new byte[0]);
    }

    private void handleJoinRoomRequest(UUID roomId) throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.JOIN_ROOM.ordinal();

        ClientJoinRoomDTO clientJoinRoomDTO = new ClientJoinRoomDTO(roomId);

        byte[] serializedData = BinarySerializer.serializeData(clientJoinRoomDTO);

        sendMessage(methodCode, serializedData);
    }

    private void handleLeaveRoomRequest() throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.LEAVE_ROOM.ordinal();

        sendMessage(methodCode, new byte[0]);
    }

    private void handleReady() throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.READY.ordinal();

        sendMessage(methodCode, new byte[0]);
    }

    private void handleUnready() throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.UNREADY.ordinal();

        sendMessage(methodCode, new byte[0]);
    }

    private void handleStartGame() throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.START_GAME.ordinal();

        sendMessage(methodCode, new byte[0]);
    }

    private void handleChatUser(UUID targetId, String message) throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.CHAT_TO_USER.ordinal();

        ClientChatUserDTO clientChatUserDTO = new ClientChatUserDTO(targetId, message);

        byte[] serializedData = BinarySerializer.serializeData(clientChatUserDTO);

        sendMessage(methodCode, serializedData);
    }

    private void handleChatRoom(String message) throws Exception {
        if (!ensureConnection()) return;

        short methodCode = (short) ClientMessageType.CHAT_TO_ROOM.ordinal();

        ClientChatRoomDTO clientChatRoomDTO = new ClientChatRoomDTO(message);

        byte[] serializedData = BinarySerializer.serializeData(clientChatRoomDTO);

        sendMessage(methodCode, serializedData);
    }

    private void handleDisconnect() throws Exception {
        if (!connected) {
            System.out.println("Not connected to server");
            return;
        }

        short methodCode = (short) ClientMessageType.DISCONNECT.ordinal();

        sendMessage(methodCode, new byte[0]);

        closeConnection();
    }

    private void processServerMessage(short responseType, byte[] payloadBytes) throws Exception {
        if (responseType == ServerMessageType.AUTH_SUCCESS.ordinal()) {
            processAuthSuccess(payloadBytes);
        } else if (responseType == ServerMessageType.AUTH_FAIL.ordinal()) {
            processAuthFail(payloadBytes);
        } else if (responseType == ServerMessageType.GET_USER_INFO.ordinal()) {
            processGetUserInfo(payloadBytes);
        } else if (responseType == ServerMessageType.GET_USER_BY_ID.ordinal()) {
            processGetUserById(payloadBytes);
        } else if (responseType == ServerMessageType.GET_FRIEND_REQUESTS.ordinal()) {
            processGetFriendRequests(payloadBytes);
        } else if (responseType == ServerMessageType.GET_FRIEND_LIST.ordinal()) {
            processGetFriendList(payloadBytes);
        } else if (responseType == ServerMessageType.FRIEND_EXISTS.ordinal()) {
            processFriendExists(payloadBytes);
        } else if (responseType == ServerMessageType.CHAT_TO_USER.ordinal()) {
            processChatUser(payloadBytes);
        } else if (responseType == ServerMessageType.CHAT_TO_ROOM.ordinal()) {
            processChatRoom(payloadBytes);
        } else if (responseType == ServerMessageType.CREATE_ROOM.ordinal()) {
            processCreateRoom(payloadBytes);
        } else if (responseType == ServerMessageType.GET_ROOM_INFO.ordinal()) {
            processGetRoomInfo(payloadBytes);
        } else if (responseType == ServerMessageType.GET_ROOM_BY_ID.ordinal()) {
            processGetRoomById(payloadBytes);
        } else if (responseType == ServerMessageType.GET_ALL_ROOMS.ordinal()) {
            processGetRooms(payloadBytes);
        } else if (responseType == ServerMessageType.ROOM_FULL.ordinal()) {
            processRoomFull(payloadBytes);
        }
    }

    private void processAuthSuccess(byte[] payloadBytes) throws Exception {
        ServerAuthSuccessDTO serverAuthSuccessDTO = BinarySerializer.deserializeData(payloadBytes, ServerAuthSuccessDTO.class);

        System.out.println("[Auth Success]");
        System.out.println("- Reconnect Token: " + serverAuthSuccessDTO.getReconnectToken());

        this.jwtReconnect = serverAuthSuccessDTO.getReconnectToken();
    }

    private void processAuthFail(byte[] payloadBytes) throws Exception {
        ServerAuthFailDTO serverAuthFailDTO = BinarySerializer.deserializeData(payloadBytes, ServerAuthFailDTO.class);

        System.out.println("[Auth Fail]");
        System.out.println("- Response: " + serverAuthFailDTO.getResponse());
    }

    private void processGetFriendRequests(byte[] payloadBytes) throws Exception {
        ServerGetFriendRequestDTO serverGetFriendRequestDTO = BinarySerializer.deserializeData(payloadBytes, ServerGetFriendRequestDTO.class);

        List<ServerFriendDTO> serverFriendDTOList = serverGetFriendRequestDTO.getFriendRequestList();

        System.out.println("[Pending Friend Requests]");

        for (ServerFriendDTO serverFriendDTO : serverFriendDTOList) {
            System.out.println("- " + serverFriendDTO.getId() + " " + serverFriendDTO.getFriendId() + " " + serverFriendDTO.getFriendDisplayName());
        }
    }

    private void processGetFriendList(byte[] payloadBytes) throws Exception {
        ServerGetFriendListDTO serverGetFriendListDTO = BinarySerializer.deserializeData(payloadBytes, ServerGetFriendListDTO.class);

        List<ServerFriendDTO> serverFriendDTOList = serverGetFriendListDTO.getFriendList();

        System.out.println("[Friend List]");

        for (ServerFriendDTO serverFriendDTO : serverFriendDTOList) {
            System.out.println("- " + serverFriendDTO.getId() + " " + serverFriendDTO.getFriendId() + " " + serverFriendDTO.getFriendDisplayName());
        }
    }

    private void processFriendExists(byte[] payloadBytes) throws Exception {
        System.out.println("[Error]");
        System.out.println("- Friend exists");
    }

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

    private void processChatUser(byte[] payloadBytes) throws Exception {
        ServerChatUserDTO serverChatUserDTO = BinarySerializer.deserializeData(payloadBytes, ServerChatUserDTO.class);

        System.out.println("[Chatting User]");
        System.out.println("- Requester Id: " + serverChatUserDTO.getRequesterId());
        System.out.println("- Requester Name: " + serverChatUserDTO.getRequesterDisplayName());
        System.out.println("- Message: " + serverChatUserDTO.getMessage());
    }

    private void processChatRoom(byte[] payloadBytes) throws Exception {
        ServerChatRoomDTO serverChatRoomDTO = BinarySerializer.deserializeData(payloadBytes, ServerChatRoomDTO.class);

        System.out.println("[Chatting Room]");
        System.out.println("- Requester Id: " + serverChatRoomDTO.getRequesterId());
        System.out.println("- Requester Name: " + serverChatRoomDTO.getRequesterDisplayName());
        System.out.println("- Message: " + serverChatRoomDTO.getMessage());
    }

    private void processCreateRoom(byte[] payloadBytes) throws Exception {
        ServerCreateRoomDTO serverCreateRoomDTO = BinarySerializer.deserializeData(payloadBytes, ServerCreateRoomDTO.class);

        UUID roomId = serverCreateRoomDTO.getRoomId();

        System.out.println("[Created Room]");
        System.out.println("- Room Id: " + roomId);
    }

    private void processGetRoomInfo(byte[] payloadBytes) throws Exception {
        ServerGetRoomInfoDTO serverGetRoomInfoDTO = BinarySerializer.deserializeData(payloadBytes, ServerGetRoomInfoDTO.class);

        ServerRoomDTO serverRoomDTO = serverGetRoomInfoDTO.getRoom();

        System.out.println("[Room]");
        printServerRoomDTO(serverRoomDTO);
    }

    private void processGetRoomById(byte[] payloadBytes) throws Exception {
        ServerGetRoomByIdDTO serverGetRoomByIdDTO = BinarySerializer.deserializeData(payloadBytes, ServerGetRoomByIdDTO.class);

        ServerRoomDTO serverRoomDTO = serverGetRoomByIdDTO.getRoom();

        System.out.println("[Room]");
        printServerRoomDTO(serverRoomDTO);
    }

    private void processGetRooms(byte[] payloadBytes) throws Exception {
        ServerGetAllRoomsDTO serverGetAllRoomsDTO = BinarySerializer.deserializeData(payloadBytes, ServerGetAllRoomsDTO.class);

        List<ServerRoomDTO> serverRoomDTOList = serverGetAllRoomsDTO.getRoomList();

        System.out.println("[Rooms]");
        for (ServerRoomDTO serverRoomDTO : serverRoomDTOList) {
            printServerRoomDTO(serverRoomDTO);
        }
    }

    private void processRoomFull(byte[] payloadBytes) throws Exception {
        System.out.println("[Error]");
        System.out.println("- Room is full");
    }

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

                    byte[] responseBytes = in.readNBytes(totalLength);

                    startWork();

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

                    stopWork();
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

    private void startWork() {
        isBusy = true;
    }

    private void stopWork() {
        isBusy = false;
    }

    private void printServerRoomDTO(ServerRoomDTO serverRoomDTO) {
        System.out.println("- "
                + serverRoomDTO.getId()
                + " " + serverRoomDTO.getName()
                + " " + serverRoomDTO.getCurrentPlayers()
                + "/" + serverRoomDTO.getMaxPlayers()
                + " " + GameMode.fromShort(serverRoomDTO.getMode())
                + " " + RoomType.fromShort(serverRoomDTO.getType())
        );
        for (ServerRoomPlayerDTO serverRoomPlayerDTO : serverRoomDTO.getPlayerList()) {
            System.out.println(
                    "+ " + serverRoomPlayerDTO.getPlayerId() + " "
                            + serverRoomPlayerDTO.getPlayerDisplayName() + " "
                            + PlayerRole.fromShort(serverRoomPlayerDTO.getPlayerRole()) + " "
                            + PlayerStatus.fromShort(serverRoomPlayerDTO.getPlayerStatus()));
        }
    }

}