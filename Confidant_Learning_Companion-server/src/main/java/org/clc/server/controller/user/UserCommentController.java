package org.clc.server.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.clc.pojo.dto.CommentDto;
import org.clc.common.result.Result;
import org.clc.server.service.CommentService;
import org.clc.pojo.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @version 1.0
 * @description: TODO
 */
@RestController
@CrossOrigin//跨域
@Slf4j
@Tag(name="评论相关接口-用户端")
@RequestMapping("/user/comment")
public class UserCommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/insert")
    @Operation(summary = "新增评论接口",
            description  = "为当前帖子新增评论",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<String> addComment(@RequestBody CommentDto commentDto){
        return commentService.addComment(commentDto);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "获取评论接口",
            description  = "获取当前帖子评论",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<List<CommentVo>> getCommentList(@PathVariable String postId){
        return commentService.getCommentListByPostId(postId);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除评论接口",
            description  = "删除给定编号的评论",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<String> deleteComment(String cId){
        return commentService.deleteCommentByCId(cId);
    }

    @PostMapping("/thumb")
    @Operation(summary = "点赞评论接口",
            description  = "点赞给定编号的评论",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public void thumbComment(String cId){
        commentService.thumbComment(cId);
    }
}
