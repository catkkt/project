package com.video.mapper;

import com.video.pojo.SearchRecords;
import com.video.utils.MyMapper;

import java.util.List;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {

    public List<String> getHotWords();
}