package com.video.controller;


import com.video.pojo.Users;
import com.video.pojo.UsersReport;
import com.video.pojo.VO.PublisherVideo;
import com.video.pojo.VO.UsersVO;
import com.video.service.UserService;
import com.video.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
@Api(value = "用户相关业务的接口",tags = {"用户相关业务的controller"})
@RequestMapping("/user")
public class UserController extends BasicController {

    @Autowired
    private UserService userService;


    @ApiOperation(value = "用户上传头像", notes = "用户上传头像的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    @PostMapping("/uploadFace")
    public JSONResult uploadFace(String userId, @RequestParam("file") MultipartFile[] files) throws Exception {

        if(StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户不能为空！");
        }

//        文件保存的命名空间
//        String fileSpace = "E:/workshop_wxxcx/video-dev";
//        头像保存的相对路径
        String uploadPathDB = "/" + userId + "/face";

        FileOutputStream fos = null;
        InputStream is = null;

//        文件传输到服务器
        try {
            if (files != null && files.length > 0) {

                String fileName = files[0].getOriginalFilename();

                if (StringUtils.isNoneBlank(fileName)) {
                    //                文件上传的最终保存路径
                    String finalFacePath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    //                设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);

                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //                    创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fos = new FileOutputStream(outFile);
                    is = files[0].getInputStream();
                    IOUtils.copy(is, fos);


                }
            } else {
                return JSONResult.errorMsg("上传出错!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JSONResult.errorMsg("上传出错!");
        }finally {
            if(fos != null) {
                fos.flush();
                fos.close();
            }
            if(is != null) {
                is.close();
            }
        }

        Users user = new Users();
        user.setId(userId);
        user.setFaceImage(uploadPathDB);
        userService.updateUserInfo(user);

        return JSONResult.ok(uploadPathDB);
    }


    @ApiOperation(value = "查询用户信息", notes = "查询用户信息的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    @PostMapping("/query")
    public JSONResult query(String userId, String fanId) throws Exception {

        if(StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户不能为空！");
        }

        Users userInfo = userService.queryUserInfo(userId);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userInfo,usersVO);

        usersVO.setFollow(userService.queryIfFollow(userId,fanId));

        return JSONResult.ok(usersVO);
    }


    @PostMapping("/queryPublisher")
    public JSONResult queryPublisher(String loginUserId, String videoId, String publisherUserId) throws Exception {

        if(StringUtils.isBlank(publisherUserId)) {
            return JSONResult.errorMsg("");
        }

//        查询视频发布者信息
        Users userInfo = userService.queryUserInfo(publisherUserId);
        UsersVO publisher = new UsersVO();
        BeanUtils.copyProperties(userInfo,publisher);

//      查询当前登录者和视频的点赞关系
        Boolean isUsersLikeVideo = userService.isUsersLikeVideo(loginUserId,videoId);

        PublisherVideo bean = new PublisherVideo();
        bean.setPublisher(publisher);
        bean.setUserLikeVideo(isUsersLikeVideo);

        return JSONResult.ok(bean);
    }

    @PostMapping("/beyourfans")
    public JSONResult beyourfans(String userId, String fanId) throws Exception {

        if(StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return JSONResult.errorMsg("");
        }

        userService.saveUserFanRelation(userId,fanId);


        return JSONResult.ok("关注成功！");
    }



    @PostMapping("/dontbeyourfans")
    public JSONResult dontbeyourfans(String userId, String fanId) throws Exception {

        if(StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return JSONResult.errorMsg("");
        }

        userService.deleteUserFanRelation(userId, fanId);


        return JSONResult.ok("取消关注成功");
    }

    @PostMapping("/reportUser")
    public JSONResult reportUser(@RequestBody UsersReport userReport) throws Exception {

//        保存举报信息
        userService.reportUser(userReport);


        return JSONResult.ok("举报成功...有你平台变得更美好...");
    }

}