package org.clc.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @version 1.0
 * 任务Vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "任务类")
public class TaskVo {
    @Schema(title="任务id",type="int")
    private String taskId;//任务ID
    @Schema(title = "任务名")
    private String name;//任务名
    @Schema(title = "任务详情")
    private String content;//任务详情
    @Schema(title = "任务状态")
    private Integer status;//任务状态
    @Schema(title = "任务紧急程度")
    private Integer position;//任务紧急程度
    @Schema(title = "任务开始时间")
    private LocalDate startTime;//任务开始时间
    @Schema(title = "任务结束时间")
    private LocalDate endTime;//任务结束时间
}
