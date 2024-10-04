package org.clc.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * 用户vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title="用户信息返回体")
public class LearnerVo {
    @Schema(title="用户ID")
    private String uid;//用户ID
    @Schema(title="权限等级")
    private int privileges;//权限等级
    @Schema(title="账号状态")
    private Boolean status;//账号状态
    @Schema(title="用户名")
    private String username;//用户名
    @Schema(title="密码")
    private String password;//密码
    @Schema(title="手机号")
    private String phone;//手机号
    @Schema(title="邮箱")
    private String email;//邮箱
    @Schema(title="性别")
    private Integer gender;//性别
    @Schema(title="年龄")
    private Integer age;//年龄
    @Schema(title="学历")
    private Integer degree;//学历
    @Schema(title="工作")
    private Integer job;//工作
    @Schema(title="个性签名")
    private String characters;//个性签名
    @Schema(title="创建时间")
    private LocalDateTime createTime;//创建时间
    @Schema(title="头像")
    private String image;//头像
}

