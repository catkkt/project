package com.video.service;

import com.video.pojo.Bgm;
import com.video.pojo.Users;
import com.video.utils.JSONResult;
import com.video.utils.PagedResult;

/**
 * @author 汤垚平
 * @version 1.0
 */
public interface VideoService {

    //添加音乐
    public void addBgm(Bgm bgm);

    //音乐列表
    public PagedResult queryBgmList(Integer page, Integer pageSize);

    //删除音乐
    public void delBgm(String id);

    //举报列表
    public PagedResult queryReportList(Integer page, Integer pageSize);

    //禁播操作
    public void updateVideoStatus(String videoId, int value);
}
