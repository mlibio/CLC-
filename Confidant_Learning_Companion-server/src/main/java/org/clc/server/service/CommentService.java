package org.clc.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.clc.pojo.dto.CommentDto;
import org.clc.pojo.entity.Comment;
import org.clc.common.result.Result;
import org.clc.pojo.vo.CommentVo;

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
