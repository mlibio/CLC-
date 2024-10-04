package org.clc.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.clc.pojo.dto.*;
import org.clc.pojo.entity.Learner;
import org.clc.common.result.PageResult;
import org.clc.common.result.Result;
import org.clc.pojo.vo.LearnerVo;

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
