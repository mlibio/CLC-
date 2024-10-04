package org.clc.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.clc.pojo.dto.PageQueryDto;
import org.clc.pojo.dto.TaskUpdateDto;
import org.clc.pojo.entity.Task;
import org.clc.common.result.PageResult;
import org.clc.common.result.Result;
import org.clc.pojo.vo.TaskVo;

import java.util.List;

/**
 * @version 1.0
 * @description: TODO
 */
public interface TaskService extends IService<Task> {
    Result<String> insertWithTime(Task task);

    Result<String> updateTask(TaskUpdateDto taskUpdateDto);

    PageResult getIncompleteTaskByUid(String uid, PageQueryDto pageQueryDto);

    Result<String> deleteTaskByTaskId(String taskId);

    Result<List<TaskVo>> getTasksByUid(String uid);
}
