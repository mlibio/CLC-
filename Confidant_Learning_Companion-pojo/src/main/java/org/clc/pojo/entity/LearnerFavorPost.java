package org.clc.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@Schema(title="用户帖子关联类")
@TableName("learner_favor_post")
public class LearnerFavorPost {
    @TableId(type = IdType.AUTO)
    private int id;
    @Schema(title="用户ID")
    @TableField(value = "uid")
    private String uid;
    @Schema(title="用户收藏的帖子ID")
    @TableField(value = "post_id")
    private String postId;
}
