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
@Schema(title="番茄更新请求体")
public class TomatoUpdateDto {
    @Schema(title="总时长(分钟)",type="Integer")
    private Integer time;//总时长(分钟)
}
