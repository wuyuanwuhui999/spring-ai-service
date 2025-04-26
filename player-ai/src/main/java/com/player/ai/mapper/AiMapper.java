package com.player.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.player.ai.entity.ChatEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiMapper extends BaseMapper<ChatEntity> {
}
