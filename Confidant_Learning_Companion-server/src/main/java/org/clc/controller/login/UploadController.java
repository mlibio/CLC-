package org.clc.controller.login;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.clc.constant.MessageConstant;
import org.clc.exception.UploadFailedException;
import org.clc.result.Result;
import org.clc.utils.AliOssUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.UUID;

/**
 * @version 1.0
 * @description: TODO
 */
@Slf4j
@CrossOrigin//跨域
@RestController
@RequestMapping("/upload")
@Tag(name = "文件上传")
public class UploadController {
    private final AliOssUtil aliOssUtil;

    public UploadController(AliOssUtil aliOssUtil) {
        this.aliOssUtil = aliOssUtil;
    }

    @PostMapping("/image")
    @Operation(summary = "图片上传接口",
            description  = "上传图片不能大于2M,仅能上传png jpg jpeg类型图片,返回图片存储地址filePath",
            responses = {@ApiResponse(responseCode = "200", description = "成功上传"),
                        @ApiResponse(responseCode = "400", description = "请求错误"),
                        @ApiResponse(responseCode = "401", description = "未授权"),
                        @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result upload(@RequestParam("image") @Parameter(description = "图片", required = true) MultipartFile image) {
        if(image.isEmpty()) {
            return Result.error(400,MessageConstant.UPLOAD_FAILED);
        }
        if(image.getSize()>2*1024*1024) {
            return Result.error(400,MessageConstant.FILE_SIZE_EXCEEDED);
        }
        //获取原始文件名
        String originalFilename = image.getOriginalFilename();
        // 获取后缀
        String suffix = StrUtil.subAfter(originalFilename, ".", true);
        // 检查文件类型是否为PNG、JPG或JPEG
        if (!("png".equalsIgnoreCase(suffix) || "jpg".equalsIgnoreCase(suffix) || "jpeg".equalsIgnoreCase(suffix))) {
            return Result.error(400,MessageConstant.INVALID_FILE_TYPE);
        }
        // 生成新文件名
        String uuid = UUID.randomUUID().toString().replace("-","");
        String fileName = uuid + "." + suffix;
        // 上传图片
        try {
            //文件请求路径
            System.out.println("传输图片");
            String filePath = aliOssUtil.uploadUserImage(image.getBytes(),fileName);
            // 返回图片访问路径
            log.debug("文件上传成功，{}", filePath);
            return Result.success(200,MessageConstant.SUCCESS,filePath);
        } catch (IOException e) {
            log.debug(MessageConstant.UPLOAD_FAILED,e);
            return Result.error(500,MessageConstant.UPLOAD_FAILED);
        }
    }
}
