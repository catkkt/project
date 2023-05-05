package com.video.controller;


import com.video.enums.VideoStatusEnum;
import com.video.org.n3r.idworker.utils.FetchVideoCover;
import com.video.pojo.Bgm;
import com.video.pojo.Comments;
import com.video.pojo.Videos;
import com.video.service.BgmService;
import com.video.service.VideoService;
import com.video.utils.JSONResult;
import com.video.utils.MergeVideoBgm;
import com.video.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * @author 汤垚平
 * @version 1.0
 * 用户主页
 */
@RestController
@Api(value = "视频相关业务的接口",tags = {"视频相关业务的controller"})
@RequestMapping("/video")
public class VideoController extends BasicController {

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;

    //        上传视频
    @ApiOperation(value = "视频上传接口", notes = "视频上传的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId", value="用户id", required=true,
                    dataType="String", paramType="form"),
            @ApiImplicitParam(name="bgmId", value="背景音乐id", required=false,
                    dataType="String", paramType="form"),
            @ApiImplicitParam(name="videoSeconds", value="背景音乐播放长度", required=true,
                    dataType="String", paramType="form"),
            @ApiImplicitParam(name="videoWidth", value="视频宽度", required=true,
                    dataType="String", paramType="form"),
            @ApiImplicitParam(name="videoHeight", value="视频高度", required=true,
                    dataType="String", paramType="form"),
            @ApiImplicitParam(name="desc", value="视频描述", required=false,
                    dataType="String", paramType="form")
    })
    @PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
    public JSONResult upload(String userId,
                             String bgmId, double videoSeconds,
                             int videoWidth, int videoHeight,
                             String desc,
                             @ApiParam(value="短视频", required=true)
                                         MultipartFile file) throws Exception {

        if(StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("用户不能为空！");
        }


//        文件保存的相对路径
        String uploadPathDB = "/" + userId + "/video";
        String coverPathDB = "/" + userId + "/video";

        FileOutputStream fos = null;
        InputStream is = null;

//        文件上传的最终保存路径
        String finalVideoPath = "";


//        文件传输到服务器
        try {
            if (file != null) {

                String fileName = file.getOriginalFilename();

//                    eg:abc.pm4
//              fix bug: 解决小程序端OK，PC端不OK的bug，原因：PC端和小程序端对临时视频的命名不同
//				String fileNamePrefix = fileName.split("\\.")[0];

                String arrayFilenameItem[] = fileName.split("\\.");
                String fileNamePrefix = "";
                for (int i = 0; i < arrayFilenameItem.length - 1; i++) {
                    fileNamePrefix += arrayFilenameItem[i];
                }


                if (StringUtils.isNoneBlank(fileName)) {

                    finalVideoPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    //                设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);
                    coverPathDB = coverPathDB + "/" + fileNamePrefix + ".jpg";

                    File outFile = new File(finalVideoPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //                    创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fos = new FileOutputStream(outFile);
                    is = file.getInputStream();
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
                is.close();
            }
        }

//        判断bgmId是否为空，如果不为空，
//        那就查询bgm的信息，并且合并视频，生产新的视频
        if(StringUtils.isNoneBlank(bgmId)) {
            Bgm bgm = bgmService.queryBgmList(bgmId);
            String bgmInputPath = FILE_SPACE_BGM + bgm.getPath();

            MergeVideoBgm mergeTool = new MergeVideoBgm(FFMPEG_EXE);
            String videoInputPath = finalVideoPath;

            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
            finalVideoPath = FILE_SPACE + uploadPathDB;

            mergeTool.convertor(videoInputPath, bgmInputPath,videoSeconds,finalVideoPath);
        }

        System.out.println("uploadPathDB=" + uploadPathDB);
        System.out.println("finalPath=" + finalVideoPath);

        // 对视频进行截图
        FetchVideoCover videoInfo = new FetchVideoCover(FFMPEG_EXE);
        videoInfo.getCover(finalVideoPath, FILE_SPACE + coverPathDB);


//        保存视频信息到数据库
        Videos video = new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoSeconds((float) videoSeconds);
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setVideoDesc(desc);
        video.setVideoPath(uploadPathDB);
        video.setCoverPath(coverPathDB);
        video.setStatus(VideoStatusEnum.SUCCESS.value);
        video.setCreateTime(new Date());

        String videoId = videoService.saveVideo(video);

        return JSONResult.ok(videoId);
    }


    //        上传封面
    @ApiOperation(value = "上传封面接口", notes = "上传封面的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "视频id主键", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true,
                    dataType = "String", paramType = "form")
    })
    @PostMapping(value = "/uploadCover",headers = "content-type=multipart/form-data")
    public JSONResult upload(String videoId,String userId,
                             @ApiParam(value="短视频", required=true) MultipartFile file) throws Exception {

        if(StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("视频id和用户不能为空!");
        }

//        文件保存的相对路径
        String coverPathDB = "/" + userId + "/video";

        FileOutputStream fos = null;
        InputStream is = null;

//        文件上传的最终保存路径
        String finalCoverPath = "";


//        文件传输到服务器
        try {
            if (file != null) {

                String fileName = file.getOriginalFilename();

//                    eg:abc.pm4
//              fix bug: 解决小程序端OK，PC端不OK的bug，原因：PC端和小程序端对临时视频的命名不同
//				String fileNamePrefix = fileName.split("\\.")[0];

                String arrayFilenameItem[] = fileName.split("\\.");
                String fileNamePrefix = "";
                for (int i = 0; i < arrayFilenameItem.length - 1; i++) {
                    fileNamePrefix += arrayFilenameItem[i];
                }


                if (StringUtils.isNoneBlank(fileName)) {

                    finalCoverPath = FILE_SPACE +coverPathDB + "/" + fileName;
                    //                设置数据库保存的路径
                    coverPathDB += ("/" + fileName);

                    File outFile = new File(finalCoverPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //                    创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fos = new FileOutputStream(outFile);
                    is = file.getInputStream();
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
                is.close();
            }
        }

        videoService.updateVideo(videoId,coverPathDB);

        return JSONResult.ok();
    }


            /*分页和搜索查询视频列表
                    isSaveRecord：1-需要保存
                    isSaveRecord：0-不需要保存，或者为空时*/
    @PostMapping(value = "/showAll")
    public JSONResult showAll(@RequestBody Videos video, Integer isSaveRecord,
                              Integer page , Integer pageSize) throws Exception {

        if(page == null) {page = 1;}
        if (pageSize == null) {pageSize = PAGE_SIZE; }
        PagedResult result = videoService.getAllVideos(video, isSaveRecord, page, pageSize);

        return JSONResult.ok(result);
    }

    //我关注的人发视频
    @PostMapping(value = "/showMyFollow")
    public JSONResult showMyFollow(String userId, Integer page, Integer pageSize) throws Exception {

        if (StringUtils.isBlank(userId)) return JSONResult.ok();
        if(page == null) page = 1;

        return JSONResult.ok(videoService.getMyFollowVideos(userId,page,PAGE_SIZE));
    }

    //我关注的人发视频
    @PostMapping(value = "/showMyLike")
    public JSONResult showMyLike(String userId, Integer page, Integer pageSize) throws Exception {

        if (StringUtils.isBlank(userId)) return JSONResult.ok();
        if(page == null) page = 1;

        return JSONResult.ok(videoService.queryMyLikeVideos(userId,page,PAGE_SIZE));
    }


//    热点搜索
    @PostMapping(value = "/hot")
    public JSONResult hot() throws Exception {

        return JSONResult.ok(videoService.getHotWords());
    }



    @PostMapping(value = "/userLike")
    public JSONResult userLike(String userId, String videoId, String videoCreaterId) throws Exception {

        videoService.userLikeVideos(userId,videoId,videoCreaterId);

        return JSONResult.ok();
    }

    @PostMapping(value = "/userUnLike")
    public JSONResult userUnLike(String userId, String videoId, String videoCreaterId) throws Exception {

        videoService.userUnLikeVideos(userId,videoId,videoCreaterId);
        return JSONResult.ok();
    }

    //videoInfo-saveComment
    @PostMapping(value = "/saveComment")
    public JSONResult saveComment(@RequestBody Comments comment,
                                  String fatherCommentId, String toUserId) throws Exception {

        if(fatherCommentId != null) {
            comment.setFatherCommentId(fatherCommentId);
        }
        if(toUserId != null) {
            comment.setToUserId(toUserId);
        }


        videoService.saveComment(comment);

        return JSONResult.ok();
    }


    //videoInfo-getCommetsList
    @PostMapping(value = "/getVideoComments")
    public JSONResult getVideoComments(String videoId, Integer page, Integer pageSize) throws Exception {

        if (StringUtils.isBlank(videoId)) return JSONResult.ok();
        if(page == null) page = 1;
        if (pageSize == null) pageSize = 10;

        PagedResult list = videoService.getAllComments(videoId,page,pageSize);

        return JSONResult.ok(list);
    }

}