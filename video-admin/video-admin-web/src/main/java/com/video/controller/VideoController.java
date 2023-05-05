package com.video.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.video.enums.VideoStatusEnum;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.video.pojo.Bgm;
import com.video.service.VideoService;
import com.video.utils.JSONResult;
import com.video.utils.PagedResult;

@Controller
@RequestMapping("video")
public class VideoController extends BasicController {

	@Autowired
	private VideoService videoService;

	@GetMapping("/showReportList")
	public String showReportList() {
		return "video/reportList";
	}

	@PostMapping("/reportList")
	@ResponseBody
	public PagedResult reportList(Integer page) {
		return videoService.queryReportList(page, PAGE_SIZE);
	}

	@PostMapping("/forbidVideo")
	@ResponseBody
	public JSONResult forbidVideo(String videoId) {
		videoService.updateVideoStatus(videoId, VideoStatusEnum.FORBID.value);
		return JSONResult.ok();
	}


	@GetMapping("/showBgmList")
	public String showBgmList() {
		return "video/bgmList";
	}

	@PostMapping("/queryBgmList")
	@ResponseBody
	public PagedResult queryBgmList(Integer page) {
		return videoService.queryBgmList(page, PAGE_SIZE);
	}

	@GetMapping("/showAddBgm")
	public String login() {
		return "video/addBgm";
	}

	@PostMapping("/addBgm")
	@ResponseBody
	public JSONResult addBgm(Bgm bgm) {

		videoService.addBgm(bgm);
		return JSONResult.ok();
	}

	@PostMapping("/delBgm")
	@ResponseBody
	public JSONResult delBgm(String bgmId) {
		videoService.delBgm(bgmId);
		return JSONResult.ok();
	}

	@PostMapping("/bgmUpload")
	@ResponseBody
	public JSONResult bgmUpload(@RequestParam("file") MultipartFile[] files) throws Exception {


		// 保存到数据库中的相对路径
		String uploadPathDB = File.separator + "bgm";

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			if (files != null && files.length > 0) {

				String fileName = files[0].getOriginalFilename();
				if (StringUtils.isNotBlank(fileName)) {
					// 文件上传的最终保存路径
					String finalPath = FILE_SPACE + uploadPathDB + File.separator + fileName;
					// 设置数据库保存的路径
					uploadPathDB += (File.separator + fileName);

					File outFile = new File(finalPath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						// 创建父文件夹
						outFile.getParentFile().mkdirs();
					}

					fileOutputStream = new FileOutputStream(outFile);
					inputStream = files[0].getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}

			} else {
				return JSONResult.errorMsg("上传出错...");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return JSONResult.errorMsg("上传出错...");
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}

		return JSONResult.ok(uploadPathDB);
	}

}
