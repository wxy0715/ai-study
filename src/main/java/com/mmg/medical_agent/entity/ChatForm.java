package com.mmg.medical_agent.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChatForm {

    @Schema(description = "会话ID")
    private Long memoryId;

    @Schema(description = "消息内容")
    private String message;
    
    public Long getMemoryId() {
        return memoryId;
    }
    
    public void setMemoryId(Long memoryId) {
        this.memoryId = memoryId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
