package com.video.service;

import com.video.pojo.Users;
import com.video.pojo.UsersReport;

/**
 * @author 汤垚平
 * @version 1.0
 */
public interface UserService {

//    判断用户名是否存在
    public  boolean queryUsernameIsExist(String username);

//    保存用户(用户注册)
    public void saveUser(Users user);

//    判断登录信息是否正确
    public Users queryUserForLogin(String username,String password);

//    用户信息更新
    public void updateUserInfo(Users user);

//    查询用户信息
    public Users queryUserInfo(String userId);

//    查询登录用户是否喜欢视频
    public Boolean isUsersLikeVideo(String userId, String videoId);

//    增加用户和粉丝的关系
    public void saveUserFanRelation(String userId, String fanId);

//    删除用户和粉丝的关系
    public void deleteUserFanRelation(String userId, String fanId);

//    举报用户
    public void reportUser(UsersReport userReport);

//    查询用户是否关注
    public boolean queryIfFollow(String userId, String fanId);


    //    删除用户
    public void deleteUser(String userId);
}
