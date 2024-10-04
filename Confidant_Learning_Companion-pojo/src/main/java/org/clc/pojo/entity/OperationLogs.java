package org.clc.pojo.entity;

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
 * @description: TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title="操作日志实体类")
@TableName("operation_logs")
public class OperationLogs {
    @Schema(title="数据库id主键")
    @TableId("id")
    private int id;
    @Schema(title="操作者ID")
    @TableField(value = "operator_uid")
    private String operatorUid;
    @Schema(title="操作对象ID")
    @TableField(value = "operand_uid")
    private String operandUid;
    @Schema(title="操作时间")
    @TableField(value = "operation_time")
    private LocalDateTime operationTime;
    @Schema(title="操作类型",description = "0 封禁用户，1 解封用户，2 设置管理员")
    @TableField(value = "operation_type")
    private Integer operationType;
    @Schema(title="操作结果",description = "0 失败，1 成功")
    @TableField(value = "operation_result")
    private Boolean operationResult;
    @Schema(title="操作信息")
    @TableField(value = "operation_message")
    private String operationMessage;
}
