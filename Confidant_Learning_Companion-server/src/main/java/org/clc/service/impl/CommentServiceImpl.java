package org.clc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.clc.constant.MessageConstant;
import org.clc.context.BaseContext;
import org.clc.dto.CommentDto;
import org.clc.entity.Comment;
import org.clc.entity.OperationLogs;
import org.clc.entity.enumeration.OperationType;
import org.clc.mapper.CommentMapper;
import org.clc.result.Result;
import org.clc.service.CommentService;
import org.clc.utils.MyRandomStringGenerator;
import org.clc.utils.OperationLogsUtil;
import org.clc.vo.CommentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @description: TODO
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService{

    @Autowired
    private CommentMapper commentMapper;

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
            BeanUtils.copyProperties(comment, commentVo);
            commentVoList.add(commentVo);
        }
        return Result.success(200,MessageConstant.SUCCESS,commentVoList);
    }

    @Override
    public Result<String> deleteCommentByCId(String cId) {
        try{
            commentMapper.delete(new QueryWrapper<Comment>().eq("c_id", cId));
            return Result.success(MessageConstant.SUCCESS);
        }catch (RuntimeException e){
            log.error(e.getMessage());
            return Result.error(500,MessageConstant.FAILED);
        }
    }

    @Override
    public void thumbComment(String cId) {
        Comment comment = commentMapper.selectOne(new QueryWrapper<Comment>().eq("c_id", cId));
        comment.setThumbs(comment.getThumbs() + 1);
        commentMapper.updateById(comment);
    }

    @Override
    public Result<String> deleteCommentByPostId(String postId) {
        try{
            commentMapper.delete(new QueryWrapper<Comment>().eq("post_id", postId));
            return Result.success(MessageConstant.SUCCESS);
        }catch (RuntimeException e){
            log.error(e.getMessage());
            return Result.error(500,MessageConstant.FAILED);
        }
    }
}
