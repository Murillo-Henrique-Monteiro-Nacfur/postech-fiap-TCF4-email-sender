package com.postech.fiap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    private Message message;
    private String subscription;

    @Getter
    @Setter
    public static class Message {
        private String data;
    }
}