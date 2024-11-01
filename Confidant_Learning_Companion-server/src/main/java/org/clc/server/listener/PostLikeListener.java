package org.clc.server.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.clc.common.constant.MessageConstant;
import org.clc.common.constant.QueueConstant;
import org.clc.pojo.entity.Post;
import org.clc.pojo.entity.PostLike;
import org.clc.pojo.message.PostLikeMessage;
import org.clc.pojo.message.PostUpdateMessage;
import org.clc.server.mapper.PostLikeMapper;
import org.clc.server.service.PostService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @version 1.0
 * @description: TODO
 */
@Component
@Slf4j
public class PostLikeListener {

    @Autowired
    private PostLikeMapper postLikeMapper;

    @RabbitListener(queues = QueueConstant.POST_LIKE_UPDATE_QUEUE)
    public void receiveMessage(PostLikeMessage message) {
        // 根据message中的信息更新数据库
        try{
            if(message.isLike()){
                // 新增点赞记录
                PostLike postLike = new PostLike();
                BeanUtils.copyProperties(message,postLike);
                postLikeMapper.insert(postLike);
            }else{
                // 删除点赞记录
                LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(PostLike::getPostId, message.getPostId())
                        .eq(PostLike::getUid, message.getUid());
                postLikeMapper.delete(wrapper);
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new RuntimeException(MessageConstant.FAILED);
        }
    }
}
