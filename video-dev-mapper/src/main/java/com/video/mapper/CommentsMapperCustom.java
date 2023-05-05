package com.video.mapper;

import com.video.pojo.Comments;
import com.video.pojo.VO.CommentsVO;
import com.video.utils.MyMapper;

import java.util.List;

public interface CommentsMapperCustom extends MyMapper<Comments> {

    public List<CommentsVO> queryComments(String videoId);
}