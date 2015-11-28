package cn.pipi.mobile.pipiplayer.util;

import java.net.URLEncoder;

import android.text.TextUtils;
import android.util.Log;
import cn.pipi.mobile.pipiplayer.config.AppConfig;

/**
 * 主分类请求 url 管理
 * 
 * @author qiny
 * 
 */
public class MainClassifyRequestManager {

	// 每页加载个数
	private final int defaultPagerNum = 20;
	// 电影 电视剧 动漫
	private String tagName;
	// 排序
	private String order;
	// 类型
	private String type;
	// 地区
	private String area;
	// 年代
	private String year;
	// 当前页数
	private int currentPager;

	// 当前电影总数
	private int totalMovieSum;

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getCurrentPager() {
		return currentPager;
	}

	public void setCurrentPager(int currentPager) {
		this.currentPager = currentPager;
	}

	public int getTotalMovieSum() {
		return totalMovieSum;
	}

	public void setTotalMovieSum(int totalMovieSum) {
		this.totalMovieSum = totalMovieSum;
	}

	public int getDefaultPagerNum() {
		return defaultPagerNum;
	}

	public MainClassifyRequestManager() {
		// 默认最新排序
		setOrder("1");
	}

	public String requestUrl(int startPager) {
		StringBuffer stringBuffer = new StringBuffer();
		String mtp = "";
		String sb="";
		String ar="";
		String stp="";
		String ft="";
		
		if (TextUtils.isEmpty(getTagName()))
			return "";
		try {
			mtp = URLEncoder.encode(getTagName(), "UTF-8");
			if (!TextUtils.isEmpty(getOrder()))
			sb = URLEncoder.encode( getOrder(), "UTF-8");
			if (!TextUtils.isEmpty(getArea()))
			ar = URLEncoder.encode(getArea(), "UTF-8");
			if (!TextUtils.isEmpty(getType()))
			stp = URLEncoder.encode(getType(), "UTF-8");
			if (!TextUtils.isEmpty(getYear()))
			ft = URLEncoder.encode(getYear(), "UTF-8");
		} catch (Exception e) {
			Log.d(AppConfig.Tag, "URLEncoder.encode,Exception");
		}
		stringBuffer.append(AppConfig.SEARCH_REQUEST_URL);
		stringBuffer.append("st=1&cl=4&");
		stringBuffer.append("ie=UTF-8&");
		stringBuffer.append("&tp=" + mtp);
		if (!TextUtils.isEmpty(getOrder())) {
			stringBuffer.append("&sb=" + sb);
		}
		if (!TextUtils.isEmpty(getArea())) {
			stringBuffer.append("&ar==" +ar);
		}
		if (!TextUtils.isEmpty(getType())) {
			stringBuffer.append("&stp=" + stp);
		}
		if (!TextUtils.isEmpty(getYear())) {
			stringBuffer.append("&ft=" + ft);
		}
		if (startPager > 1) {
			// np 开始记录位置
			setCurrentPager(startPager);
			stringBuffer.append("&np=" + startPager);
		}
		stringBuffer.append("&ps=" + defaultPagerNum);
		Log.d(AppConfig.Tag,
				getTagName() + ":请求url为-->" + stringBuffer.toString());
		return stringBuffer.toString();
	}

	/**
	 * 重置请求条件
	 */
	public void resetRequestCondition() {
		// 默认最热
		setOrder("1");
		setType("");
		setArea("");
		setYear("");

	}

}
