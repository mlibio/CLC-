package org.clc.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.clc.pojo.dto.PageQueryDto;
import org.clc.pojo.entity.Tomato;
import org.clc.common.result.PageResult;
import org.clc.common.result.Result;

/**
 * @version 1.0
 * @description: TODO
 */
public interface TomatoService extends IService<Tomato> {

    PageResult getByUid(String currentId, PageQueryDto pageQueryDto);

    Result<String> updateTomate(String currentId,Integer time);

    Tomato getTomatoToday();
}
