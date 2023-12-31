package com.example.tweb3demo1.grass;


import com.example.tweb3demo1.grass.domain.GrassConnection;
import com.example.tweb3demo1.grass.domain.HttpProxy;
import com.example.tweb3demo1.grass.domain.repo.ConnectionRepo;
import com.example.tweb3demo1.grass.domain.repo.HttpProxyRepo;
import com.example.tweb3demo1.grass.inboundadapter.GrassSSLWebSocketClient;
import com.example.tweb3demo1.grass.outboundAdapter.repo.ConnectionRepoImpl;
import com.example.tweb3demo1.grass.outboundAdapter.repo.HttpProxyRepoImpl;
import org.java_websocket.drafts.Draft_6455;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.Collection;

public class GrassApp {

    private static final Logger logger = LoggerFactory.getLogger(GrassApp.class);
    private static final String URL = "wss://proxy.wynd.network:4650";


    public static void main(String[] args) throws URISyntaxException {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Received SIGINT signal.");
                System.exit(0);
            }
        });

        ConnectionRepo connectionRepo = new ConnectionRepoImpl();
        HttpProxyRepo httpProxyRepo = new HttpProxyRepoImpl();

        Collection<GrassConnection> grassConnections = connectionRepo.queryAll();

        for (GrassConnection grassConnection : grassConnections) {
            HttpProxy proxy = new HttpProxy();
            proxy.setIp(grassConnection.getIp());
            HttpProxy proxy1 = httpProxyRepo.query(proxy);

            GrassSSLWebSocketClient client = new GrassSSLWebSocketClient(new URI(URL), new Draft_6455(), null, 30000, grassConnection, proxy1);
            client.start();

        }

    }

}
