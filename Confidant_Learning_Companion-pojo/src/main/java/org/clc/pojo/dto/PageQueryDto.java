package org.clc.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @version 1.0
 * @description: TODO
 */
@Data
//@Schema(title = "分页请求参数")
public class PageQueryDto{
    //@Schema(title = "页数")
    @Min(value = 1,message = "页数必须为正整数")
    private int page;
    @Min(value = 5,message = "页面大小必须为正整数，最小为5")
    //@Schema(title = "页面大小")
    private int pageSize;
}
