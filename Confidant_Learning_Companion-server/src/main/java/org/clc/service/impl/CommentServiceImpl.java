package org.clc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.clc.constant.MessageConstant;
import org.clc.context.BaseContext;
import org.clc.dto.CommentDto;
import org.clc.entity.Comment;
import org.clc.mapper.CommentMapper;
import org.clc.result.Result;
import org.clc.service.CommentService;
import org.clc.utils.MyRandomStringGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @description: TODO
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService{
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
}
