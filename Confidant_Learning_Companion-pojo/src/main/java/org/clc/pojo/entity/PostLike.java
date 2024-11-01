package org.clc.pojo.entity;

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
@Schema(title="帖子点赞实体类")
public class PostLike {
    @Schema(title="数据库id主键")
    private int id;
    @Schema(title="用户ID")
    private String uid;//用户ID
    @Schema(title="帖子ID")
    private String postId;//帖子ID
}
