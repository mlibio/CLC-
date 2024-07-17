package org.clc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * @description: TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title="任务ID请求体")
public class TaskIdDto {
    @Schema(title="任务id",required=true)
    private String taskId;
}
