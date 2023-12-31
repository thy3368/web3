package com.example.tweb3demo1.grass.domain;


import lombok.Data;

@Data
public class HttpProxy {

    private String proxyType;
    private String ip;
    private int port;
    private String username;
    private String password;
    private boolean available;
    public HttpProxy() {
    }

    public HttpProxy(String ip, int port, String username, String password) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }
}
