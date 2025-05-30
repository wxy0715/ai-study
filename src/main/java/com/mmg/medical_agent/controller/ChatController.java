package com.mmg.medical_agent.controller;

import com.mmg.medical_agent.assistant.ChatAgent;
import com.mmg.medical_agent.entity.ChatForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "chat_assistant", description = "聊天助手")
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private ChatAgent chatAgent;

    @Operation(summary = "对话", operationId = "chat")
    //@PostMapping(value = "/chat")
    @PostMapping(value = "/chat", produces = "text/stream;charset=utf-8")
    public Flux<String> chat(@RequestBody ChatForm chatForm) {
        return chatAgent.chat(chatForm.getMemoryId(), chatForm.getMessage());
    }
}