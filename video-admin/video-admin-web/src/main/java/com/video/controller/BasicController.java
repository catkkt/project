package com.video.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


import java.io.File;

@Controller
public class BasicController {



	//        文件保存的命名空间
	//windows
	public static final String FILE_SPACE = "E:" + File.separator +"workshop_wxxcx"+ File.separator +"bgm-mvc";
	//linux
//		public static final String FILE_SPACE = File.separator +"workshop_wxxcx"+ File.separator +"bgm-mvc";


	//        每页显示条数
	public static final Integer PAGE_SIZE = 10;


	

}
