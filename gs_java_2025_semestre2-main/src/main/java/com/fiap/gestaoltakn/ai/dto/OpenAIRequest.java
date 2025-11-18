package com.fiap.gestaoltakn.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class OpenAIRequest {
    private String model;
    private List<Message> messages;
    private double temperature;

    @JsonProperty("max_tokens")
    private Integer maxTokens;

    public OpenAIRequest() {}

    public OpenAIRequest(String model, List<Message> messages, double temperature, Integer maxTokens) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
    }

    public static class Message {
        private String role;
        private String content;

        public Message() {}

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    @JsonProperty("max_tokens")
    public Integer getMaxTokens() { return maxTokens; }

    @JsonProperty("max_tokens")
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

}
