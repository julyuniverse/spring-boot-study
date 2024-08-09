package com.springbootstudy.sociallogin.config;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Component
public class FilterErrorResponse {
    // 한글 출력을 위해 getWriter() 사용
    public void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("timestamp", String.valueOf(OffsetDateTime.now()));
        responseJson.addProperty("status", errorCode.getHttpStatus().value());
        responseJson.addProperty("error", errorCode.getHttpStatus().name());
        responseJson.addProperty("message", errorCode.getMessage());
        responseJson.addProperty("code", errorCode.name());
        response.getWriter().print(responseJson);
    }
}
