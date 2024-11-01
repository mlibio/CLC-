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
public class PostUpdateMessage{
    @Schema(title="帖子ID")
    private String postId;//帖子ID
    @Schema(title="标题")
    private String title;//标题
    @Schema(title="点赞量")
    private Integer thumbs;//点赞量
    @Schema(title="内容")
    private String content;//内容
    @Schema(title="插图")
    private String image;//插图
}
