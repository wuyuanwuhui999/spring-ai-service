package com.player.common.utils;

import com.alibaba.fastjson.JSONObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import com.player.common.entity.UserEntity;
import io.jsonwebtoken.*;

/**
 * @author zengjintao
 * @version 1.0
 * @create_at 2020/4/19 15:31
 */
public class JwtToken {

    private static Long expirationTime = 2592000000L;

    /**
     * 生成jwt token
     * @param value
     * @return
     */
    public static String createToken(Object value,String secret) {
        // 生成SecretKey 对象
        SecretKey secretKey = createSecretKey(secret);
        String jsonValue = JSONObject.toJSONString(value);
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date();
        JwtBuilder jwtBuilder = Jwts.builder().setIssuedAt(now).setSubject(jsonValue).signWith(signatureAlgorithm, secretKey);
        if (expirationTime > 0L) {
            long expMillis = nowMillis + expirationTime;
            Date exp = new Date(expMillis);
            jwtBuilder.setExpiration(exp);  // 设置token过期时间
        }
        return "Bearer " + jwtBuilder.compact();
    }

    private static SecretKey createSecretKey(String secret) {
        // 使用 Java 8+ 标准 Base64 解码
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        // 验证密钥长度是否符合 AES 要求 (128/192/256 bits)
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("Invalid AES key length: " + keyBytes.length * 8 + " bits");
        }
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * 解析token
     *
     * @param token
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parserToken(String token, Class<T> clazz,String secret) {
        token = token.substring(7);
        SecretKey secretKey = createSecretKey(secret);
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            String subject = claims.getSubject();
            return (T) JSONObject.parseObject(subject, clazz);
        } catch (SignatureException | MalformedJwtException e) {
            return null;
        } catch (ExpiredJwtException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getId(String token,String secret){
        UserEntity userEntity = parserToken(token,UserEntity.class,secret);
        if(userEntity != null)return userEntity.getId();
        return null;
    }
}
