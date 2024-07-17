package org.clc.entity;

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
@Schema(title="用户关联类")
@TableName("learner_concern")
public class LearnerFollow {
    @Schema(title="数据库id主键")
    @TableId("id")
    private int id;
    @Schema(title="用户ID")
    @TableField(value = "uid")
    private String uid;
    @Schema(title="关注的用户ID")
    @TableField(value = "followed_learner")
    private String FollowedLearner;
}
