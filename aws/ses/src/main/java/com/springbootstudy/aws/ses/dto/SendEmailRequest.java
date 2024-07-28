package com.springbootstudy.aws.ses.dto;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public record SendEmailRequest(String to, String subject, String body) {
}
