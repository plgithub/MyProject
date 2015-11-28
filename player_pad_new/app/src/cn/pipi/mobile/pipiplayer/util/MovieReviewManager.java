package cn.pipi.mobile.pipiplayer.util;

import java.util.List;
import cn.pipi.mobile.pipiplayer.bean.MovieReviewBean;

/**
 * 影片管理
 * @author qiny
 *
 */
public class MovieReviewManager {

	
	private int pagerCount;
	
	private List<MovieReviewBean> list;
	
	
	private int defaultSize=50; // 从服务器端每页获取到的数据为50个
	
	public MovieReviewManager(){
	}


	public int getPagerCount() {
		return pagerCount;
	}

	public void setPagerCount(int pagerCount) {
		this.pagerCount = pagerCount;
	}

	public List<MovieReviewBean> getList() {
		return list;
	}

	public void setList(List<MovieReviewBean> list) {
		this.list = list;
	}
	
	public boolean hasMoreDate(int currentPager){
		boolean tmp=false;
		if(getPagerCount()==0) return tmp;
		if(currentPager*defaultSize<getPagerCount()){
			tmp=true;
		}
		return tmp;
	}

}
