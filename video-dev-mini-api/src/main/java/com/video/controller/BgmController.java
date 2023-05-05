package com.video.controller;


import com.video.pojo.Users;
import com.video.pojo.VO.UsersVO;
import com.video.service.BgmService;
import com.video.service.UserService;
import com.video.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author 汤垚平
 * @version 1.0
 * 用户主页
 */
@RestController
@Api(value = "bgm相关业务的接口",tags = {"bgm相关业务的controller"})
@RequestMapping("/bgm")
public class BgmController extends BasicController {

    @Autowired
    private BgmService bgmService;

    //       查询bgm
    @ApiOperation(value = "获取背景音乐列表", notes = "获取背景音乐列表的接口")
    @PostMapping("/list")
    public JSONResult list() {

        return JSONResult.ok(bgmService.queryBgmList());
    }


}