package org.clc.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.clc.common.constant.MessageConstant;
import org.clc.common.constant.StringConstant;
import org.clc.common.context.BaseContext;
import org.clc.pojo.dto.PageQueryDto;
import org.clc.pojo.dto.TaskUpdateDto;
import org.clc.pojo.entity.Task;
import org.clc.server.mapper.TaskMapper;
import org.clc.common.result.PageResult;
import org.clc.common.result.Result;
import org.clc.server.service.TaskService;
import org.clc.common.utils.MyRandomStringGenerator;
import org.clc.pojo.vo.TaskVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


/**
 * @version 1.0
 * @description: TODO
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    @Autowired
    private TaskMapper taskMapper;

    @Override
    public Result<String> insertWithTime(Task task) {
        task.setTaskId(StringConstant.PREFIX_FOR_TASK+ MyRandomStringGenerator.generateRandomString(8));
        task.setUid(BaseContext.getCurrentId());
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.insert(task);
        return Result.success(MessageConstant.SUCCESS);
    }

    @Override
    public Result<String> updateTask(TaskUpdateDto taskUpdateDto) {
        Task task = selectByTaskId(taskUpdateDto.getTaskId());
        task.setUpdateTime(LocalDateTime.now());
        BeanUtils.copyProperties(taskUpdateDto, task);
        taskMapper.updateById(task);
        return Result.success(MessageConstant.SUCCESS);
    }

    @Override
    public PageResult getIncompleteTaskByUid(String uid, PageQueryDto pageQueryDto) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getUid, uid);
        queryWrapper.eq(Task::getStatus,0);
        PageResult pageResult = new PageResult();
        /* 构造排序逻辑,在endTime不为空时按照endTime升序排列，在endTime为空或相等时按照startTime升序排列,
         在startTime为空或相等时按照position降序排列*/
        String sql = "ORDER BY CASE WHEN end_time IS NULL THEN 1 ELSE 0 END ASC, "
                + "end_time ASC, "
                + "CASE WHEN start_time IS NULL THEN 1 ELSE 0 END ASC, "
                + "start_time ASC, "
                + "position DESC";
        // PageHelper分页查询
        /* PageHelper.startPage(pageQueryDto.getPage(), pageQueryDto.getPageSize());
        List<Task> taskList=taskMapper.selectList(queryWrapper.last(sql));*/
        // MP分页查询
        Page<Task> page=Page.of(pageQueryDto.getPage(), pageQueryDto.getPageSize());
        Page<Task> p=lambdaQuery()
                .last(sql)
                .page(page);
        pageResult.setTotal(p.getTotal());
        pageResult.setPages(p.getPages());
        List<Task> taskList=p.getRecords();
        if(CollUtil.isEmpty(taskList)){//没有查到数据
            pageResult.setRecords(Collections.EMPTY_LIST);
            return pageResult;
        }
        //转化Task为TaskVo
        pageResult.setRecords(BeanUtil.copyToList(taskList,TaskVo.class));
        return pageResult;
    }

    @Override
    public Result<String> deleteTaskByTaskId(String taskId) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        taskMapper.delete(queryWrapper);
        return Result.success(MessageConstant.SUCCESS);
    }

    @Override
    public Result<List<TaskVo>> getTasksByUid(String uid) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        /* 构造排序逻辑,在endTime不为空时按照endTime升序排列，在endTime为空或相等时按照startTime升序排列,
         在startTime为空或相等时按照position降序排列,position不能为空 */
        String sql = "ORDER BY CASE WHEN end_time IS NULL THEN 1 ELSE 0 END ASC, "
                + "end_time ASC, "
                + "CASE WHEN start_time IS NULL THEN 1 ELSE 0 END ASC, "
                + "start_time ASC, "
                + "position DESC";
        queryWrapper.last(sql);
        List<Task> taskList=taskMapper.selectList(queryWrapper);
        return Result.success(200,MessageConstant.SUCCESS,BeanUtil.copyToList(taskList,TaskVo.class));
    }

    public Task selectByTaskId(String taskId) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        return taskMapper.selectOne(queryWrapper);
    }

}
