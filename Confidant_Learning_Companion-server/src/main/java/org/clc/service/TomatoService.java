package org.clc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.clc.dto.PageQueryDto;
import org.clc.entity.Tomato;
import org.clc.result.PageResult;
import org.clc.result.Result;

/**
 * @version 1.0
 * @description: TODO
 */
public interface TomatoService extends IService<Tomato> {

    PageResult getByUid(String currentId, PageQueryDto pageQueryDto);

    Result<String> updateTomate(String currentId,Integer time);

    Tomato getTomatoToday();
}
