package com.player.ai.controller;

import com.player.ai.service.IAiService;
import com.player.common.entity.UserEntity;
import com.player.common.utils.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/ai",produces = "text/html;charset=utf-8")
public class ChatController {
    private  final ChatClient chatClient;

    @Value("${token.secret}")
    private String secret;

    @Autowired
    private IAiService aiService;

    @RequestMapping("/chat")
    public Flux<String> chat(
            @RequestHeader("Authorization") String token,
            @RequestParam("prompt") String prompt,
            @RequestParam("chatId") String chatId,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ){
        String userId = JwtToken.parserToken(token, UserEntity.class, secret).getId();
        return aiService.chat(userId, prompt, chatId, files);
    }
}
