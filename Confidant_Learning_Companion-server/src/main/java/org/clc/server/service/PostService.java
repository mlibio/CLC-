package org.clc.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.clc.pojo.dto.PageQueryDto;
import org.clc.pojo.dto.PostDto;
import org.clc.pojo.dto.PostIdDto;
import org.clc.pojo.entity.Post;
import org.clc.common.result.PageResult;
import org.clc.common.result.Result;
import org.clc.pojo.vo.PostDetailVo;
import org.clc.pojo.vo.PostVo;

import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 */
public interface PostService extends IService<Post> {
    /**
     * 获取收藏帖子
     */
    PageResult getFavorPost(PageQueryDto pageQueryDto);

    PageResult getPosts(PageQueryDto pageQueryDto);

    Result<String> ban(PostIdDto postIdDto);

    Result<String> unban(PostIdDto postIdDto);

    PostDetailVo getPostDetail(Post post);

    List<PostVo> getPostsVo(List<Post> posts);

    Result<String> addPost(PostDto postDto);

    /**
     * 点赞
     */
    void thumbComment(String postId);

    /**
     * 获取热帖
     */
    PageResult getHotPosts(PageQueryDto pageQueryDto);

    List<Post> selectPostsByPostIds(List<String> postIds);

    /**
     * 缓存帖子
     */
    void cachePost(Post post);

    /**
     * 取消点赞
     */
    void unThumbComment(String postId);

    /**
     * 同步点赞数据到数据库
     */
    void updateLikesInDatabase(Map<String, Double> likesMap);
}
