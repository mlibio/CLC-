package org.clc.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.clc.common.constant.MessageConstant;
import org.clc.common.constant.QueueConstant;
import org.clc.common.constant.RedisKeyConstant;
import org.clc.common.constant.StringConstant;
import org.clc.common.context.BaseContext;
import org.clc.pojo.dto.PageQueryDto;
import org.clc.pojo.dto.PostDto;
import org.clc.pojo.dto.PostIdDto;
import org.clc.pojo.dto.PostUpdateDto;
import org.clc.pojo.entity.*;
import org.clc.pojo.message.PostLikeMessage;
import org.clc.pojo.message.PostUpdateMessage;
import org.clc.server.mapper.*;
import org.clc.common.result.PageResult;
import org.clc.common.result.Result;
import org.clc.server.service.PostService;
import org.clc.common.utils.MyRandomStringGenerator;
import org.clc.pojo.vo.PostDetailVo;
import org.clc.pojo.vo.PostVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @version 1.0
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
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PostLikeMapper postLikeMapper;

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
            QueryWrapper<Post> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.in("post_id", postIdList);
            queryWrapper1.eq("status", 1);
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
        Page<Post> p=postMapper.selectPage(page,new QueryWrapper<Post>().eq("status",1));
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
        redisTemplate.delete(RedisKeyConstant.PREFIX_FOR_CACHE_POST + postId);
        try {
            postMapper.updateById(post);
            return Result.success();
        } catch (Exception e) {
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
            return Result.success();
        } catch (Exception e) {
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
        String key = RedisKeyConstant.PREFIX_FOR_CACHE_LIKES;
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
    @Transactional
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
            // 处理异常
            log.error(MessageConstant.FAILED, e);
            throw new RuntimeException(MessageConstant.FAILED, e);
        }
    }
    /**
     * 更新帖子，异步更新缓存
     * @return
     */
    @Override
    public Result<String> updatePost(PostUpdateDto postUpdateDto) {
        // 更新数据库
        try{
            if(selectPostsByPostIds(List.of(postUpdateDto.getPostId())).isEmpty()){
                return Result.error(400,MessageConstant.EMPTY_KEY_PARAMETERS);
            }
            Post post = selectPostsByPostIds(List.of(postUpdateDto.getPostId())).get(0);
            BeanUtils.copyProperties(postUpdateDto, post);
            post.setUpdateTime(LocalDateTime.now());
            updateById(post);
            // 通过消息队列来异步更新缓存
            PostUpdateMessage message=new PostUpdateMessage();
            BeanUtils.copyProperties(postUpdateDto, message);
            // 将消息发送到队列
            rabbitTemplate.convertAndSend(QueueConstant.POST_UPDATE_QUEUE, message);
            return Result.success(MessageConstant.SUCCESS);
        }catch (Exception e){
            log.error(MessageConstant.FAILED, e);
            return Result.error(500,MessageConstant.FAILED);
        }
    }
    /**
     * 根据postIds查询post，先从Redis中查询，若没有查到，则从数据库中查询，并将数据同步到Redis中
     */
    public List<Post> selectPostsByPostIds(List<String> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 用于存储从Redis和数据库获取的帖子
        Map<String, Post> postsMap = new HashMap<>();

        // 尝试从Redis中获取帖子
        for (String postId : postIds) {
            String key = RedisKeyConstant.PREFIX_FOR_CACHE_POST + postId;
            Map<Object, Object> postFromRedis = redisTemplate.opsForHash().entries(key);
            if (!postFromRedis.isEmpty()) {
                Post post = convertToPost(postFromRedis);
                postsMap.put(postId, post);
            }
        }

        // 找出没有在Redis中找到的postId
        List<String> missingPostIds = postIds.stream()
                .filter(id -> !postsMap.containsKey(id))
                .collect(Collectors.toList());

        // 如果有缺失的postId，从数据库中查询
        if (!missingPostIds.isEmpty()) {
            QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("post_id", missingPostIds);
            List<Post> postsFromDB = postMapper.selectList(queryWrapper.eq("status",1));

            // 更新到Redis并添加到结果中
            for (Post post : postsFromDB) {
                cachePost(post);
                postsMap.put(post.getPostId(), post);
            }
        }

        // 按照原始postIds的顺序返回帖子列表
        return postIds.stream()
                .map(postsMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    /**
     * 将Post存储到Redis中
     */
    public void cachePost(Post post) {
        String key = RedisKeyConstant.PREFIX_FOR_CACHE_POST + post.getPostId();
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
        // 设置键过期时间为一天
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
    }
    /**
     * 实现点赞功能
     */
    @Override
    public void thumbPost(String postId) {
        // 使用postId构建key，从Redis的ZSet中获取点赞数
        int likes = getThumbFromCache(postId);
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId).eq(PostLike::getUid, BaseContext.getCurrentId());
        PostLike like = postLikeMapper.selectOne(wrapper);
        boolean isLike = like == null; // 如果不存在，则认为是点赞；如果存在，则认为是取消点赞
        if(like == null) {
            likes--;
        }else {
            likes++;
        }
        // 将更新后的点赞数存回Redis的ZSet
        redisTemplate.opsForZSet().add(RedisKeyConstant.PREFIX_FOR_CACHE_LIKES, postId, likes);
        // 创建并发送消息，记录点赞人，被点赞贴
        PostLikeMessage message = new PostLikeMessage();
        message.setPostId(postId);
        message.setUid(BaseContext.getCurrentId());
        message.setLike(isLike);
        rabbitTemplate.convertAndSend(QueueConstant.POST_LIKE_UPDATE_QUEUE, message);
    }
    /**
     * 从Redis中取出thumb
     */
    private int getThumbFromCache(String postId) {
        Double score = redisTemplate.opsForZSet().score(RedisKeyConstant.PREFIX_FOR_CACHE_LIKES, postId);
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
        redisTemplate.opsForZSet().add(RedisKeyConstant.PREFIX_FOR_CACHE_LIKES, postId, thumbs);
    }
    /**
     * 从Redis中取出Post
     */
    public Post getPostFromCache(String postId) {
        String key = RedisKeyConstant.PREFIX_FOR_CACHE_POST + postId;
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
