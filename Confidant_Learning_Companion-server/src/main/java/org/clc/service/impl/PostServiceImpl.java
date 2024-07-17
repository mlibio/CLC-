package org.clc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.clc.constant.MessageConstant;
import org.clc.context.BaseContext;
import org.clc.dto.PageQueryDto;
import org.clc.dto.PostIdDto;
import org.clc.entity.LearnerFavorPost;
import org.clc.entity.OperationLogs;
import org.clc.entity.Post;
import org.clc.entity.enumeration.OperationType;
import org.clc.mapper.LearnerFavorPostMapper;
import org.clc.mapper.OperationLogsMapper;
import org.clc.mapper.PostMapper;
import org.clc.result.PageResult;
import org.clc.result.Result;
import org.clc.service.PostService;
import org.clc.utils.OperationLogsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @version 1.0
 * @description: TODO
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private LearnerFavorPostMapper learnerFavorPostMapper;

    @Autowired
    private OperationLogsMapper operationLogsMapper;

    @Override
    public PageResult getFavorPost(PageQueryDto pageQueryDto) {
        String uid= BaseContext.getCurrentId();
        QueryWrapper<LearnerFavorPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        List<LearnerFavorPost> learnerFavorPosts = learnerFavorPostMapper.selectList(queryWrapper);
        List<String> postIdList = new ArrayList<>();
        if(learnerFavorPosts!=null){
            for (LearnerFavorPost learnerFavorPost:learnerFavorPosts){
                postIdList.add(learnerFavorPost.getPostId());
            }
        }else{
            return new PageResult(0,0,Collections.EMPTY_LIST);
        }
        if(!postIdList.isEmpty()){
            Page<Post> page=Page.of(pageQueryDto.getPage(),pageQueryDto.getPageSize());
            LambdaQueryWrapper<Post> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper.in("post_id", postIdList);
            Page<Post> p = postMapper.selectPage(page, queryWrapper1);
            //TODO:返回值有待商榷，返回帖子的作者信息，评论信息，标签信息。。。
            return new PageResult(p.getTotal(),p.getPages(),p.getRecords());
        }else{
            return new PageResult(0,0,Collections.EMPTY_LIST);
        }
    }

    @Override
    public PageResult getPosts(PageQueryDto pageQueryDto) {
        Page<Post> page=Page.of(pageQueryDto.getPage(),pageQueryDto.getPageSize());
        postMapper.selectList(new QueryWrapper<>());
        //TODO:返回值有待商榷，返回帖子的作者信息，评论信息，标签信息。。。
        return new PageResult(page.getTotal(),page.getPages(),page.getRecords());
    }

    @Override
    public Result<String> ban(PostIdDto postIdDto) {
        String postId=postIdDto.getPostId();
        Post post=selectByPostId(postId);
        post.setStatus(false);
        try {
            postMapper.updateById(post);
            //建立操作日志
            OperationLogs operationLogs= OperationLogsUtil.buildOperationLog(
                    OperationType.BAN_POST,
                    postIdDto.getPostId(),
                    true,
                    MessageConstant.SUCCESS
            );
            operationLogsMapper.insert(operationLogs);//添加操作日志
            return Result.success();
        } catch (Exception e) {
            //建立操作日志
            OperationLogs operationLogs= OperationLogsUtil.buildOperationLog(
                    OperationType.BAN_POST,
                    postIdDto.getPostId(),
                    false,
                    e.getMessage()
            );
            operationLogsMapper.insert(operationLogs);//添加操作日志
            return Result.error(500,MessageConstant.FAILED);
        }
    }

    @Override
    public Result<String> unban(PostIdDto postIdDto) {
        String postId=postIdDto.getPostId();
        Post post=selectByPostId(postId);
        post.setStatus(true);
        try {
            postMapper.updateById(post);
            //建立操作日志
            OperationLogs operationLogs= OperationLogsUtil.buildOperationLog(
                    OperationType.UNBAN_USER,
                    postIdDto.getPostId(),
                    true,
                    MessageConstant.SUCCESS
            );
            operationLogsMapper.insert(operationLogs);//添加操作日志
            return Result.success();
        } catch (Exception e) {
            //建立操作日志
            OperationLogs operationLogs= OperationLogsUtil.buildOperationLog(
                    OperationType.UNBAN_USER,
                    postIdDto.getPostId(),
                    false,
                    e.getMessage()
            );
            operationLogsMapper.insert(operationLogs);//添加操作日志
            return Result.error(500,MessageConstant.FAILED);
        }
    }

    private Post selectByPostId(String postId) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId);
        return postMapper.selectOne(queryWrapper);
    }
}
