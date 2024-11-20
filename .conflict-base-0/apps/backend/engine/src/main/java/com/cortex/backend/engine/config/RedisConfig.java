package com.cortex.backend.engine.config;

import com.cortex.backend.engine.api.dto.CodeExecutionResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, CodeExecutionResult> redisTemplate(
      RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, CodeExecutionResult> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());

    ObjectMapper objectMapper = JsonMapper.builder().build();
    Jackson2JsonRedisSerializer<CodeExecutionResult> serializer =
        new Jackson2JsonRedisSerializer<>(objectMapper, CodeExecutionResult.class);

    template.setValueSerializer(serializer);
    return template;
  }
}