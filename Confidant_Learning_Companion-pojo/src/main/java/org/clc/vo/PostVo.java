package org.clc.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.clc.entity.Tag;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @version 1.0
 * @description: TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostVo {
    @Schema(title="用户ID")
    private String uid;//用户ID
    @Schema(title="用户名")
    private String username;//用户名
    @Schema(title = "头像")
    private String learnerImage;
    @Schema(title="帖子ID")
    private String postId;//帖子ID
    @Schema(title="标题")
    private String title;//标题
    @Schema(title="内容")
    private String content;//内容
    @Schema(title="点赞量")
    private Integer thumbs;//点赞量
    @Schema(title="插图")
    private String image;//插图
    @Schema(title="更新时间")
    private LocalDateTime updateTime;//更新时间
    @Schema(title="标签")
    private List<Tag> tags;
}
