package org.clc.pojo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @version 1.0
 * @description: TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeMessage {
    @Schema(title="用户ID")
    private String uid;//用户ID
    @Schema(title="帖子ID")
    private String postId;//帖子ID
    @Schema(title="操作")
    private boolean isLike; // true表示点赞，false表示取消点赞
}