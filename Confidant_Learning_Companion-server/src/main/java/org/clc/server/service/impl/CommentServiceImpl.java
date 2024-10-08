package org.clc.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.clc.common.constant.MessageConstant;
import org.clc.common.context.BaseContext;
import org.clc.pojo.dto.CommentDto;
import org.clc.pojo.entity.Comment;
import org.clc.pojo.entity.Learner;
import org.clc.pojo.entity.Post;
import org.clc.server.mapper.CommentMapper;
import org.clc.server.service.CommentService;
import org.clc.common.result.Result;
import org.clc.common.utils.MyRandomStringGenerator;
import org.clc.pojo.vo.CommentVo;
import org.clc.server.service.LearnerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @description: TODO
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private LearnerService learnerService;

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @Override
    public Result<String> addComment(CommentDto commentDto) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDto, comment);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUid(BaseContext.getCurrentId());
        comment.setCId(MessageConstant.PREFIX_FOR_COMMENT +MyRandomStringGenerator.generateRandomString(8));
        comment.setThumbs(0);
        try{
            this.save(comment);
            return Result.success(MessageConstant.SUCCESS);
        }catch (RuntimeException e){
            return Result.error(500,MessageConstant.FAILED);
        }
    }

    @Override
    public Result<List<CommentVo>> getCommentListByPostId(String postId) {
        List<Comment> commentList = commentMapper.selectList(new QueryWrapper<Comment>().eq("post_id", postId));
        if(commentList == null || commentList.isEmpty()){
            return Result.success(MessageConstant.EMPTY_RESOURCES);
        }
        List<CommentVo> commentVoList = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentVo commentVo = new CommentVo();
            Learner learner = learnerService.getLearnerByUid(comment.getUid());
            commentVo.setUsername(learner.getUsername());
            commentVo.setLearnerImage(learner.getImage());
            BeanUtils.copyProperties(comment, commentVo);
            commentVoList.add(commentVo);
        }
        return Result.success(200,MessageConstant.SUCCESS,commentVoList);
    }

    @Override
    public Result<String> deleteCommentByCId(String cId) {
        try{
            commentMapper.delete(new QueryWrapper<Comment>().eq("c_id", cId));
            // 删除 Redis 中的缓存数据
            redisTemplate.delete(cId);
            return Result.success(MessageConstant.SUCCESS);
        }catch (RuntimeException e){
            log.error(e.getMessage());
            return Result.error(500,MessageConstant.FAILED);
        }
    }

    @Override
    public void thumbComment(String cId) {
        // 使用cId作为key，从Redis中获取点赞数
        Integer likes = redisTemplate.opsForValue().get(cId);
        if (likes == null) {
            // 如果Redis中没有记录，需要从数据库加载初始值
            likes = commentMapper.selectOne(new QueryWrapper<Comment>().eq("c_id", cId)).getThumbs();
        }
        // 点赞数加一
        likes++;
        // 将更新后的点赞数存回Redis
        redisTemplate.opsForValue().set(cId, likes);
        // 异步更新数据库
        updateLikesInDatabaseAsync(cId, likes);
    }

    @Override
    public Result<String> deleteCommentByPostId(String postId) {
        try{
            // 删除 Redis 中的缓存数据
            redisTemplate.delete(postId);
            List<Comment> comments = commentMapper.selectList(new QueryWrapper<Comment>().eq("post_id", postId));
            for(Comment comment : comments){
                redisTemplate.delete(comment.getCId());
            }
            // 删除相关评论
            commentMapper.delete(new QueryWrapper<Comment>().eq("post_id", postId));
            return Result.success(MessageConstant.SUCCESS);
        }catch (RuntimeException e){
            log.error(e.getMessage());
            return Result.error(500,MessageConstant.FAILED);
        }
    }

    @Async
    protected void updateLikesInDatabaseAsync(String cId, Integer likes) {
        Comment comment = commentMapper.selectOne(new QueryWrapper<Comment>().eq("c_id", cId));
        comment.setThumbs(likes);
        commentMapper.updateById(comment);
    }
}
