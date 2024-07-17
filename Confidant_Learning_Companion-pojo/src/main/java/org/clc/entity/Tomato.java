package org.clc.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @version 1.0
 * 番茄统计
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title="番茄统计实体类")
public class Tomato {
    @Schema(title="数据库id主键")
    private int id;
    @Schema(title="用户ID")
    private String uid;//用户ID
    @Schema(title="日期(年月日)")
    private LocalDate tdate;//日期
    @Schema(title="番茄数量")
    private Integer count;//番茄数量
    @Schema(title="总时长(分钟)")
    private Integer time;//总时长(分钟)
}
