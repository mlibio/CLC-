package org.clc.server.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.clc.common.constant.MessageConstant;
import org.clc.pojo.dto.LearnerUidDto;
import org.clc.pojo.dto.PageQueryDto;
import org.clc.pojo.entity.Learner;
import org.clc.pojo.entity.OperationLogs;
import org.clc.pojo.entity.enumeration.OperationType;
import org.clc.common.result.PageResult;
import org.clc.common.result.Result;
import org.clc.server.service.LearnerService;
import org.clc.server.service.OperationLogsService;
import org.clc.common.utils.OperationLogsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @version 1.0
 * @description: TODO
 */
@RestController
@CrossOrigin//跨域
@Slf4j
@Tag(name="用户相关接口-管理端")
@RequestMapping("/admin/learner")
public class AdminLearnerController {
    @Autowired
    private LearnerService learnerService;
    @Autowired
    private OperationLogsService operationLogsService;
    @GetMapping
    @Operation(summary = "分页查询用户接口",
            description  = "输入page和pageSize，返回对应分页的用户信息",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public PageResult getLearner(@Parameter(description = "页数", required = true)
                                     @RequestParam(value = "page",defaultValue = "1") int page,
                                 @Parameter(description = "页码大小", required = true)
                                 @RequestParam(value = "pageSize",defaultValue = "5") int pageSize) {
        PageQueryDto pageQueryDto = new PageQueryDto();
        pageQueryDto.setPage(page);
        pageQueryDto.setPageSize(pageSize);
        return learnerService.pageQuery(pageQueryDto);
    }

    @PostMapping("/setAdmin")
    @Operation(summary = "添加管理员接口",
            description  = "输入对应账号的Uid，将该账号权限改为管理员",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<String> setAdmin(@RequestBody @Parameter(description = "账号Uid", required = true) LearnerUidDto learnerUidDto){
        String uid = learnerUidDto.getUid();
        Learner learner=learnerService.getLearnerByUid(uid);
        if(learner==null){
            return Result.error(400, MessageConstant.ACCOUNT_NOT_FOUND);
        }else if(learner.getPrivileges()==1){
            //设置权限为管理员
            learner.setPrivileges(0);
            try{
                learnerService.updateByUid(learner);
                //建立操作日志
                OperationLogs operationLogs=OperationLogsUtil.buildOperationLog(
                        OperationType.SET_ADMIN,
                        learnerUidDto.getUid(),
                        true,
                        MessageConstant.SUCCESS
                );
                operationLogsService.save(operationLogs);//添加操作日志
                return Result.success("管理员设置成功:"+learner.getUid());
            }catch (Exception e){
                //建立操作日志
                OperationLogs operationLogs=OperationLogsUtil.buildOperationLog(
                        OperationType.SET_ADMIN,
                        learnerUidDto.getUid(),
                        false,
                        e.getMessage()
                );
                operationLogsService.save(operationLogs);//添加操作日志
                return Result.error(500,MessageConstant.FAILED);
            }
        }else{
            return Result.error(400,MessageConstant.INVALID_ACTION);
        }
    }

    @PostMapping("/ban")
    @Operation(summary = "封禁账号接口",
            description  = "输入对应账号的Uid，将该账号状态改为封禁",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<String> ban(@RequestBody @Parameter(description = "账号Uid", required = true) LearnerUidDto learnerUidDto){
        String uid = learnerUidDto.getUid();
        Learner learner=learnerService.getLearnerByUid(uid);
        if(learner==null){
            return Result.error(400, MessageConstant.ACCOUNT_NOT_FOUND);
        }else if(!learner.getStatus()){
            return Result.error(400, MessageConstant.INVALID_ACTION);
        }else{
            learner.setStatus(Boolean.FALSE);
            try {
                learnerService.updateByUid(learner);
                //建立操作日志
                OperationLogs operationLogs=OperationLogsUtil.buildOperationLog(
                        OperationType.BAN_USER,
                        learnerUidDto.getUid(),
                        true,
                        MessageConstant.SUCCESS
                );
                operationLogsService.save(operationLogs);//添加操作日志
                return Result.success(MessageConstant.SUCCESS);
            } catch (Exception e) {
                //建立操作日志
                OperationLogs operationLogs=OperationLogsUtil.buildOperationLog(
                        OperationType.BAN_USER,
                        learnerUidDto.getUid(),
                        false,
                        e.getMessage()
                );
                operationLogsService.save(operationLogs);//添加操作日志
                return Result.error(500, e.getMessage());
            }
        }
    }

    @PostMapping("/unban")
    @Operation(summary = "解封账号接口",
            description  = "输入对应账号的Uid，将该账号状态改为正常",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<String> unban(@RequestBody @Parameter(description = "账号Uid", required = true) LearnerUidDto learnerUidDto){
        String uid = learnerUidDto.getUid();
        Learner learner=learnerService.getLearnerByUid(uid);
        if(learner==null){
            return Result.error(400, MessageConstant.ACCOUNT_NOT_FOUND);
        }else if(learner.getStatus()){
            return Result.error(400, MessageConstant.INVALID_ACTION);
        }else{
            learner.setStatus(Boolean.TRUE);
            try {
                learnerService.updateByUid(learner);
                //建立操作日志
                OperationLogs operationLogs=OperationLogsUtil.buildOperationLog(
                        OperationType.UNBAN_USER,
                        learnerUidDto.getUid(),
                        true,
                        MessageConstant.SUCCESS
                );
                operationLogsService.save(operationLogs);//添加操作日志
                return Result.success(MessageConstant.SUCCESS);
            } catch (Exception e) {
                //建立操作日志
                OperationLogs operationLogs=OperationLogsUtil.buildOperationLog(
                        OperationType.UNBAN_USER,
                        learnerUidDto.getUid(),
                        false,
                        e.getMessage()
                );
                operationLogsService.save(operationLogs);//添加操作日志
                return Result.error(500, e.getMessage());
            }
        }
    }

}
