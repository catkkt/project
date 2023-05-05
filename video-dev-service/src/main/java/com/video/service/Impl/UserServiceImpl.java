package com.video.service.Impl;

import com.video.mapper.UsersFansMapper;
import com.video.mapper.UsersLikeVideosMapper;
import com.video.mapper.UsersMapper;
import com.video.mapper.UsersReportMapper;
import com.video.org.n3r.idworker.Sid;
import com.video.pojo.Users;
import com.video.pojo.UsersFans;
import com.video.pojo.UsersLikeVideos;
import com.video.pojo.UsersReport;
import com.video.service.UserService;
import org.apache.commons.lang3.StringUtils;
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
public class UserServiceImpl implements UserService {

    @Autowired(required = false)
    private UsersMapper usersMapper;

    @Autowired(required = false)
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired(required = false)
    private UsersFansMapper usersFansMapper;

    @Autowired(required = false)
    private UsersReportMapper usersReportMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {

        Users user = new Users();
        user.setUsername(username);
        Users record = usersMapper.selectOne(user);
        return record == null ? false :true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {

        String userTd = sid.nextShort();
        user.setId(userTd);
        usersMapper.insert(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        Users user = usersMapper.selectOneByExample(userExample);
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserInfo(Users user) {

        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id",user.getId());
        usersMapper.updateByExampleSelective(user,userExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {

        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id",userId);
        Users user = usersMapper.selectOneByExample(userExample);
        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Boolean isUsersLikeVideo(String userId, String videoId) {

        if(StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)) {
            return  false;
        }

        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId", videoId);

        List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);

        if(list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {
        String relId = sid.nextShort();

        UsersFans usersFan = new UsersFans();
        usersFan.setId(relId);
        usersFan.setFanId(fanId);
        usersFan.setUserId(userId);

        usersFansMapper.insert(usersFan);
        usersMapper.addFansCount(userId);
        usersMapper.addFollowersCount(fanId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserFanRelation(String userId, String fanId) {

        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);

        usersFansMapper.deleteByExample(example);

        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFollowerCount(fanId);

    }

    @Override
    public boolean queryIfFollow(String userId, String fanId) {

        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);

        List<UsersFans> list = usersFansMapper.selectByExample(example);

        if(list != null && !list.isEmpty() && list.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteUser(String userId) {

        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("id", userId);

        usersMapper.deleteByExample(example);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void reportUser(UsersReport userReport) {
        String urId = sid.nextShort();
        userReport.setId(urId);
        userReport.setCreateDate(new Date());

        usersReportMapper.insert(userReport);
    }

}
