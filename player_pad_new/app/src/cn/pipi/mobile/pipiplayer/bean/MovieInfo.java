package cn.pipi.mobile.pipiplayer.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MovieInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String movieID;

	private String movieName;

	private String movieImgPath;

	private String movieUpstate;

	private String movieSubTitel;

	private String movieType;
	// 打分
	private String grade;
	// 主演
	private String actor;
	// 导演
	private String director;

	// 描述
	private String desc;

	private String year;

	private String area;

	private boolean isEditState;

	private String currentSourceKey;// 当前来源名字

	private List<SourceBean> currentMovieDetialSources;

	// 影片来源及下载地址集合
	private Map<String, List<DownLoadInfo>> movieDownUrlmap;
	
	private boolean isAd;

	public boolean isAd() {
		return isAd;
	}

	public void setAd(boolean isAd) {
		this.isAd = isAd;
	}

	public String getCurrentSourceKey() {
		return currentSourceKey;
	}

	public void setCurrentSourceKey(String currentSourceKey) {
		this.currentSourceKey = currentSourceKey;
	}

	public List<SourceBean> getCurrentMovieDetialSources() {
		return currentMovieDetialSources;
	}

	public void setCurrentMovieDetialSources(List<SourceBean> currentMovieDetialSources) {
		this.currentMovieDetialSources = currentMovieDetialSources;
	}

	public Map<String, List<DownLoadInfo>> getMovieDownUrlmap() {
		return movieDownUrlmap;
	}

	public void setMovieDownUrlmap(Map<String, List<DownLoadInfo>> movieDownUrlmap) {
		this.movieDownUrlmap = movieDownUrlmap;
	}

	public boolean isEditState() {
		return isEditState;
	}

	public void setEditState(boolean isEditState) {
		this.isEditState = isEditState;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	// showName 大片 展示名字
	private String showName;

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getMovieID() {
		return movieID;
	}

	public void setMovieID(String movieID) {
		this.movieID = movieID;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public String getMovieImgPath() {
		return movieImgPath;
	}

	public void setMovieImgPath(String movieImgPath) {
		if (!TextUtils.isEmpty(movieImgPath) && movieImgPath.startsWith("http")) {
			this.movieImgPath = movieImgPath;
		} else if ( movieImgPath.indexOf("/upload/Image/")!=-1 )	{
//			http://img.pipi.cn/imgupload/clientwww3/201406/27/20140627165509_518.jpg
			this.movieImgPath = "http://img.pipi.cn/imgupload/" + movieImgPath.substring(14);
		}
		else {
			this.movieImgPath = "http://img.pipi.cn/movies/126X168/" + movieImgPath;
		}
//		this.movieImgPath = movieImgPath;
	}

	public String getMovieUpstate() {
		return movieUpstate;
	}

	public void setMovieUpstate(String movieUpstate) {
		this.movieUpstate = movieUpstate;
	}

	public String getMovieSubTitel() {
		return movieSubTitel;
	}

	public void setMovieSubTitel(String movieSubTitel) {
		this.movieSubTitel = movieSubTitel;
	}

	public String getMovieType() {
		return movieType;
	}

	public void setMovieType(String movieType) {
		this.movieType = movieType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
