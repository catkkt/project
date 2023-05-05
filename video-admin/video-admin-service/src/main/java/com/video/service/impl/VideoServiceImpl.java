package com.video.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.video.enums.BGMOperatorTypeEnum;
import com.video.mapper.BgmMapper;
import com.video.mapper.UsersMapper;
import com.video.mapper.UsersReportMapperCustom;
import com.video.mapper.VideosMapper;
import com.video.pojo.*;
import com.video.pojo.UsersExample.Criteria;
import com.video.pojo.vo.Reports;
import com.video.service.UsersService;
import com.video.service.VideoService;
import com.video.utils.JSONResult;
import com.video.utils.JsonUtils;
import com.video.utils.PagedResult;
import com.video.utils.ZKCurator;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	private BgmMapper bgmMapper;

	@Autowired
	private VideosMapper videosMapper;

	@Autowired
	private UsersReportMapperCustom usersReportMapperCustom;

	@Autowired
	private Sid sid;

	@Autowired
	private ZKCurator zkCurator;

	@Override
	public void addBgm(Bgm bgm) {

		String bgmId = sid.nextShort();
		bgm.setId(bgmId);
		bgmMapper.insert(bgm);

		Map<String, String> map = new HashMap<>();
		map.put("operType", BGMOperatorTypeEnum.ADD.type);
		map.put("path", bgm.getPath());

		zkCurator.sendBgmOperator(bgmId, JsonUtils.objectToJson(map));
	}

	@Override
	public PagedResult queryBgmList(Integer page, Integer pageSize) {

		PageHelper.startPage(page, pageSize);

		BgmExample example = new BgmExample();
		List<Bgm> list = bgmMapper.selectByExample(example);

		PageInfo<Bgm> pageList = new PageInfo<>(list);

		PagedResult pagedResult = new PagedResult();
		pagedResult.setTotal(pageList.getPages());
		pagedResult.setPage(page);
		pagedResult.setRows(list);
		pagedResult.setRecords(pageList.getTotal());

		return pagedResult;
	}

	@Override
	public void delBgm(String id) {
		Bgm bgm = bgmMapper.selectByPrimaryKey(id);

		bgmMapper.deleteByPrimaryKey(id);

		Map<String, String> map = new HashMap<>();
		map.put("operType", BGMOperatorTypeEnum.DELETE.type);
		map.put("path", bgm.getPath());

		zkCurator.sendBgmOperator(id, JsonUtils.objectToJson(map));
	}

	@Override
	public PagedResult queryReportList(Integer page, Integer pageSize) {

		PageHelper.startPage(page, pageSize);

		BgmExample bgmExample = new BgmExample();
		List<Bgm> list = bgmMapper.selectByExample(bgmExample);
		PageInfo<Bgm> pageList = new PageInfo<>(list);

		PagedResult pagedResult = new PagedResult();
		pagedResult.setTotal(pageList.getPages());
		pagedResult.setPage(page);
		pagedResult.setRows(list);
		pagedResult.setRecords(pageList.getTotal());

		return pagedResult;
	}

	@Override
	public void updateVideoStatus(String videoId, int videoStatus) {
		Videos video = new Videos();
		video.setId(videoId);
		video.setStatus(videoStatus);

		videosMapper.updateByPrimaryKeySelective(video);
	}
}
