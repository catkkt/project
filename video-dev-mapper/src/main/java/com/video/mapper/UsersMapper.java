package com.video.mapper;

import com.video.pojo.Users;
import com.video.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {

//      增加粉丝数
    public void addFansCount(String userId);

//      增加关注数
    public void addFollowersCount(String userId);

//      减少粉丝数
    public void reduceFansCount(String userId);

//      减少关注数
    public void reduceFollowerCount(String userId);

//    增加收到的喜欢数
    public  void addReceiveLikeCount(String videoCreaterId);

//    减少收到的喜欢数
    public void reduceReceiveLikeCount(String videoCreaterId);
}