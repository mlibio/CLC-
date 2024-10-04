package org.clc.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * 登录vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title="登录返回体")
public class LoginVo {
    @Schema(title="加密token",type="String")
    private String token;//登录token
}
