package com.fiap.gestaoltakn.ai.dto;

import java.util.List;

public class OpenAIResponse {
    private List<Choice> choices;

    public OpenAIResponse() {}

    public List<Choice> getChoices() { return choices; }
    public void setChoices(List<Choice> choices) { this.choices = choices; }

    public static class Choice {
        private Message message;

        public Choice() {}

        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }

        public static class Message {
            private String content;

            public Message() {}

            public String getContent() { return content; }
            public void setContent(String content) { this.content = content; }
        }
    }

}
