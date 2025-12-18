package com.postech.fiap;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.funqy.Funq;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.inject.Inject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

import java.util.logging.Logger;



@ApplicationScoped
public class EmailSenderFunction {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    Mailer mailer;

    private static final Logger LOGGER = Logger.getLogger(EmailSenderFunction.class.getName());

    @Funq("emailSender")
    public void emailSender(MessageRequest request) {
        LOGGER.info("Pub/Sub message received");

        try {
            String decodedData = new String(
                    Base64.getDecoder().decode(request.getMessage().getData()),
                    StandardCharsets.UTF_8
            );

            EmailData emailData = objectMapper.readValue(decodedData, EmailData.class);
            sendEmail(emailData);

        } catch (Exception e) {
            LOGGER.severe("Error processing Pub/Sub message: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void sendEmail(EmailData emailData) {
        Mail mail = new Mail();
        List<String> recipients = emailData.getDestinations()
                .stream()
                .map(String::trim)
                .filter(email -> !email.isEmpty())
                .toList();
        mail.setTo(recipients);
        mail.setSubject(emailData.getSubject());
        mail.setText(emailData.getMessage());


        if (emailData.getAttachment() != null && emailData.getAttachment().getNameFile() != null && !emailData.getAttachment().getNameFile().isEmpty()) {
            try {
                Path filePath = Paths.get(emailData.getAttachment().getNameFile());
                byte[] content = Files.readAllBytes(filePath);
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                mail.addAttachment(
                        filePath.getFileName().toString(),
                        content,
                        contentType
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mailer.send(mail);
    }
}
