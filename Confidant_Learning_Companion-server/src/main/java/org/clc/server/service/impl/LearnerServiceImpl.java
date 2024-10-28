package org.clc.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.clc.common.constant.StringConstant;
import org.clc.common.constant.MessageConstant;
import org.clc.common.context.BaseContext;
import org.clc.pojo.dto.*;
import org.clc.pojo.entity.Learner;
import org.clc.common.exception.DuplicatePhoneException;
import org.clc.server.service.LearnerService;
import org.clc.server.mapper.LearnerMapper;
import org.clc.common.result.PageResult;
import org.clc.common.result.Result;
import org.clc.common.utils.MyRandomStringGenerator;
import org.clc.pojo.vo.LearnerVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

/**
 * @version 1.0
 * @description: TODO
 */
@Service
@Slf4j
public class LearnerServiceImpl extends ServiceImpl<LearnerMapper, Learner> implements LearnerService {
    @Autowired
    private LearnerMapper learnerMapper;

    /**
     * 用户登录
     * @param loginDto
     * @return
     */
    @Override
    public Learner login(LoginDto loginDto) {
        return getLearnerByEmail(loginDto.getEmail());
    }
    /**
     * 根据邮箱查找用户
     * @param email
     * @return
     */
    @Override
    public Learner getLearnerByEmail(String email) {
        QueryWrapper<Learner> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return learnerMapper.selectOne(queryWrapper);
    }

    /**
     * 用户注册
     * @param learnerDto
     * @return
     */
    @Override
    public LearnerVo register(LearnerDto learnerDto) throws Exception {
        Learner learner = new Learner();
        BeanUtils.copyProperties(learnerDto, learner);
        try {
            String uid= MyRandomStringGenerator.generateRandomString(8);
            learner.setUid(uid);//Uid：********
            if (learner.getUsername() == null) {
                learner.setUsername(StringConstant.PREFIX_FOR_NAME + uid);
            }
            if(learner.getPhone()!=null && !learner.getPhone().isEmpty()){//检查手机号是否已存在
                Learner learner1=getLearnerByPhone(learner.getPhone());
                if(learner1!=null){
                    throw new DuplicatePhoneException(MessageConstant.PHONE_ALREADY_EXISTS);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        learner.setCreateTime(LocalDateTime.now());
        learner.setPrivileges(1);
        learner.setStatus(true);
        learnerMapper.insert(learner);
        LearnerVo learnerVo = new LearnerVo();
        BeanUtils.copyProperties(learner, learnerVo);
        return learnerVo;
    }

    @Override
    public Learner getLearnerByPhone(String phone) {
        QueryWrapper<Learner> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        return learnerMapper.selectOne(queryWrapper);
    }

    @Override
    public Result<LearnerVo> updateLearner(LearnerUpdateDto learnerUpdateDto) {
        Learner learner = getLearnerByUid(BaseContext.getCurrentId());
        if(learner==null){
            return Result.error(404,MessageConstant.ACCOUNT_NOT_FOUND);
        }
        learnerMapper.updateById(learner);
        LearnerVo learnerVo = new LearnerVo();
        BeanUtils.copyProperties(learner, learnerVo);
        return Result.success(200,MessageConstant.SUCCESS,learnerVo);
    }

    @Override
    public Result<String> updatePassword(LearnerPasswordUpdateDto learnerPasswordUpdateDto) {
        Learner learner = getLearnerByUid(BaseContext.getCurrentId());
        // 首先检查learner是否存在
        if (learner == null) {
            // 如果learner不存在，则返回404错误，表示账户未找到
            return Result.error(404, MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 检查旧密码是否正确
        String oldPassword = learnerPasswordUpdateDto.getOidPassword();
        if (!learner.getPassword().equals(oldPassword)) {
            // 如果旧密码不匹配，则返回404错误，表示密码错误
            return Result.error(404, MessageConstant.PASSWORD_ERROR);
        }

        // 检查新密码和确认密码是否一致
        String newPassword = learnerPasswordUpdateDto.getNewPassword();
        String confirmPassword = learnerPasswordUpdateDto.getEnsurePassword();
        if (!newPassword.equals(confirmPassword)) {
            // 如果新密码和确认密码不一致，则返回400错误，表示确认密码与新密码不符
            return Result.error(400, MessageConstant.THE_CONFIRMATION_IS_NOT_THE_SAME_AS_THE_NEW_PASSWORD);
        }

        // 确保新密码不同于旧密码
        if (oldPassword.equals(newPassword)) {
            // 如果新密码与旧密码相同，则返回400错误，表示新密码不能与旧密码相同
            return Result.error(400, MessageConstant.NEW_PASSWORD_IS_THE_SAME_AS_THE_OLD_ONE);
        }

        // 更新learner的密码并保存到数据库
        learner.setPassword(newPassword);
        learnerMapper.updateById(learner);

        // 成功更新密码后，返回200成功状态和成功消息
        return Result.success(MessageConstant.SUCCESS);
    }

    @Override
    public PageResult pageQuery(PageQueryDto pageQueryDto) {
        try {
            // PageHelper分页
            //PageHelper.startPage(pageQueryDto.getPage(), pageQueryDto.getPageSize());
            //List<Learner> learners = learnerMapper.selectList(new QueryWrapper<>());

            // MP分页
            Page<Learner> page=Page.of(pageQueryDto.getPage(),pageQueryDto.getPageSize());
            Page<Learner> p=lambdaQuery().page(page);
            PageResult pageResult=new PageResult();
            pageResult.setTotal(p.getTotal());
            pageResult.setPages(p.getPages());
            List<Learner> learnerList=p.getRecords();
            pageResult.setRecords(BeanUtil.copyToList(learnerList,LearnerVo.class));
            return pageResult;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Learner getLearnerByUid(String uid) {
        QueryWrapper<Learner> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return learnerMapper.selectOne(queryWrapper);
    }

    @Override
    public void updateByUid(Learner learner) {
        UpdateWrapper<Learner> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", learner.getUid());
        boolean result = this.update(learner, updateWrapper);
        if (!result) {
            throw new RuntimeException(MessageConstant.FAILED);
        }
    }
}
