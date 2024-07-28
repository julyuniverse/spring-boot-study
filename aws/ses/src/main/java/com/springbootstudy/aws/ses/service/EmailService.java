package com.springbootstudy.aws.ses.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class EmailService {
    private final SesClient sesClient;
    @Value("${aws.ses.email.sender-name}")
    private String senderName;
    @Value("${aws.ses.email.source}")
    private String emailSource;

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public void sendEmail(String to, String subject, String body) {
        String fromAddress = String.format("%s <%s>", senderName, emailSource);
        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .destination(Destination.builder().toAddresses(to).build())
                .message(Message.builder()
                        .subject(Content.builder().data(subject).build())
                        .body(Body.builder().html(Content.builder().data(body).build()).build())
                        .build())
                .source(fromAddress)
                .build();
        sesClient.sendEmail(emailRequest);
    }
}
