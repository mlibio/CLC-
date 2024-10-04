package org.clc.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @version 1.0
 * 任务实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title="任务实体类")
public class Task {
    @Schema(title="数据库id主键")
    private int id;
    @Schema(title="任务id")
    private String taskId;//任务ID
    @Schema(title="用户ID")
    private String uid;//用户ID
    @Schema(title="任务名")
    private String name;//任务名
    @Schema(title="任务详情")
    private String content;//任务详情
    @Schema(title="状态(0 待办，1 已完成，2 已超时)")
    private Integer status;//任务状态
    @Schema(title="任务紧急程度(0 一般，1 重要，2 非常重要)")
    private Integer position;//任务紧急程度
    @Schema(title="任务开始时间")
    private LocalDate startTime;//任务开始时间
    @Schema(title="任务结束时间")
    private LocalDate endTime;//任务结束时间
    @Schema(title="任务创建时间")
    private LocalDateTime createTime;//任务创建时间
    @Schema(title="任务更新时间")
    private LocalDateTime updateTime;//任务更新时间
}
