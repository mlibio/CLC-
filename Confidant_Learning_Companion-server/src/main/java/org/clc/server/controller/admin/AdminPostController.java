package org.clc.server.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.clc.pojo.dto.PageQueryDto;
import org.clc.pojo.dto.PostIdDto;
import org.clc.common.result.PageResult;
import org.clc.server.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.clc.common.result.Result;

/**
 * @version 1.0
 * @description: TODO
 */
@RestController
@CrossOrigin//跨域
@Slf4j
@Tag(name="帖子相关接口-管理端")
@RequestMapping("/admin/post")
public class AdminPostController {
    @Autowired
    private PostService postService;

    @GetMapping
    @Operation(summary = "分页返回贴子的接口",
            description  = "分页返回帖子")
    public PageResult getPosts(@Parameter(description = "页数", required = true)
                               @RequestParam(value = "page",defaultValue = "1") int page,
                               @Parameter(description = "页码大小", required = true)
                               @RequestParam(value = "pageSize",defaultValue = "5") int pageSize) {
        PageQueryDto pageQueryDto = new PageQueryDto();
        pageQueryDto.setPage(page);
        pageQueryDto.setPageSize(pageSize);
        return postService.getPosts(pageQueryDto);
    }

    @PostMapping("/ban")
    @Operation(summary = "封禁帖子的接口",
            description  = "输入要封禁帖子的postId")
    public Result<String> ban(@RequestBody @Parameter(description = "要封禁的postId") PostIdDto postIdDto){
        return postService.ban(postIdDto);
    }

    @PostMapping("/unban")
    @Operation(summary = "解封帖子的接口",
            description  = "输入要解封帖子的postId")
    public Result<String> unban(@RequestBody @Parameter(description = "要封禁的postId") PostIdDto postIdDto){
        return postService.unban(postIdDto);
    }
}
