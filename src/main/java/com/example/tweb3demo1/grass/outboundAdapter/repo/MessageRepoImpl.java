package com.example.tweb3demo1.grass.outboundAdapter.repo;

import com.alibaba.fastjson.JSONObject;
import com.example.tweb3demo1.grass.domain.Message;
import com.example.tweb3demo1.grass.domain.repo.MessageRepo;
import org.java_websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageRepoImpl implements MessageRepo {


    private static final Logger logger = LoggerFactory.getLogger(MessageRepoImpl.class);


    @Override
    public void send(WebSocketClient webSocketClient, Message message) {

        String jsonString = JSONObject.toJSONString(message);

        if (webSocketClient.isOpen()) {
            webSocketClient.send(jsonString);
            logger.info("Send==========" + jsonString);
        }


    }
}
