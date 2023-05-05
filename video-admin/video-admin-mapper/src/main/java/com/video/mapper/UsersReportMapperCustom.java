package com.video.mapper;

import com.video.pojo.UsersReport;
import com.video.pojo.UsersReportExample;
import com.video.pojo.vo.Reports;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UsersReportMapperCustom {

    List<Reports> selectAllVideoReport();
}