package org.clc.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * 标签实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title="标签实体类")
public class Tag {
    @Schema(title="数据库id主键")
    private int id;
    @Schema(title="标签名")
    private String name;//标签名
}
