package org.clc.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @version 1.0
 * 番茄vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "番茄统计类")
public class TomatoVo {
    @Schema(title = "用户ID")
    private String uid;//用户ID
    @Schema(title = "日期")
    private LocalDate tdate;//日期
    @Schema(title = "番茄数量")
    private Integer count;//番茄数量
    @Schema(title = "时长")
    private Integer time;//时长
}
