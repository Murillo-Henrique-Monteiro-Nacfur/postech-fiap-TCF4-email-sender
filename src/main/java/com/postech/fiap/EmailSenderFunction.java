package com.postech.fiap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postech.fiap.service.GoogleStorageService;
import io.quarkus.funqy.Funq;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.inject.Inject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

import java.util.logging.Logger;



@ApplicationScoped
public class EmailSenderFunction {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    Mailer mailer;

    @Inject
    GoogleStorageService gcsUploadService;

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
        try {
            Mail mail = new Mail();
            List<String> recipients = emailData.getDestinations()
                    .stream()
                    .map(String::trim)
                    .filter(email -> !email.isEmpty())
                    .toList();
            mail.setTo(recipients);
            mail.setSubject(emailData.getSubject());
            mail.setText(emailData.getMessage());


            if (emailData.getAttachment() != null
                    && emailData.getAttachment().getNameFile() != null
                    && !emailData.getAttachment().getNameFile().isEmpty()) {

                String bucketName = emailData.getAttachment().getBucket();
                String objectName = emailData.getAttachment().getNameFile();

                if (bucketName != null && !bucketName.isEmpty()) {
                    String destPath = System.getProperty("java.io.tmpdir") + File.separator + objectName;
                    File file = gcsUploadService.downloadFile(bucketName, objectName, destPath);

                    Path filePath = file.toPath();
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
                }
            }

            mailer.send(mail);
            LOGGER.info("Email sent successfully to: " + recipients);

        } catch (Exception e) {
            LOGGER.severe("Failed to send email: " + e.getMessage());
            throw new RuntimeException("Error sending email", e);
        }
    }
}
