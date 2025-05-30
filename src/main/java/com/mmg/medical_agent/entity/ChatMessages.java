package com.mmg.medical_agent.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("chat_messages")
public class ChatMessages {

    @Id // 唯一标识，映射到MongoDB的_id字段,使用ObjectId类型
    private ObjectId messageId;

    private String memoryId;

    // 存储聊天消息的内容
    private String content;
}
