package org.clc.server.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.clc.common.constant.MessageConstant;
import org.clc.common.constant.QueueConstant;
import org.clc.pojo.entity.Post;
import org.clc.pojo.message.PostUpdateMessage;
import org.clc.server.service.PostService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @version 1.0
 * @description: TODO
 */
@Component
@Slf4j
public class PostMqListener {

    @Autowired
    private PostService postService;

    @RabbitListener(queues = QueueConstant.POST_UPDATE_QUEUE)
    public void receiveMessage(PostUpdateMessage message, Channel channel, long tag) {
        try {
            String postId = message.getPostId();
            List<Post> posts = postService.selectPostsByPostIds(List.of(postId));
            if (!posts.isEmpty()) {
                Post post = posts.get(0);
                BeanUtils.copyProperties(message, post);
                postService.cachePost(post);
                // 手动确认消息
                channel.basicAck(tag, false);
            } else {
                log.warn(MessageConstant.EMPTY_ID, postId);
                // 拒绝消息并重新入队
                channel.basicNack(tag, false, true);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // 拒绝消息并重新入队
            try {
                channel.basicNack(tag, false, true);
            } catch (IOException ex) {
                log.error(MessageConstant.FAILED, ex);
            }
            throw new RuntimeException(MessageConstant.FAILED);
        }
    }
}
