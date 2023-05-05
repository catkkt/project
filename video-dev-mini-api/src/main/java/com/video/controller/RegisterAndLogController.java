package com.video.controller;

import com.video.pojo.Users;
import com.video.pojo.VO.UsersVO;
import com.video.service.UserService;
import com.video.utils.JSONResult;
import com.video.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author 汤垚平
 * @version 1.0
 * 登录注册等功能
 */
@RestController
@Api(value = "用户相关业务的接口",tags = {"用户相关业务的controller"})
public class RegisterAndLogController extends BasicController{

    @Autowired
    private UserService userService;

//    1.用户注册
    @ApiOperation(value = "用户注册",notes = "用户注册的接口")
    @PostMapping("/register")
    public JSONResult register(@RequestBody Users user) throws Exception {
//        1. 判断前端传来的用户名和密码是否为空
        if(StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())) {
            JSONResult.errorMsg("用户名和密码不能为空！");
        }
//          2. 判断用户名是否存在
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());

//        3. 保存用户，注册信息
        if(!usernameIsExist) {
//            用户不存在
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));//密码加密
            user.setFansCounts(0);
            user.setReceiveLikeCounts(0);
            user.setFollowCounts(0);
            userService.saveUser(user);
        } else {
            return JSONResult.errorMsg("用户名已经存在!");
        }

        user.setPassword("");

        UsersVO usersVO = setUserRedisSessionToken(user);

        return JSONResult.ok(usersVO);
    }

//    公用的redisSession
    public UsersVO setUserRedisSessionToken(Users userModel) {

        String uniqueToken = UUID.randomUUID().toString();
        redis.set(USER_REDIS_SESSION + ":" +userModel.getId(),uniqueToken,30*60*1000);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userModel,usersVO);
        usersVO.setUserToken(uniqueToken);

        return  usersVO;

    }

//       2.用户登录
    @ApiOperation(value = "用户登录",notes = "用户登录的接口")
    @PostMapping("/login")
    public JSONResult Login(@RequestBody Users user) throws Exception {
    //        1. 判断用户名和密码是否为空
        if(StringUtils.isBlank(user.getUsername())||StringUtils.isBlank(user.getPassword())) {
            JSONResult.ok("用户名和密码不能为空！");
        }
    //          2. 判断用户信息是否正确
        Users userForLogin = userService.queryUserForLogin(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));

        //        3. 登录进入主菜单
        if(userForLogin != null) {
            userForLogin.setPassword("");

            UsersVO usersVO = setUserRedisSessionToken(userForLogin);
            return JSONResult.ok(usersVO);
        } else {
        return JSONResult.errorMsg("用户名或密码不正确，请重试!");
        }

    }

//        3.用户注销
    @ApiOperation(value = "用户注销",notes = "用户注销的接口")
    @ApiImplicitParam(name = "userId",value = "用户id",required = true,dataType = "String",paramType = "query")
    @PostMapping("/logout")
    public JSONResult logout(String userId) throws Exception {
        redis.del(USER_REDIS_SESSION + ":" +userId);
        userService.deleteUser(userId);
        return JSONResult.ok();
    }

}
