package org.clc.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.clc.constant.MessageConstant;
import org.clc.context.BaseContext;
import org.clc.dto.LearnerPasswordUpdateDto;
import org.clc.dto.LearnerUidDto;
import org.clc.dto.LearnerUpdateDto;
import org.clc.entity.Learner;
import org.clc.result.PageResult;
import org.clc.result.Result;
import org.clc.service.LearnerFollowService;
import org.clc.service.LearnerService;
import org.clc.vo.LearnerVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @version 1.0
 * @description: TODO
 */
@RestController
@CrossOrigin//跨域
@Slf4j
@Tag(name="用户相关接口-用户端")
@RequestMapping("/user/learner")
public class UserLearnerController {
    @Autowired
    private LearnerService learnerService;

    @Autowired
    private LearnerFollowService learnerFollowService;
    @GetMapping
    @Operation(summary = "用户个人信息接口",
            description  = "返回当前登录用户的信息",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<LearnerVo> getUserLearner() {
        String uid= BaseContext.getCurrentId();
        Learner learner=learnerService.getLearnerByUid(uid);
        LearnerVo learnerVo=new LearnerVo();
        BeanUtils.copyProperties(learner,learnerVo);
        return Result.success(200, MessageConstant.SUCCESS,learnerVo);
    }

    @PostMapping("/update")
    @Operation(summary = "更新用户接口",
            description  = "输入更新用户信息，返回更新后的用户信息",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<LearnerVo> update(@RequestBody @Parameter(description = "更新参数", required = true) LearnerUpdateDto learnerUpdateDto){
        return learnerService.updateLearner(learnerUpdateDto);
    }

    @PostMapping("/updatePassword")
    @Operation(summary = "更改密码接口",
            description  = "输入旧密码、新密码和确认新密码，返回更新结果",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<String> updatePassword(@RequestBody @Parameter(description = "更改密码参数", required = true) LearnerPasswordUpdateDto learnerPasswordUpdateDto){
        return learnerService.updatePassword(learnerPasswordUpdateDto);
    }

    @PostMapping("/follow")
    @Operation(summary = "关注用户接口",
            description  = "输入被关注者UID",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<String> follow(@RequestBody @Parameter(description = "被关注者UID", required = true) LearnerUidDto learnerUidDto){
        String followedLearner=learnerUidDto.getUid();
        return learnerFollowService.follow(followedLearner);
    }

    @PostMapping("/unfollow")
    @Operation(summary = "取消关注接口",
            description  = "输入被关注者UID",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<String> unfollow(@RequestBody @Parameter(description = "被关注者UID", required = true) LearnerUidDto learnerUidDto){
        String followedLearner=learnerUidDto.getUid();
        return learnerFollowService.unfollow(followedLearner);
    }

}
