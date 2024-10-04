package org.clc.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @description: TODO
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title="帖子请求体")
public class PostDto {
    @Schema(title="标题")
    private String title;//标题
    @Schema(title="内容")
    private String content;//内容
    @Schema(title="插图")
    private String image;//插图
}