package com.player.ai.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("chat_history")
public class ChatEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String userId;
    private String files;
    private String chatId;
    private String prompt;
    private String content;
    private String createTime;
}

