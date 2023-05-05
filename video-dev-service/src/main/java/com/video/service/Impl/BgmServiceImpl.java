package com.video.service.Impl;

import com.video.mapper.BgmMapper;
import com.video.mapper.UsersMapper;
import com.video.org.n3r.idworker.Sid;
import com.video.pojo.Bgm;
import com.video.pojo.Users;
import com.video.service.BgmService;
import com.video.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author 汤垚平
 * @version 1.0
 */
@Service
public class BgmServiceImpl implements BgmService {

    @Autowired(required = false)
    private BgmMapper bgmMapper;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Bgm> queryBgmList() {
        return bgmMapper.selectAll();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Bgm queryBgmList(String bgmId) {
        return bgmMapper.selectByPrimaryKey(bgmId);
    }
}
