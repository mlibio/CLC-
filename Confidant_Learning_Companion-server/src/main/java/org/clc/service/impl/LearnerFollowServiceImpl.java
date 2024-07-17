package org.clc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.clc.constant.MessageConstant;
import org.clc.context.BaseContext;
import org.clc.entity.LearnerFollow;
import org.clc.mapper.LearnerFollowMapper;
import org.clc.result.Result;
import org.clc.service.LearnerFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @description: TODO
 */
@Service
public class LearnerFollowServiceImpl extends ServiceImpl<LearnerFollowMapper, LearnerFollow> implements LearnerFollowService {

    @Autowired
    private LearnerFollowMapper learnerFollowMapper;

    @Override
    public Result<String> unfollow(String followedLearner) {
        String uid= BaseContext.getCurrentId();
        QueryWrapper<LearnerFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("followed_learner", followedLearner);
        learnerFollowMapper.delete(queryWrapper);
        return Result.success(MessageConstant.SUCCESS);
    }

    @Override
    public Result<String> follow(String followedLearner) {
        String uid= BaseContext.getCurrentId();
        LearnerFollow learnerFollow = new LearnerFollow();
        learnerFollow.setUid(uid);
        learnerFollow.setFollowedLearner(followedLearner);
        learnerFollowMapper.insert(learnerFollow);
        return Result.success(MessageConstant.SUCCESS);
    }
}
