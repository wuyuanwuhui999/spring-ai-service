package com.player.common.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Base64;

public class Common {
    public static String getFullTime(String pattern){
        //得到long类型当前时间
        long  l = System.currentTimeMillis();
        //new日期对象
        Date date =  new  Date(l);
        //转换提日期输出格式
        SimpleDateFormat dateFormat;
        if(pattern == null){
            dateFormat = new  SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
        }else{
            dateFormat = new  SimpleDateFormat(pattern);
        }
        return dateFormat.format(date);
    }

    public static RequestEntity postRequestEntity(String path, String token, Object params){
        URI uri = UriComponentsBuilder.fromUriString(path).build().toUri();
        // 自定义body实体类
        String s = JSON.toJSONString(params);
        return RequestEntity.post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Authorization", token)
                .body(s);
    }

    public static RequestEntity putRequestEntity(String path,String token, Object params){
        URI uri = UriComponentsBuilder.fromUriString(path).build().toUri();
        // 自定义body实体类
        String s = JSON.toJSONString(params);
        return RequestEntity.put(uri)
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Authorization", token)
                .body(s);
    }

    public static RequestEntity deleteRequestEntity(String path,String token){
        URI uri = UriComponentsBuilder.fromUriString(path).build().toUri();
        // 自定义body实体类
        return RequestEntity.delete(uri)
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Authorization", token)
                .build();
    }

    public static String nullToString(String str) {
        return str == null ?  "" : str;
    }


    public static String generateImage(String base64str,String savepath){
        if (base64str == null || savepath == null) return null;

        try {
            // Base64 解码
            byte[] imageData = Base64.getDecoder().decode(base64str);

            // 读取图像元信息（宽度、高度）
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            if (image == null) {
                throw new IOException("Invalid image data");
            }

            // 构造包含尺寸的新文件名
            String extension = "";
            int dotIndex = savepath.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = savepath.substring(dotIndex);
                savepath = savepath.substring(0, dotIndex);
            }
            String newFilename = String.format(
                    "%s_%dx%d%s",
                    savepath,
                    image.getWidth(),
                    image.getHeight(),
                    extension
            );

            // 写入文件（使用 try-with-resources 自动关闭流）
            try (FileOutputStream out = new FileOutputStream(newFilename)) {
                out.write(imageData);
            }

            // 返回处理后的路径（移除可能的 URI 前缀）
            return newFilename.replaceAll("^.+:", "");

        } catch (Exception e) {
            e.printStackTrace(); // 实际生产环境应记录日志
            return null;
        }

    }
}
