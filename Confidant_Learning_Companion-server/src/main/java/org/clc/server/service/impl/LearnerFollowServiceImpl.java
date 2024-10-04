package org.clc.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.clc.common.constant.MessageConstant;
import org.clc.common.context.BaseContext;
import org.clc.pojo.entity.LearnerFollow;
import org.clc.server.mapper.LearnerFollowMapper;
import org.clc.common.result.Result;
import org.clc.server.service.LearnerFollowService;
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
