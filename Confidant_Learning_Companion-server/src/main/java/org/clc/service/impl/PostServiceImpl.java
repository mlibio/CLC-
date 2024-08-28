package org.clc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.clc.constant.MessageConstant;
import org.clc.context.BaseContext;
import org.clc.dto.PageQueryDto;
import org.clc.dto.PostIdDto;
import org.clc.entity.*;
import org.clc.entity.enumeration.OperationType;
import org.clc.mapper.*;
import org.clc.result.PageResult;
import org.clc.result.Result;
import org.clc.service.PostService;
import org.clc.utils.OperationLogsUtil;
import org.clc.vo.PostDetailVo;
import org.clc.vo.PostVo;
import org.springframework.beans.BeanUtils;
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
    private LearnerMapper learnerMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private TagPostMapper tagPostMapper;

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
            //返回帖子的作者信息，标签信息
            List<Post> posts = p.getRecords();
            List<PostVo> postVos = getPostsVo(posts);
            return new PageResult(p.getTotal(),p.getPages(),postVos);
        }else{
            return new PageResult(0,0,Collections.EMPTY_LIST);
        }
    }

    @Override
    public PageResult getPosts(PageQueryDto pageQueryDto) {
        Page<Post> page=Page.of(pageQueryDto.getPage(),pageQueryDto.getPageSize());
        postMapper.selectList(new QueryWrapper<>());
        //返回帖子的作者信息，标签信息
        List<Post> posts = page.getRecords();
        List<PostVo> postVos = getPostsVo(posts);
        return new PageResult(page.getTotal(),page.getPages(),postVos);
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

    @Override
    public PostDetailVo getPostDetail(Post post) {
        PostDetailVo postDetailVo=new PostDetailVo();
        BeanUtils.copyProperties(post,postDetailVo);
        postDetailVo.setUsername(learnerMapper.selectOne(new QueryWrapper<Learner>().eq("uid",post.getUid())).getUsername());
        postDetailVo.setLearnerImage(learnerMapper.selectOne(new QueryWrapper<Learner>().eq("uid",post.getUid())).getImage());
        List<TagPost> tagPosts=tagPostMapper.selectList(new QueryWrapper<TagPost>().eq("postId",post.getPostId()));
        List<Tag> tags=new ArrayList<>();
        for(TagPost tagPost:tagPosts){
            tags.add(tagMapper.selectById(tagPost.getTagId()));
        }
        postDetailVo.setTags(tags);
        return postDetailVo;
    }


    @Override
    public List<PostVo> getPostsVo(List<Post> posts) {
        List<PostVo> postVos = new ArrayList<>();
        for(Post post:posts){
            PostVo postVo=new PostVo();
            BeanUtils.copyProperties(post,postVo);
            postVo.setUsername(learnerMapper.selectOne(new QueryWrapper<Learner>().eq("uid",post.getUid())).getUsername());
            postVo.setLearnerImage(learnerMapper.selectOne(new QueryWrapper<Learner>().eq("uid",post.getUid())).getImage());
            List<TagPost> tagPosts=tagPostMapper.selectList(new QueryWrapper<TagPost>().eq("postId",post.getPostId()));
            List<Tag> tags=new ArrayList<>();
            for(TagPost tagPost:tagPosts){
                tags.add(tagMapper.selectById(tagPost.getTagId()));
            }
            postVo.setTags(tags);
            postVos.add(postVo);
        }
        return postVos;
    }

    private Post selectByPostId(String postId) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId);
        return postMapper.selectOne(queryWrapper);
    }
}
