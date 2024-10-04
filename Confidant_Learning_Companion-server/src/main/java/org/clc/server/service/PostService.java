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

/**
 * @version 1.0
 * @description: TODO
 */
public interface PostService extends IService<Post> {
    PageResult getFavorPost(PageQueryDto pageQueryDto);

    PageResult getPosts(PageQueryDto pageQueryDto);

    Result<String> ban(PostIdDto postIdDto);

    Result<String> unban(PostIdDto postIdDto);

    PostDetailVo getPostDetail(Post post);

    List<PostVo> getPostsVo(List<Post> posts);

    Result<String> addPost(PostDto postDto);

    void thumbComment(String postId);
}
