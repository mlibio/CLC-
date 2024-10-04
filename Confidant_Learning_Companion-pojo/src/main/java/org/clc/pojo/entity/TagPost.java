package org.clc.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @version 1.0
 * 标签帖子关联实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title="标签帖子关联实体类")
public class TagPost {
    @Schema(title="数据库id主键")
    private int id;
    @Schema(title="标签id")
    private Integer tagId;
    @Schema(title="帖子postId")
    private String postId;
}
