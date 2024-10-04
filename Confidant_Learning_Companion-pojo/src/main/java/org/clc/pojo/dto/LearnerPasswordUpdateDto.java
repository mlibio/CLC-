package org.clc.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
@Schema(title="用户更新密码请求类")
public class LearnerPasswordUpdateDto {
    @Schema(title="原密码",description = "5-16位非空字符",type="String",required=true)
    @NotNull(message = "密码不能为空")
    @Pattern(regexp = "^\\S{5,16}$",message = "密码需要5-16位非空字符")
    private String oidPassword;//密码
    @Schema(title="新密码",description = "5-16位非空字符",type="String",required=true)
    @NotNull(message = "密码不能为空")
    @Pattern(regexp = "^\\S{5,16}$",message = "密码需要5-16位非空字符")
    private String newPassword;//密码
    @Schema(title="确认密码",description = "5-16位非空字符",type="String",required=true)
    @NotNull(message = "密码不能为空")
    @Pattern(regexp = "^\\S{5,16}$",message = "密码需要5-16位非空字符")
    private String ensurePassword;//密码
}
