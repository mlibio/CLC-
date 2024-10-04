package org.clc.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.clc.pojo.entity.LearnerFollow;
import org.clc.common.result.Result;

/**
 * @version 1.0
 * @description: TODO
 */
public interface LearnerFollowService extends IService<LearnerFollow> {
    Result<String> unfollow(String followedLearner);

    Result<String> follow(String followedLearner);
}
