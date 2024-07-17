package org.clc.entity.enumeration;

import lombok.Getter;

/**
 * @version 1.0
 * @description: TODO
 */
@Getter
public enum OperationType {
    BAN_USER(0, "封禁用户"),
    UNBAN_USER(1, "解封用户"),
    SET_ADMIN(2, "设置管理员"),
    BAN_POST(3,"封禁帖子"),
    UNBAN_POST(4,"解封帖子");

    private final int key; // 数据库存储的键值
    private final String displayName; // 显示名称，用于前后端显示

    OperationType(int key, String displayName) {
        this.key = key;
        this.displayName = displayName;
    }

    // 可选：提供一个静态方法，根据键值查找枚举实例
    public static OperationType getByKey(int key) {
        for (OperationType operationType : values()) {
            if (operationType.getKey() == key) {
                return operationType;
            }
        }
        throw new IllegalArgumentException("Invalid key: " + key);
    }
}
