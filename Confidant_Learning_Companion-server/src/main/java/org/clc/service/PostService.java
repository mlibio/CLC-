package org.clc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.clc.dto.PageQueryDto;
import org.clc.dto.PostIdDto;
import org.clc.entity.Post;
import org.clc.result.PageResult;
import org.clc.result.Result;

/**
 * @version 1.0
 * @description: TODO
 */
public interface PostService extends IService<Post> {
    PageResult getFavorPost(PageQueryDto pageQueryDto);

    PageResult getPosts(PageQueryDto pageQueryDto);

    Result<String> ban(PostIdDto postIdDto);

    Result<String> unban(PostIdDto postIdDto);
}
