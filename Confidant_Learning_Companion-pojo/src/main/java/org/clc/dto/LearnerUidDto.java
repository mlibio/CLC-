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
@Schema(title="用户Uid请求类")
public class LearnerUidDto {
    @Schema(title="用户ID",type="String")
    private String uid;//用户ID
}
