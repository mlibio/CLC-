package org.clc.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.clc.dto.CommentDto;
import org.clc.result.Result;
import org.clc.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "获取评论信息接口",
            description  = "返回当前帖子的评论",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<String> addComment(@RequestBody CommentDto commentDto){
        return commentService.addComment(commentDto);
    }
}
