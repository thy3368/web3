package com.example.tweb3demo1.grass.inboundadapter;

import com.alibaba.fastjson.JSONObject;
import com.example.tweb3demo1.grass.GrassApp;
import com.example.tweb3demo1.grass.applicationservice.MessageApplicationService;
import com.example.tweb3demo1.grass.domain.GrassConnection;
import com.example.tweb3demo1.grass.domain.HttpProxy;
import com.example.tweb3demo1.grass.domain.Message;
import lombok.Getter;
import nl.altindag.ssl.pem.util.PemUtils;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.*;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 构建SSLWebSocket客户端，忽略证书
 */


@Getter
public class GrassSSLWebSocketClient extends WebSocketClient {


    private static final Logger logger = LoggerFactory.getLogger(GrassApp.class);
    //构造方法
    public static MessageApplicationService messageApplicationService = new MessageApplicationService();

    private final GrassConnection grassConnection;
    private final HttpProxy httpProxy;

    public GrassSSLWebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout, GrassConnection grassConnection1, HttpProxy httpProxy1) {
        super(serverUri, protocolDraft, httpHeaders, connectTimeout);

        this.grassConnection = grassConnection1;
        this.httpProxy = httpProxy1;
        trustAllHosts(this);

    }

    /**
     * 忽略证书
     *
     * @paramclient
     */
    void trustAllHosts(GrassSSLWebSocketClient client) {
        X509ExtendedTrustManager trustManager = PemUtils.loadTrustMaterial("ssl/websocket.pem");
        TrustManager[] trustAllCerts = new TrustManager[]{trustManager};
        try {
            SSLContext ssl = SSLContext.getInstance("SSL");
            ssl.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory socketFactory = ssl.getSocketFactory();
            this.setSocketFactory(socketFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        logger.info(grassConnection.getUserId() + "握手成功", "[OPEN]", "Websocket is open");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info(grassConnection.getUserId() + "链接已关闭", code, reason);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        logger.info(grassConnection.getUserId() + "发生错误已关闭");
    }

    @Override
    public void onMessage(String message) {
        logger.info(grassConnection.getUserId() + "Receive==========" + message);
        JSONObject jsonObject = JSONObject.parseObject(message);
        String action = (String) jsonObject.get("action");


        if ("PONG".equals(action)) {
            Message.PongRequestMessage pongRequestMessage = JSONObject.parseObject(message, Message.PongRequestMessage.class);
            messageApplicationService.handlePong(this, pongRequestMessage);
        } else if ("AUTH".equals(action)) {
            Message.AuthRequestMessage authRequestMessage = JSONObject.parseObject(message, Message.AuthRequestMessage.class);
            messageApplicationService.handleAuth(grassConnection.getUserId(), httpProxy.getIp(), this, authRequestMessage);

        }


    }

    public void start() {

        logger.info(grassConnection.getUserId() + httpProxy);


        if (grassConnection.isEnableProxy()) {
            logger.info(grassConnection.getUserId() + httpProxy);
            this.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxy.getIp(), httpProxy.getPort())));
            if (StringUtils.isNoneEmpty(httpProxy.getUsername())) {
                logger.info(grassConnection.getUserId() + "enable proxy username/password");
                setAuthProperties(httpProxy.getUsername(), httpProxy.getPassword());
            }
        }

//        client.addHeader("Cache-Control", "only-if-cached");


        GrassSSLWebSocketClient webSocketClient = this;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                messageApplicationService.sendPing(grassConnection.getUserId(), httpProxy.getIp(), webSocketClient);
            }
        }, 1000, 20000);


        while (true) {
            try {
                if (this.getReadyState() == ReadyState.NOT_YET_CONNECTED) {
                    logger.info(grassConnection.getUserId() + "新建连接中...");
                    this.connectBlocking();
                } else if (this.getReadyState() == ReadyState.CLOSED) {
                    logger.info(grassConnection.getUserId() + "重建连接中...");
                    this.reconnectBlocking();
                } else if (this.getReadyState() == ReadyState.OPEN) {
                    Thread.sleep(10000);
                    logger.info(grassConnection.getUserId() + "连接状态正常");
                } else if (this.getReadyState() == ReadyState.CLOSING) {
                    Thread.sleep(2000);
                    logger.info(grassConnection.getUserId() + "关闭连接中...");
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(grassConnection.getUserId() + "连接异常");
            }

        }
    }

    private void setAuthProperties(String authUser, String authPassword) {
        System.setProperty("http.proxyUser", authUser);
        System.setProperty("http.proxyPassword", authPassword);
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        Authenticator.setDefault(new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(authUser, authPassword.toCharArray());
            }
        });
    }

}
