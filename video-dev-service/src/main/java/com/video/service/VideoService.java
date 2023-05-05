package com.video.service;


import com.video.pojo.Comments;
import com.video.pojo.Videos;
import com.video.utils.PagedResult;

import java.util.List;

/**
 * @author 汤垚平
 * @version 1.0
 */
public interface VideoService {

//    保存视频到数据库
    public String saveVideo(Videos video);

//    上传封面
    public void updateVideo(String videoId,String coverPath);

//    分页
    public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize);

//    我关注的人的视频列表
    public PagedResult getMyFollowVideos(String userId, Integer page, Integer pageSize);

//    查询我喜欢的视频列表
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);

//        热搜词
    public List<String> getHotWords();

//    用户喜欢的视频列表
    public void userLikeVideos(String userId, String videoId, String videoCreaterId);

//    用户不喜欢的视频列表
    public void userUnLikeVideos(String userId, String videoId, String videoCreaterId);

//    用户留言
    public void saveComment(Comments comment);

//    用户留言分页
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize);
}
