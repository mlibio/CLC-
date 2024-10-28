package org.clc.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.clc.common.constant.MessageConstant;
import org.clc.common.constant.StringConstant;
import org.clc.common.context.BaseContext;
import org.clc.pojo.dto.PageQueryDto;
import org.clc.pojo.dto.PostDto;
import org.clc.pojo.dto.PostIdDto;
import org.clc.pojo.entity.*;
import org.clc.pojo.entity.enumeration.OperationType;
import org.clc.server.mapper.*;
import org.clc.common.result.PageResult;
import org.clc.common.result.Result;
import org.clc.server.service.PostService;
import org.clc.common.utils.MyRandomStringGenerator;
import org.clc.common.utils.OperationLogsUtil;
import org.clc.pojo.vo.PostDetailVo;
import org.clc.pojo.vo.PostVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

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
        Page<Post> p=postMapper.selectPage(page,new QueryWrapper<>());
        //返回帖子的作者信息，标签信息
        List<Post> posts = p.getRecords();
        List<PostVo> postVos = getPostsVo(posts);
        return new PageResult(p.getTotal(),p.getPages(),postVos);
    }

    @Override
    public Result<String> ban(PostIdDto postIdDto) {
        String postId=postIdDto.getPostId();
        Post post=selectPostsByPostIds(List.of(postId)).get(0);
        post.setStatus(false);
        // 删除 Redis 中的缓存数据
        redisTemplate.delete(postId);
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
        Post post=selectPostsByPostIds(List.of(postId)).get(0);
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
        List<TagPost> tagPosts=tagPostMapper.selectList(new QueryWrapper<TagPost>().eq("post_id",post.getPostId()));
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
            List<TagPost> tagPosts=tagPostMapper.selectList(new QueryWrapper<TagPost>().eq("post_id",post.getPostId()));
            List<Tag> tags=new ArrayList<>();
            for(TagPost tagPost:tagPosts){
                tags.add(tagMapper.selectById(tagPost.getTagId()));
            }
            postVo.setTags(tags);
            postVos.add(postVo);
        }
        return postVos;
    }

    @Override
    public Result<String> addPost(PostDto postDto) {
        Post post=new Post();
        BeanUtils.copyProperties(postDto,post);
        post.setUid(BaseContext.getCurrentId());
        post.setPostId(StringConstant.PREFIX_FOR_POST+MyRandomStringGenerator.generateRandomString(8));
        post.setThumbs(0);
        post.setStatus(true);
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        try{
            postMapper.insert(post);
            return Result.success(MessageConstant.SUCCESS);
        }catch (RuntimeException e){
            return Result.error(500,MessageConstant.FAILED);
        }
    }

    /**
     * 获取最热帖子
     */
    @Override
    public PageResult getHotPosts(PageQueryDto pageQueryDto) {
        String key = StringConstant.PREFIX_FOR_CACHE_LIKES;
        // 分页获取按点赞数排序的帖子ID列表
        int pageNumber = pageQueryDto.getPage();
        int pageSize = pageQueryDto.getPageSize();
        // 计算起始索引和结束索引
        long start = (long) (pageNumber - 1) * pageSize;
        long end = ((long) pageNumber * pageSize - 1);
        Set<ZSetOperations.TypedTuple<String>> tuples;
        try {
            tuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
        } catch (Exception e) {
            tuples = null;
            // 处理Redis异常
            log.error(MessageConstant.FAILED_TO_FETCH_DATA_FROM_REDIS, e);
        }// 根据ID获取帖子
        List<Post> posts = new ArrayList<>();
        if (tuples != null) {
            for (ZSetOperations.TypedTuple<String> tuple : tuples) {
                String postId = tuple.getValue();
                // 如果Redis中没有数据，getPostFromCache方法会从数据库中加载并缓存
                Post post = getPostFromCache(postId);
                posts.add(post);
            }
        }else{
            PageResult pageResult = new PageResult();
            pageResult.setTotal(0L);
            pageResult.setPages(0L);
            pageResult.setRecords(Collections.EMPTY_LIST);
            return pageResult;
        }
        // 转换为PostVo列表
        List<PostVo> postVos = getPostsVo(posts);
        // 计算总记录数
        Long total = null;
        if (redisTemplate != null) {
            total = redisTemplate.opsForZSet().zCard(key);
        }
        // 如果 total 为 null，设置默认值
        if (total == null) {
            total = 0L;
        }
        // 计算总页数
        long pages = (total + pageSize - 1) / pageSize; // 向上取整
        // 构建PageResult对象
        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setPages(pages);
        pageResult.setRecords(postVos);
        return pageResult;
    }

    /**
     * 更新帖子点赞数
     */
    public void updateLikesInDatabase(Map<String, Double> likesMap) {
        if (likesMap == null || likesMap.isEmpty()) {
            return; // 如果没有需要更新的数据，直接返回
        }
        // 一次性获取所有需要更新的帖子
        List<Post> posts = selectPostsByPostIds(new ArrayList<>(likesMap.keySet()));
        // 更新每个帖子的点赞数
        for (Post post : posts) {
            if (likesMap.containsKey(post.getPostId())) {
                post.setThumbs(likesMap.get(post.getPostId()).intValue());
            }
        }
        // 批量更新帖子
        try {
            for (Post post : posts) {
                updateById(post);
            }
        } catch (Exception e) {
            // 处理异常，例如记录日志或抛出自定义异常
            throw new RuntimeException(MessageConstant.FAILED, e);
        }
    }

    public List<Post> selectPostsByPostIds(List<String> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("post_id", postIds);
        return postMapper.selectList(queryWrapper);
    }

    /**
     * 将Post存储到Redis中
     */
    public void cachePost(Post post) {
        String key = StringConstant.PREFIX_FOR_CACHE + post.getId();
        Map<String, String> postMap = new HashMap<>();
        postMap.put("uid", post.getUid());
        postMap.put("postId", post.getPostId());
        postMap.put("title", post.getTitle());
        postMap.put("content", post.getContent());
        postMap.put("thumbs", String.valueOf(post.getThumbs()));
        postMap.put("status", String.valueOf(post.getStatus()));
        postMap.put("image", post.getImage());
        postMap.put("createTime", post.getCreateTime().toString());
        postMap.put("updateTime", post.getUpdateTime().toString());
        redisTemplate.opsForHash().putAll(key, postMap);
        // 设置键过期时间为一周
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    /**
     * 实现点赞功能
     */
    @Override
    public void thumbComment(String postId) {
        // 使用postId作为key，从Redis的ZSet中获取点赞数
        int likes = getThumbFromCache(postId);
        // 点赞数加一
        //TODO：添加点赞人，被点赞贴，点赞状态
        likes++;
        // 将更新后的点赞数存回Redis的ZSet
        redisTemplate.opsForZSet().add(StringConstant.PREFIX_FOR_CACHE_LIKES, postId, likes);
    }
    /**
     * 实现取消点赞功能
     */
    @Override
    public void unThumbComment(String postId) {
        // 使用postId作为key，从Redis的ZSet中获取点赞数
        int likes = getThumbFromCache(postId);
        // 点赞数减一
        //TODO：删除点赞人，被点赞贴，点赞状态
        likes--;
        // 将更新后的点赞数存回Redis的ZSet
        redisTemplate.opsForZSet().add(StringConstant.PREFIX_FOR_CACHE_LIKES, postId, likes);
    }

    /**
     * 从Redis中取出thumb
     */
    private int getThumbFromCache(String postId) {
        Double score = redisTemplate.opsForZSet().score(StringConstant.PREFIX_FOR_CACHE_LIKES, postId);
        int likes;
        if (score == null) {
            // 如果Redis中没有记录，从数据库中加载并缓存
            Post post = selectPostsByPostIds(List.of(postId)).get(0);
            cacheThumbs(post.getPostId(),post.getThumbs());
            likes = post.getThumbs();
        } else {
            likes = score.intValue();
        }
        return likes;
    }
    /**
     * 将thumb存储到Redis中
     */
    private void cacheThumbs(String postId, Integer thumbs) {
        redisTemplate.opsForZSet().add(StringConstant.PREFIX_FOR_CACHE_LIKES, postId, thumbs);
    }
    /**
     * 从Redis中取出Post
     */
    public Post getPostFromCache(String postId) {
        String key = StringConstant.PREFIX_FOR_CACHE + postId;
        Map<Object, Object> postMap = redisTemplate.opsForHash().entries(key);
        if (postMap.isEmpty()) {
            // 如果Redis中没有数据，从数据库中加载并缓存
            Post post = selectPostsByPostIds(List.of(postId)).get(0);
            cachePost(post);
            return post;
        }
        return convertToPost(postMap);
    }
    /**
     * 将从Redis中取出的键值对转为Post
     */
    private Post convertToPost(Map<Object, Object> postMap) {
        Post post = new Post();
        post.setUid((String) postMap.get("uid"));
        post.setPostId((String) postMap.get("postId"));
        post.setTitle((String) postMap.get("title"));
        post.setContent((String) postMap.get("content"));
        post.setThumbs(Integer.parseInt((String) postMap.get("thumbs")));
        post.setStatus(Boolean.parseBoolean((String) postMap.get("status")));
        post.setImage((String) postMap.get("image"));
        post.setCreateTime(LocalDateTime.parse((String) postMap.get("createTime")));
        post.setUpdateTime(LocalDateTime.parse((String) postMap.get("updateTime")));
        return post;
    }
}
