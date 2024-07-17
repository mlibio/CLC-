package org.clc.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * 帖子实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title="帖子实体类")
public class Post {
    @Schema(title="数据库id主键")
    private int id;
    @Schema(title="用户ID")
    private String uid;//用户ID
    @Schema(title="帖子ID")
    private String postId;//帖子ID
    @Schema(title="标题")
    private String title;//标题
    @Schema(title="内容")
    private String content;//内容
    @Schema(title="点赞量")
    private Integer thumbs;//点赞量
    @Schema(title="状态(0 封禁，1 正常) ")
    private Boolean status;//状态
    @Schema(title="插图")
    private String image;//插图
    @Schema(title="创建时间")
    private LocalDateTime createTime;//创建时间
    @Schema(title="更新时间")
    private LocalDateTime updateTime;//更新时间
}
