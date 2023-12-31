package com.example.tweb3demo1.grassapp.domain;

import com.example.tweb3demo1.grass.domain.Message;
import org.junit.Test;

public class MessageTest {


    @Test
    public void ping() {

        Message.PingMessage message = Message.ping();
        System.out.println(message);

    }

    @Test
    public void performHttpRequestResponse() {

        Message message = Message.performHttpRequestResponse("id", "originAction");
        System.out.println(message);

    }

    @Test
    public void pongResponse() {

        Message message = Message.pongResponse("id", "originAction");
        System.out.println(message);

    }

    @Test
    public void authenticationResponse() {

        Message message = Message.authenticationResponse("id", "originAction", "userid", "browserId");
        System.out.println(message);

    }


}
