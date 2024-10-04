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
@Schema(title="评论请求体")
public class CommentDto {
    @Schema(title="帖子postId")
    private String postId;//帖子postId
    @Schema(title="详情")
    private String content;//详情
}
