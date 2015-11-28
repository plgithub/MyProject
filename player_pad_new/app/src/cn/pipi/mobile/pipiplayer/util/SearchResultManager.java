package cn.pipi.mobile.pipiplayer.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.pipi.mobile.pipiplayer.bean.MovieInfo;

/**
 * 搜索结果管理
 * @author qiny
 *
 */
public class SearchResultManager {
	
	private int totalCount=0;
	
	// 电影 电视剧 
	private Set<String> typeSet;
	
	private List<MovieInfo> list;
	
	public List<MovieInfo> getList() {
		return list;
	}

	public void setList(List<MovieInfo> list) {
		this.list = list;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public Set<String> getTypeSet() {
		return typeSet;
	}

	public void setTypeSet(Set<String> typeSet) {
		this.typeSet = typeSet;
	}

	
}
