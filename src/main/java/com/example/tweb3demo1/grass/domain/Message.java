package com.example.tweb3demo1.grass.domain;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class Message {


    public static PingMessage ping() {

        PingMessage message = new PingMessage();
        return message;

    }


    public static PerformHttpRequestResponse performHttpRequestResponse(String id, String originAction) {

        PerformHttpRequestResponse message = new PerformHttpRequestResponse();
        message.setId(id);
        message.setOriginAction(originAction);
        return message;

    }

    @Data
    public static class PingMessage extends Message {

        private final String id = String.valueOf(UUID.randomUUID());
        private final String version = "1.0.0";
        private final String action = "PING";
        private final Map data = new HashMap<>();

//
//        id: uuidv4(),
//        version: "1.0.0",
//        action: "PING",
//        data: {},


    }


//
//        const authenticationResponse = {
//        browser_id,
//                user_id: userId,
//                user_agent: USER_AGENT,
//                timestamp: getUnixTimestamp(),
//                device_type: deviceType,
//    };
//


    @Data
    public static class PerformHttpRequestResponse extends RpcResponseMessage {


    }


    @Data
    public static class RpcResponseMessage extends Message {

        private final String version = "1.0.0";
        private String id;
        @JSONField(name = "origin_action")
        private String originAction;
        private Object result = new Object();


    }


    @Data
    public static class AuthResponse extends RpcResponseMessage {

    }

    @Data
    public static class PongResponse extends RpcResponseMessage {

    }

    @Data
    public static class AuthResult {
        @JSONField(name = "user_agent")
        private final String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";
        private final long timestamp = (long) Math.floor(new Date().getTime() / 1000);
        @JSONField(name = "device_type")
        private final String deviceType = "vps, darwin, 21.2.0";
        @JSONField(name = "user_id")
        private String userId;
        @JSONField(name = "browser_id")
        private String browserId;


    }

    @Data
    public static class PerformHttpRequestResult {
        private String url;
        private String status;
        @JSONField(name = "status_text")
        private String statusText;
        private Object headers;
        private Object body;
    }


    @Data
    public static class PongRequestMessage extends Message {
        private String id;
        private String version;
        private String action;
        private Map data;

        public PongResponse createResponse() {
            PongResponse message = new PongResponse();
            message.setId(id);
            message.setOriginAction(action);
            return message;
        }
    }


    @Data
    public static class AuthRequestMessage extends Message {

        private String id;
        private String version;
        private String action;
        private Map data;

        public AuthResponse createResponse(String userId, String ip) {
            AuthResponse message = new AuthResponse();

            message.setId(id);
            message.setOriginAction(action);

            AuthResult result = new AuthResult();

            String NAMESPACE = "bfeb71b6-06b8-5e07-87b2-c461c20d9ff6";
            String source = ip + NAMESPACE;
            byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
            result.setBrowserId(String.valueOf(UUID.nameUUIDFromBytes(bytes)));
            result.setUserId(userId);

            message.setResult(result);

            return message;
        }
    }


}
