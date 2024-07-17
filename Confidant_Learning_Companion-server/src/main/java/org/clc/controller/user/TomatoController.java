package org.clc.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.clc.constant.MessageConstant;
import org.clc.context.BaseContext;
import org.clc.dto.PageQueryDto;
import org.clc.dto.TomatoUpdateDto;
import org.clc.entity.Tomato;
import org.clc.result.PageResult;
import org.clc.result.Result;
import org.clc.service.TomatoService;
import org.clc.vo.TomatoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @version 1.0
 * @description: TODO
 */
@Validated//开启数据校验
@RestController
@CrossOrigin//跨域
@RequestMapping("/user/tomato")
@Slf4j
@Tag(name = "番茄统计")
public class TomatoController {

    @Autowired
    private TomatoService tomatoService;

    @GetMapping
    @Operation(summary = "返回用户当天番茄数据接口",
            description  = "返回用户ID、日期、当日番茄数量和当日总学习时长",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<TomatoVo> getTomatoToday(){
        Tomato tomato=tomatoService.getTomatoToday();
        if(tomato==null){
            TomatoVo tomatoVo=new TomatoVo();
            tomatoVo.setCount(0);
            tomatoVo.setTime(0);
            return Result.success(200,MessageConstant.SUCCESS,tomatoVo);
        }
        TomatoVo tomatoVo=new TomatoVo();
        BeanUtils.copyProperties(tomato,tomatoVo);
        return Result.success(200, MessageConstant.SUCCESS,tomatoVo);
    }

    @GetMapping("/statistics")
    @Operation(summary = "番茄统计接口",
            description  = "分页返回用户ID、日期、当日番茄数量和当日总学习时长",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public PageResult getTomato(@Parameter(description = "页数", required = true)
                                    @RequestParam(value = "page",defaultValue = "1") int page,
                                @Parameter(description = "页码大小", required = true)
                                    @RequestParam(value = "pageSize",defaultValue = "5") int pageSize){
        String currentId = BaseContext.getCurrentId();
        PageQueryDto pageQueryDto = new PageQueryDto();
        pageQueryDto.setPage(page);
        pageQueryDto.setPageSize(pageSize);
        return tomatoService.getByUid(currentId,pageQueryDto);
    }

    @PostMapping("/update")
    @Operation(summary = "番茄更新接口",
            description  = "传入本次番茄学习时长（分钟）",
            responses = {@ApiResponse(responseCode = "200", description = "成功"),
                    @ApiResponse(responseCode = "400", description = "请求错误"),
                    @ApiResponse(responseCode = "401", description = "未授权"),
                    @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<String> updateTomato(@RequestBody @Parameter(description = "本次番茄学习时长（分钟）", required = true) TomatoUpdateDto tomatoUpdateDto){
        String currentId = BaseContext.getCurrentId();
        Integer time=tomatoUpdateDto.getTime();
        return tomatoService.updateTomate(currentId,time);
    }
}
