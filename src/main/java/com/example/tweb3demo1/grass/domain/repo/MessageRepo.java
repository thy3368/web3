package com.example.tweb3demo1.grass.domain.repo;

import com.example.tweb3demo1.grass.domain.Message;
import org.java_websocket.client.WebSocketClient;

public interface MessageRepo {

    void send(WebSocketClient webSocketClient, Message message);


}
