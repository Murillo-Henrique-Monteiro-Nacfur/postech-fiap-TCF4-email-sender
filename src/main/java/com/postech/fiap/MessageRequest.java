package com.postech.fiap;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class MessageRequest {
    private Message message;
    private String subscription;

    @Getter
    @Setter
    @RegisterForReflection
    public static class Message {
        private String data;
    }
}