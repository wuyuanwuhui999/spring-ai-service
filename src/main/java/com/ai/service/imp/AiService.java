package com.ai.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ai.entity.ChatEntity;
import com.ai.mapper.AiMapper;
import com.ai.service.IAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@RequiredArgsConstructor
@Service
public class AiService extends ServiceImpl<AiMapper,ChatEntity> implements IAiService {

    @Autowired
    private ChatClient chatClient;

    @Value("${spring.servlet.multipart.upload-dir}")
    private String UPLOAD_DIR;

    @Override
    public Flux<String> chat(String prompt, String chatId, List<MultipartFile> files) {
        Flux<String> stringFlux;
        if (files == null || files.isEmpty()) {
            // 没有附件，纯文本聊天
            stringFlux = chatClient
                    .prompt()
                    .user(prompt)
                    .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY,chatId))
                    .stream()
                    .content();
        } else {
            // 有附件，多模态聊天
            // 1.解析多媒体
            String uploadFiles = upload(files);
            List<Media> medias = files.stream()
                    .map(file -> new Media(
                                    MimeType.valueOf(Objects.requireNonNull(file.getContentType())),
                                    file.getResource()
                            )
                    )
                    .toList();
            // 2.请求模型
            stringFlux =  chatClient.prompt()
                    .user(p -> p.text(prompt).media(medias.toArray(Media[]::new)))
                    .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                    .stream()
                    .content();
        }

        return stringFlux;
    }

    @Override
    public String upload(List<MultipartFile>files){
        // 确保上传目录存在
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        List<String> uploadedFileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                // 获取原始文件名
                String originalFileName = file.getOriginalFilename();

                // 获取文件扩展名
                String fileExtension = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                }

                // 生成唯一文件名
                String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                // 构建文件保存路径
                Path filePath = Paths.get(UPLOAD_DIR + uniqueFileName);

                // 将文件保存到指定路径
                Files.copy(file.getInputStream(), filePath);

                // 添加到文件名列表
                uploadedFileNames.add(uniqueFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 拼接文件名，用分号隔开
        String result = String.join(";", uploadedFileNames);
        return result.isEmpty() ? "" : result;
    }
}
