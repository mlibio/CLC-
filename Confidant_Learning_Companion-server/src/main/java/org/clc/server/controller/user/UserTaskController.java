package org.clc.server.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.clc.common.context.BaseContext;
import org.clc.pojo.dto.PageQueryDto;
import org.clc.pojo.dto.TaskDto;
import org.clc.pojo.dto.TaskIdDto;
import org.clc.pojo.dto.TaskUpdateDto;
import org.clc.pojo.entity.Task;
import org.clc.common.result.PageResult;
import org.clc.common.result.Result;
import org.clc.server.service.TaskService;
import org.clc.pojo.vo.TaskVo;
import org.springframework.beans.BeanUtils;
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
@Tag(name="任务相关接口-用户端")
@RequestMapping("/user/task")
public class UserTaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping
    @Operation(summary = "查询任务接口",
            description  = "按一定顺序返回当前登录用户的任务列表",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<List<TaskVo>> findAllTask() {
        String uid=BaseContext.getCurrentId();
        return taskService.getTasksByUid(uid);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询任务接口",
            description  = "按一定顺序分页返回当前登录用户的任务列表，",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public PageResult getTask(@Parameter(description = "页数", required = true)
                                  @RequestParam(value = "page",defaultValue = "1") int page,
                              @Parameter(description = "页码大小", required = true)
                                  @RequestParam(value = "pageSize",defaultValue = "5") int pageSize){
        String uid= BaseContext.getCurrentId();
        PageQueryDto pageQueryDto = new PageQueryDto();
        pageQueryDto.setPage(page);
        pageQueryDto.setPageSize(pageSize);
        return taskService.getIncompleteTaskByUid(uid,pageQueryDto);
    }

    @PostMapping("/insert")
    @Operation(summary = "新增任务接口",
            description  = "输入任务信息",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<String> addTask(@RequestBody @Parameter(description = "任务参数", required = true) TaskDto taskDto) {
        Task task=new Task();
        BeanUtils.copyProperties(taskDto,task);
        return taskService.insertWithTime(task);
    }
    @PostMapping("/update")
    @Operation(summary = "修改任务接口",
            description  = "输入修改信息",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<String> updateTask(@RequestBody @Parameter(description = "修改参数", required = true) TaskUpdateDto taskUpdateDto) {
        return taskService.updateTask(taskUpdateDto);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除任务接口",
            description  = "输入要删除的任务的taskId",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<String> deleteTask(@RequestBody @Parameter(description = "要删除的任务ID请求体",required = true) TaskIdDto taskIdDto) {
        String taskId = taskIdDto.getTaskId();
        return taskService.deleteTaskByTaskId(taskId);
    }
}
