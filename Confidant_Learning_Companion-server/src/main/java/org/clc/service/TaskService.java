package org.clc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.clc.dto.PageQueryDto;
import org.clc.dto.TaskDto;
import org.clc.dto.TaskUpdateDto;
import org.clc.entity.Task;
import org.clc.result.PageResult;
import org.clc.result.Result;
import org.clc.vo.TaskVo;

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
