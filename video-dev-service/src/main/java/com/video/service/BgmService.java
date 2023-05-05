package com.video.service;


import com.video.pojo.Bgm;

import java.util.List;

/**
 * @author 汤垚平
 * @version 1.0
 */
public interface BgmService {

//    查询背景音乐列表
    public List<Bgm> queryBgmList();

    public Bgm queryBgmList(String bgmId);

}
