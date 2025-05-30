package com.mmg.medical_agent.integrations.listener;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomChatModelListener {
    @Bean
    public dev.langchain4j.model.chat.listener.ChatModelListener chatModelListener() {
        return new dev.langchain4j.model.chat.listener.ChatModelListener() {
            private final Logger log = LoggerFactory.getLogger(dev.langchain4j.model.chat.listener.ChatModelListener.class);
            @Override
            public void onRequest(ChatModelRequestContext requestContext) {
                //log.info("onRequest(): {}", requestContext.chatRequest());
            }
            @Override
            public void onResponse(ChatModelResponseContext responseContext) {
                //log.info("onResponse(): {}", responseContext.chatResponse());
            }
            @Override
            public void onError(ChatModelErrorContext errorContext) {
                //log.info("onError(): {}", errorContext.error().getMessage());
            }
        };
    }
}
