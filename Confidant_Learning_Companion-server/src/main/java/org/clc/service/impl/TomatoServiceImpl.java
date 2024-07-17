package org.clc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import org.clc.context.BaseContext;
import org.clc.dto.PageQueryDto;
import org.clc.entity.Tomato;
import org.clc.mapper.TomatoMapper;
import org.clc.result.PageResult;
import org.clc.result.Result;
import org.clc.service.TomatoService;
import org.clc.vo.TomatoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @description: TODO
 */
@Service
public class TomatoServiceImpl extends ServiceImpl<TomatoMapper, Tomato> implements TomatoService {

    @Autowired
    private TomatoMapper tomatoMapper;

    @Override
    public PageResult getByUid(String currentId, PageQueryDto pageQueryDto) {
        try {
            QueryWrapper<Tomato> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uid", currentId);
            PageHelper.startPage(pageQueryDto.getPage(), pageQueryDto.getPageSize());
            List<Tomato> tomatoList = tomatoMapper.selectList(queryWrapper);
            List<TomatoVo> tomatoVoList = new ArrayList<>();
            for (Tomato tomato : tomatoList) {
                TomatoVo tomatoVo = new TomatoVo();
                BeanUtils.copyProperties(tomato, tomatoVo);
                tomatoVoList.add(tomatoVo);
            }
            PageResult pageResult = new PageResult();
            pageResult.setTotal(tomatoList.size());
            pageResult.setRecords(tomatoVoList);
            return pageResult;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result<String> updateTomate(String currentId,Integer time) {
        QueryWrapper<Tomato> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", currentId);
        queryWrapper.eq("tdate", LocalDate.now());
        Tomato tomato = tomatoMapper.selectOne(queryWrapper);
        if (tomato == null) {
            tomato = new Tomato();
            tomato.setUid(currentId);
            tomato.setCount(1);
            tomato.setTdate(LocalDate.now());
            tomato.setTime(time);
            tomatoMapper.insert(tomato);
        }else{
            tomato.setCount(tomato.getCount()+1);
            tomato.setTime(tomato.getTime()+time);
            tomatoMapper.updateById(tomato);
        }
        return Result.success("更新成功");
    }

    @Override
    public Tomato getTomatoToday() {
        String uid= BaseContext.getCurrentId();
        LocalDate date = LocalDate.now();
        QueryWrapper<Tomato> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("tdate", date);
        Tomato tomato=tomatoMapper.selectOne(queryWrapper);
        if (tomato == null) {
            return null;
        }
        return tomato;
    }
}
