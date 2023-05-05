package com.video.mapper;

import com.video.pojo.VO.CommentsVO;
import com.video.pojo.VO.VideosVO;
import com.video.pojo.Videos;
import com.video.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideosMapperCustom extends MyMapper<Videos> {

//    查询所用视频
    public List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc,
                                         @Param("userId") String userId);

//    查询关注的视频
    public List<VideosVO> queryMyFollowVideos(String userId);

//    查询点赞视频
    public List<VideosVO> queryMyLikeVideos(@Param("userId") String userId);

//    对视频喜欢的数量进行累加
    public void addVideoLikeCount(String videoId);

//    对视频喜欢的数量进行累减
    public void reduceVideoLikeCount(String videoId);
}