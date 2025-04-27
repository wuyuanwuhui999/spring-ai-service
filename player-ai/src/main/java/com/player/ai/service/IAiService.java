package com.player.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.player.ai.entity.ChatEntity;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

public interface IAiService extends IService<ChatEntity> {
    Flux<String> chat(String userId,String prompt, String chatId, List<MultipartFile> files);

    String upload(List<MultipartFile>files);
}
