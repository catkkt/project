package com.video.controller;

import com.video.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

	@Autowired
	public RedisOperator redis;

	public static final String USER_REDIS_SESSION = "user-redis-session";

	//        文件保存的命名空间
	public static final String FILE_SPACE = "E:/workshop_wxxcx/video-dev";

	//        bgm保存的命名空间
	public static final String FILE_SPACE_BGM = "E:/workshop_wxxcx";

	// ffmpeg所在目录
	public static final String FFMPEG_EXE = "E:\\后端文件\\ffmpeg-release-essentials\\" +
											"ffmpeg-4.3.1-2021-01-01-essentials_build\\bin\\ffmpeg.exe";

	//        每页显示条数
	public static final Integer PAGE_SIZE = 6;


	

}
