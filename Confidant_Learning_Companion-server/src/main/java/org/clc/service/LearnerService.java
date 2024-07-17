package org.clc.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.clc.dto.*;
import org.clc.entity.Learner;
import org.clc.result.PageResult;
import org.clc.result.Result;
import org.clc.vo.LearnerVo;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @version 1.0
 * @description: TODO
 */
public interface LearnerService extends IService<Learner> {
    Learner login(LoginDto loginDto);

    Learner getLearnerByEmail(String email);

    LearnerVo register(LearnerDto learnerDto) throws Exception;

    PageResult pageQuery(PageQueryDto pageQueryDto);

    Learner getLearnerByUid(String uid);

    void updateByUid(Learner learner);

    Learner getLearnerByPhone(String phone);

    Result<LearnerVo> updateLearner(LearnerUpdateDto learnerUpdateDto);

    Result<String> updatePassword(LearnerPasswordUpdateDto learnerPasswordUpdateDto);
}
