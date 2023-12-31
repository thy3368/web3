package com.example.tweb3demo1.grass.applicationservice;

import com.example.tweb3demo1.grass.domain.GrassConnection;
import com.example.tweb3demo1.grass.domain.Message;
import com.example.tweb3demo1.grass.domain.repo.ConnectionRepo;
import com.example.tweb3demo1.grass.domain.repo.MessageRepo;
import com.example.tweb3demo1.grass.outboundAdapter.repo.ConnectionRepoImpl;
import com.example.tweb3demo1.grass.outboundAdapter.repo.MessageRepoImpl;
import org.java_websocket.client.WebSocketClient;

import static com.squareup.okhttp.internal.Internal.logger;

public class MessageApplicationService {


//    private static final String userId = "30901ea9-bf85-43b0-954c-206450000284";
//    private static final String browserId = "bfb1afbc-7727-53d9-8465-c7005754221e";

    private final MessageRepo messageRepo = new MessageRepoImpl();
    private final ConnectionRepo connectionRepo = new ConnectionRepoImpl();


    public void handlePong(WebSocketClient client, Message.PongRequestMessage pongRequestMessage) {

        Message.PongResponse pongResponse = pongRequestMessage.createResponse();
        messageRepo.send(client, pongResponse);

    }

    public void handleAuth(String userId, String ip, WebSocketClient client, Message.AuthRequestMessage authRequestMessage) {

        Message.AuthResponse authResponse = authRequestMessage.createResponse(userId, ip);
        GrassConnection grassConnection = new GrassConnection();
        grassConnection.setUserId(userId);
        grassConnection.setIp(ip);

        GrassConnection newGrassConnection = connectionRepo.query(grassConnection);
        newGrassConnection.setAuthed(true);
        connectionRepo.save(newGrassConnection);

        messageRepo.send(client, authResponse);
    }

    public void sendPing(String userId, String ip, WebSocketClient client) {
        GrassConnection grassConnection = new GrassConnection();
        grassConnection.setUserId(userId);
        grassConnection.setIp(ip);
        GrassConnection newGrassConnection = connectionRepo.query(grassConnection);

        logger.info(grassConnection.getUserId() + "准备ping");
        if (newGrassConnection.isAuthed()) {
            Message.PingMessage message = Message.ping();
            messageRepo.send(client, message);
        }

    }
}
