package com.video.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.video.mapper.*;
import com.video.org.n3r.idworker.Sid;
import com.video.pojo.Comments;
import com.video.pojo.SearchRecords;
import com.video.pojo.UsersLikeVideos;
import com.video.pojo.VO.CommentsVO;
import com.video.pojo.VO.VideosVO;
import com.video.pojo.Videos;
import com.video.service.VideoService;
import com.video.utils.PagedResult;
import com.video.utils.TimeAgoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author 汤垚平
 * @version 1.0
 */
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired(required = false)
    private VideosMapper videosMapper;

    @Autowired(required = false)
    private UsersMapper usersMapper;

    @Autowired(required = false)
    private CommentsMapper commentsMapper;

    @Autowired(required = false)
    private CommentsMapperCustom commentsMapperCustom;

    @Autowired(required = false)
    private VideosMapperCustom videosMapperCustom;

    @Autowired(required = false)
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired(required = false)
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private Sid sid;


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveVideo(Videos video) {

        String id = sid.nextShort();
        video.setId(id);
        videosMapper.insertSelective(video);

        return id;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideo(String videoId, String coverPath) {
        Videos video = new Videos();
        video.setId(videoId);
        video.setCoverPath(coverPath);
        videosMapper.updateByPrimaryKeySelective(video);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize) {

//        保存热搜词
        String desc = video.getVideoDesc();
        String userId = video.getUserId();
        if(isSaveRecord != null && isSaveRecord == 1) {
            SearchRecords record = new SearchRecords();
            String id = sid.nextShort();
            record.setId(id);
            record.setContent(desc);
            searchRecordsMapper.insert(record);
        }

        PageHelper.startPage(page,pageSize);
        List<VideosVO> list = videosMapperCustom.queryAllVideos(desc,userId);
        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);//当前页
        pagedResult.setTotal(pageList.getPages());//总页数
        pagedResult.setRows(list);//每行显示的内容
        pagedResult.setRecords(pageList.getTotal());//总记录数

        return pagedResult;

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getMyFollowVideos(String userId, Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyFollowVideos(userId);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);//当前页
        pagedResult.setTotal(pageList.getPages());//总页数
        pagedResult.setRows(list);//每行显示的内容
        pagedResult.setRecords(pageList.getTotal());//总记录数

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyLikeVideos(userId);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);//当前页
        pagedResult.setTotal(pageList.getPages());//总页数
        pagedResult.setRows(list);//每行显示的内容
        pagedResult.setRecords(pageList.getTotal());//总记录数

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotWords();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideos(String userId, String videoId, String videoCreaterId) {
//        1. 保存用户和视频的喜欢点赞关联关系表
        String likeId = sid.nextShort();
        UsersLikeVideos ulv = new UsersLikeVideos();
        ulv.setId(likeId);
        ulv.setUserId(userId);
        ulv.setVideoId(videoId);
        usersLikeVideosMapper.insert(ulv);

//         2. 视频喜欢数量累加
        videosMapperCustom.addVideoLikeCount(videoId);

//        3. 用户受喜欢数量的累加
        usersMapper.addReceiveLikeCount(videoCreaterId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userUnLikeVideos(String userId, String videoId, String videoCreaterId) {
//        1. 删除用户和视频的喜欢点赞关联关系表
        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);
        usersLikeVideosMapper.deleteByExample(example);

//         2. 视频喜欢数量累减
        videosMapperCustom.reduceVideoLikeCount(videoId);

//        3. 用户受喜欢数量的累减
        usersMapper.reduceReceiveLikeCount(videoCreaterId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComment(Comments comment) {

        String id = sid.nextShort();
        comment.setId(id);
        comment.setCreateTime(new Date());
        commentsMapper.insert(comment);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);
        List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);

        for (CommentsVO c : list) {
            String timeAgo = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(timeAgo);
        }

        PageInfo<Object> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);//当前页
        pagedResult.setTotal(pageList.getPages());//总页数
        pagedResult.setRows(list);//每行显示的内容
        pagedResult.setRecords(pageList.getTotal());//总记录数

        return pagedResult;
    }
}
