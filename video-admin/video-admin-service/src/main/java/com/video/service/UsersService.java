package com.video.service;

import com.video.pojo.Users;
import com.video.utils.PagedResult;

public interface UsersService {

	public PagedResult queryUsers(Users user, Integer page, Integer pageSize);
	
}
