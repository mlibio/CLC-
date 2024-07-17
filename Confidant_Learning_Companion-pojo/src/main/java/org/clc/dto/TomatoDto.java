package org.clc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @version 1.0
 * 番茄dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title="番茄统计请求体")
public class TomatoDto {
    //@Schema(title="用户ID",type="String",required=true)
    private String uid;//用户ID
    @Schema(title="日期(年月日)",type="LocalDate",required=true)
    private LocalDate tdate;//日期
    @Schema(title="番茄数量",type="Integer")
    private Integer count;//番茄数量
    @Schema(title="总时长(分钟)",type="Integer")
    private Integer time;//总时长(分钟)
}
