package org.clc.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.clc.constant.MessageConstant;
import org.clc.dto.PageQueryDto;
import org.clc.dto.PostDto;
import org.clc.dto.PostIdDto;
import org.clc.entity.Post;
import org.clc.result.PageResult;
import org.clc.result.Result;
import org.clc.service.PostService;
import org.clc.vo.PostDetailVo;
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
@Tag(name="帖子相关接口-用户端")
@RequestMapping("/user/post")
public class UserPostController {

    @Autowired
    private PostService postService;

    @GetMapping
    @Operation(summary = "分页返回贴子的接口",
            description  = "分页返回首页帖子",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public PageResult getPosts(@Parameter(description = "页数", required = true)
                                   @RequestParam(value = "page",defaultValue = "1") int page,
                               @Parameter(description = "页码大小", required = true)
                                   @RequestParam(value = "pageSize",defaultValue = "5") int pageSize) {
        PageQueryDto pageQueryDto = new PageQueryDto();
        pageQueryDto.setPage(page);
        pageQueryDto.setPageSize(pageSize);
        return postService.getPosts(pageQueryDto);
    }

    @GetMapping("/detail")
    @Operation(summary = "返回贴子详情的接口",
            description  = "返回所给ID的帖子的详情",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public PostDetailVo getPost(@RequestBody PostIdDto postIdDto) {
        try{
            return postService.getPostDetail(postService.getOne(new QueryWrapper<Post>().eq("postId",postIdDto.getPostId())));
        }catch (Exception e){
            throw new RuntimeException(MessageConstant.NO_RESOURCES_EXIST);
        }
    }

    @GetMapping("/favor")
    @Operation(summary = "分页返回收藏贴的接口",
            description  = "分页返回用户收藏的帖子",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public PageResult getFavorPost(@Parameter(description = "页数", required = true)
                                       @RequestParam(value = "page",defaultValue = "1") int page,
                                   @Parameter(description = "页码大小", required = true)
                                       @RequestParam(value = "pageSize",defaultValue = "5") int pageSize){
        PageQueryDto pageQueryDto = new PageQueryDto();
        pageQueryDto.setPage(page);
        pageQueryDto.setPageSize(pageSize);
        return postService.getFavorPost(pageQueryDto);
    }

    @PostMapping
    @Operation(summary = "新增帖子接口",
            description  = "新增帖子",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<String> addPost(@RequestBody PostDto postDto){
        return postService.addPost(postDto);
    }

    @PostMapping("/thumb")
    @Operation(summary = "点赞帖子接口",
            description  = "点赞给定编号的帖子",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public void thumbComment(String postId){
        postService.thumbComment(postId);
    }
}
