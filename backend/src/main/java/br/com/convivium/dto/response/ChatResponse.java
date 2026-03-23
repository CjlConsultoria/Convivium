package br.com.convivium.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class ChatResponse {
    private String message;
    private String type;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private boolean isAiResponse;
    private String source;

    public ChatResponse() {
        this.timestamp = LocalDateTime.now();
        this.isAiResponse = true;
        this.type = "text";
        this.source = "assistant";
    }

    public ChatResponse(String message) {
        this();
        this.message = message;
    }

    public ChatResponse(String message, String type) {
        this(message);
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isAiResponse() {
        return isAiResponse;
    }

    public void setAiResponse(boolean aiResponse) {
        isAiResponse = aiResponse;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}