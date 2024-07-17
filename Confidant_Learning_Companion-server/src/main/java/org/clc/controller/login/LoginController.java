package org.clc.controller.login;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.clc.dto.LearnerDto;
import org.clc.dto.LoginDto;
import org.clc.entity.Learner;
import org.clc.properties.JwtProperties;
import org.clc.result.Result;
import org.clc.service.LearnerService;
import org.clc.utils.JwtUtil;
import org.clc.vo.LearnerVo;
import org.clc.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.clc.constant.MessageConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * 登录
 */
@Validated//开启数据校验
@RestController
@CrossOrigin//跨域
@RequestMapping("/login")
@Slf4j
@Tag(name = "登录")
public class LoginController {
    @Autowired
    private LearnerService learnerService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping
    @Operation(summary = "登录接口",
            description  = "传入email和password，返回包含username、privileges和expireDate等信息的加密token",
            responses = {@ApiResponse(responseCode = "200", description = "成功登录"),
                        @ApiResponse(responseCode = "400", description = "请求错误"),
                        @ApiResponse(responseCode = "401", description = "未授权"),
                        @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<LoginVo> login(@RequestBody @Valid @Parameter(description = "登录参数", required = true) LoginDto loginDto) {
        try {
            if (loginDto.getEmail() == null || loginDto.getPassword() == null) {
                return Result.error(400,MessageConstant.ACCOUNT_NOT_FOUND);
            }
            Learner learner = learnerService.login(loginDto);
            if (learner == null) {
                return Result.error(400,MessageConstant.ACCOUNT_NOT_FOUND);
            }else if(!learner.getPassword().equals(loginDto.getPassword())){
                return Result.error(400,MessageConstant.PASSWORD_ERROR);
            }else if(!learner.getStatus()){
                return Result.error(401,MessageConstant.ACCOUNT_LOCKED);
            }
            Map<String, Object> claims = new HashMap<>();
            if(learner.getPrivileges()==0){
                claims.put("adminId", learner.getUid());
            }else if(learner.getPrivileges()==1){
                claims.put("userId", learner.getUid());
            }else{
                return Result.error(401,MessageConstant.ABNORMAL_PERMISSION_VALUE);
            }
            claims.put("username", learner.getUsername());//用户名
            claims.put("privileges", learner.getPrivileges());//权限等级
            String token;
            if (learner.getPrivileges() == 0) {//管理员登录
                token = JwtUtil.createJWT(
                        jwtProperties.getAdminSecretKey(),
                        jwtProperties.getAdminTtl(),
                        claims);

            } else if (learner.getPrivileges()==1) {//普通用户登录
                token = JwtUtil.createJWT(
                        jwtProperties.getUserSecretKey(),
                        jwtProperties.getUserTtl(),
                        claims);
            }else{
                return Result.error(401,MessageConstant.ABNORMAL_PERMISSION_VALUE);
            }
            LoginVo loginVo = new LoginVo();
            loginVo.setToken(token);
            return Result.success(200,"登陆成功",loginVo);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.error(500,"登陆失败："+e.getMessage());
        }
    }
    @PostMapping("/register")
    @Operation(summary = "注册接口",
            description  = "传入注册信息，返回包含username、privileges和expireDate等信息的加密token",
            responses = {@ApiResponse(responseCode = "200", description = "成功注册"),
                        @ApiResponse(responseCode = "400", description = "请求错误"),
                        @ApiResponse(responseCode = "401", description = "未授权"),
                        @ApiResponse(responseCode = "500", description = "服务器错误")})
    public Result<LearnerVo> register(@RequestBody @Parameter(description = "注册参数", required = true) LearnerDto learnerDto) throws Exception {
        if(learnerDto==null){
            return Result.error(400,MessageConstant.ILLEGAL_DATA);
        }
        if(learnerDto.getEmail()==null){
            return Result.error(400,MessageConstant.EMPTY_PASSWORD);
        }else if(learnerDto.getPassword()==null){
            return Result.error(400,MessageConstant.EMPTY_PASSWORD);
        }
        Learner learner = learnerService.getLearnerByEmail(learnerDto.getEmail());
        if(learner!=null){
            return Result.error(400,MessageConstant.ACCOUNT_ALREADY_EXISTS);
        }else{
            LearnerVo learnerVo=learnerService.register(learnerDto);
            return Result.success(200,"注册成功",learnerVo);
        }
    }
}
