package org.clc.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * 用户实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title="用户实体类")
@TableName("learner")
public class Learner {
    @Schema(title="数据库id主键")
    @TableId("id")
    private int id;
    @Schema(title="用户ID")
    @TableField(value = "uid")
    private String uid;//用户ID
    @Schema(title="权限等级(0 管理员，1 普通用户)")
    @TableField(value = "privileges")
    private int privileges;//权限等级
    @Schema(title="账号状态(0 封禁，1 正常)")
    @TableField(value = "status")
    private Boolean status;//账号状态
    @Schema(title="用户名")
    @TableField(value = "username")
    private String username;//用户名
    @Schema(title="密码")
    @TableField(value = "password")
    private String password;//密码
    @Schema(title="邮箱")
    @TableField(value = "email")
    private String email;//邮箱
    @Schema(title="手机号")
    @TableField(value = "phone")
    private String phone;//手机号
    @Schema(title="性别(0 不愿透露，1 男性，2 女性)")
    @TableField(value = "gender")
    private Integer gender;//性别
    @Schema(title="年龄")
    @TableField(value = "age")
    private Integer age;//年龄
    @Schema(title="学历(0 不愿透露，1 小学，2 初中，3 高中，4 大学，5 硕士，6 博士)")
    @TableField(value = "degree")
    private Integer degree;//学历
    @Schema(title="工作")
    @TableField(value = "job")
    private Integer job;//工作
    @Schema(title="个性签名")
    @TableField(value = "characters")
    private String characters;//个性签名
    @Schema(title="创建时间")
    @TableField(value = "create_time")
    private LocalDateTime createTime;//创建时间
    @Schema(title = "头像")
    @TableField(value = "image")
    private String image;
}
