package org.clc.server.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.clc.common.result.Result;
import org.clc.server.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0
 * @description: TODO
 */
@RestController
@CrossOrigin//跨域
@Slf4j
@Tag(name="评论相关接口-管理端")
@RequestMapping("/admin/comment")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/delete")
    @Operation(summary = "删除评论接口",
            description  = "删除给定编号的评论")
    public Result<String> deleteComment(String cId){
        return commentService.deleteCommentByCId(cId);
    }

    @PostMapping("/deleteByPostId")
    @Operation(summary = "删除评论接口",
            description  = "删除给定评论编号的评论")
    public Result<String> deleteCommentByPostId(String postId){
        return commentService.deleteCommentByPostId(postId);
    }

}
