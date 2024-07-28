package com.springbootstudy.aws.ses.controller;

import com.springbootstudy.aws.ses.dto.SendEmailRequest;
import com.springbootstudy.aws.ses.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aws/ses")
public class EmailController {
    private final EmailService emailService;

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @PostMapping
    public ResponseEntity<Void> sendEmail(@RequestBody SendEmailRequest sendEmailRequest) {
        emailService.sendEmail(sendEmailRequest.to(), sendEmailRequest.subject(), sendEmailRequest.body());

        return ResponseEntity.ok().build();
    }
}
