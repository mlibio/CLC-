package org.clc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.clc.dto.CommentDto;
import org.clc.entity.Comment;
import org.clc.result.Result;

/**
 * @version 1.0
 * @description: TODO
 */

public interface CommentService extends IService<Comment> {
    Result<String> addComment(CommentDto commentDto);
}
