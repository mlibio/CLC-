package org.clc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.clc.dto.CommentDto;
import org.clc.entity.Comment;
import org.clc.result.Result;
import org.clc.vo.CommentVo;

import java.util.List;

/**
 * @version 1.0
 * @description: TODO
 */

public interface CommentService extends IService<Comment> {
    Result<String> addComment(CommentDto commentDto);

    Result<List<CommentVo>> getCommentListByPostId(String postId);

    Result<String> deleteCommentByCId(String cId);

    void thumbComment(String cId);

    Result<String> deleteCommentByPostId(String postId);
}
