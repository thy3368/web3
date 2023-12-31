package com.example.tweb3demo1.grass.domain;


import lombok.Data;

@Data
public class GrassConnection {

    private String userId;
    private String ip;
    private boolean enableProxy = true;
    private boolean authed;

}
