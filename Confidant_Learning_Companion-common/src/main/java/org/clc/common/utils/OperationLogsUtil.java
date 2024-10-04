package org.clc.common.utils;

import org.clc.common.context.BaseContext;
import org.clc.pojo.entity.OperationLogs;
import org.clc.pojo.entity.enumeration.OperationType;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @description: TODO
 */

public class OperationLogsUtil {
/**
     * 构建并初始化OperationLogs对象
     *
     * @param operationType  操作类型，应为OperationType枚举中的值
     * @param operandUid 操作对象的Uid
     * @param operationResult 操作结果，true表示成功，false表示失败
     * @param operationMessage 操作消息，成功时通常为成功信息，失败时为异常信息
     * @return 初始化完成的OperationLogs对象
     */

    public static OperationLogs buildOperationLog(OperationType operationType, String operandUid, boolean operationResult, String operationMessage) {
        OperationLogs operationLogs = new OperationLogs();
        operationLogs.setOperatorUid(BaseContext.getCurrentId()); // 获取当前操作员的UID
        operationLogs.setOperandUid(operandUid); // 设置被操作用户的UID
        operationLogs.setOperationType(operationType.getKey()); // 设置操作类型
        operationLogs.setOperationTime(LocalDateTime.now()); // 设置操作时间
        operationLogs.setOperationResult(operationResult); // 设置操作结果
        operationLogs.setOperationMessage(operationMessage); // 设置操作消息
        return operationLogs;
    }
}
