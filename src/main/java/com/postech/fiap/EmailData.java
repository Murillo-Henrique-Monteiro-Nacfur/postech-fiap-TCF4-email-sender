package com.postech.fiap;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import java.util.List;

@Data
@RegisterForReflection
public class EmailData {
    private List<String> destinations;
    private String message;
    private String subject;
    private Attachment attachment;

    @Data
    @RegisterForReflection
    public static class Attachment {
        private String bucket;
        private String nameFile;
    }
}