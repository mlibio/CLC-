package org.clc.server.task;

import lombok.extern.slf4j.Slf4j;
import org.clc.common.constant.MessageConstant;
import org.clc.common.constant.StringConstant;
import org.clc.server.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class LikeDataSyncScheduler {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private PostService postService;

    /**
     * 异步更新点赞数据到数据库
     */
    // 每隔两小时执行一次（cron表达式）
    @Scheduled(cron = "0 0 */2 * * ?")
    public void syncLikesToDatabase() {
        try {
            // 获取所有点赞数据
            Set<String> keys = redisTemplate.keys(StringConstant.PREFIX_FOR_CACHE_LIKES + "*");
            if (keys == null || keys.isEmpty()) {
                return;
            }
            // 批量获取点赞数
            Map<String, Double> likesMap = getLikesFromRedis(keys);
            // 更新数据库
            postService.updateLikesInDatabase(likesMap);
        } catch (Exception e) {
            log.error(MessageConstant.FAILED, e);
        }
    }
    private Map<String, Double> getLikesFromRedis(Set<String> keys) {
        if (keys.isEmpty()) {
            return new ConcurrentHashMap<>();
        }
        Map<String, Double> likesMap = new ConcurrentHashMap<>();
        // 使用 pipeline 批量获取点赞数
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String key : keys) {
                connection.zSetCommands().zScore(StringConstant.PREFIX_FOR_CACHE_LIKES.getBytes(), key.getBytes());
            }
            return null;
        });
        int index = 0;
        for (String key : keys) {
            Object score = results.get(index++);
            if (score != null) {
                likesMap.put(key, ((Number) score).doubleValue());
            } else {
                likesMap.put(key, 0.0);
            }
        }
        return likesMap;
    }
}