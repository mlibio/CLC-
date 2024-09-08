package org.clc.vo;

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
@Schema(title="评论返回体")
public class CommentVo {
    @Schema(title="评论ID")
    private String cId;//评论ID
    @Schema(title="用户名")
    private String username;//用户名
    @Schema(title="头像")
    private String learnerImage;
    @Schema(title="详情")
    private String content;//详情
    @Schema(title="创建时间")
    private LocalDateTime createTime;//创建时间
    @Schema(title="点赞量")
    private Integer thumbs;//点赞量
}
