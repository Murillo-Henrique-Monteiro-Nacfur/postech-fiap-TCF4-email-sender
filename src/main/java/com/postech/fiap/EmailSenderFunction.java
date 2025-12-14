package com.postech.fiap;

import io.netty.handler.codec.base64.Base64;
import io.quarkus.funqy.Funq;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.logging.Logger;

@ApplicationScoped
public class EmailSenderFunction {

    private static final Logger LOGGER = Logger.getLogger(EmailSenderFunction.class.getName());

    public static class PubSubMessage {
        public Message message;
        public String subscription;
    }

    public static class Message {
        public String data;
        public Map<String, String> attributes;
        public String messageId;
    }

    @Funq("emailSender")
    public void scheduledReport(PubSubMessage message) {
        LOGGER.info("Scheduler trigger received. Message ID: " + message.message.data);
        // The message payload can be ignored if the function is just a periodic trigger.
        // Add your scheduled task logic here. For example, generating a report.
        // E.g., generateReportUseCase.execute();
        LOGGER.info("Scheduled task finished.");
    }
}
