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
@Schema(title="帖子postId请求类")
public class PostIdDto {
    @Schema(title="postId",type="String")
    private String postId;//用户ID
}
