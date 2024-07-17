package org.clc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @version 1.0
 * 任务dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title="任务请求体")
public class TaskDto {
    @Schema(title="任务名",type="String",required=true)
    private String name;//任务名
    @Schema(title="任务详情",type="String",required=true)
    private String content;//任务详情
    @Schema(title="状态(0 待办，1 已完成，2 已超时)",type="Integer")
    private Integer status;//任务状态
    @Schema(title="任务紧急程度(0 一般，1 重要，2 非常重要)",type="Integer")
    private Integer position;//任务紧急程度
    @Schema(title="任务开始时间",type="LocalDate")
    private LocalDate startTime;//任务开始时间
    @Schema(title="任务结束时间",type="LocalDate")
    private LocalDate endTime;//任务结束时间
}
