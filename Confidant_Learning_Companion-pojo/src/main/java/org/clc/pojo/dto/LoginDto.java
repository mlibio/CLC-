package org.clc.pojo.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * 登录dto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title="登录请求体")
public class LoginDto {
    @Schema(title="邮箱",description = "合法邮箱",type="String",required=true)
    //@Pattern(regexp = "^[\\w.-]+@[\\w.-]+$",message = "请输入合法邮箱")
    @NotNull(message = "邮箱不能为空")
    private String email;
    @Schema(title="密码",description = "5-16位非空字符",type="String",required=true)
    //@Pattern(regexp = "^\\S{5,16}$",message = "密码需要5-16位非空字符")
    @NotNull(message = "密码不能为空")
    private String password;
}
