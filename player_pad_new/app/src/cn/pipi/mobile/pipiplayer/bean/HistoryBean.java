package cn.pipi.mobile.pipiplayer.bean;

import java.io.Serializable;

/**
 * 播放历史对象
 * @author qiny
 *
 */
public class HistoryBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String movieId;
	
	private String movieName;
	
	// 来源
	private String sourceTag;
	
	private String watchTime;
	
	private long  watchedTime;
	// 观看日期
	private String watchedDate;
	
	private String movieUrl;
	
	private int playPosition;
	
	public int getPlayPosition() {
		return playPosition;
	}
	public void setPlayPosition(int playPosition) {
		this.playPosition = playPosition;
	}
	private boolean isEditDel; //是否编辑删除
	
	public boolean isEditDel() {
		return isEditDel;
	}
	public void setEditDel(boolean isEditDel) {
		this.isEditDel = isEditDel;
	}
	public String getMovieUrl() {
		return movieUrl;
	}
	public void setMovieUrl(String movieUrl) {
		this.movieUrl = movieUrl;
	}
	public String getMovieId() {
		return movieId;
	}
	public void setMovieId(String movieId) {
		this.movieId = movieId;
	}
	public String getMovieName() {
		return movieName;
	}
	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}
	public String getSourceTag() {
		return sourceTag;
	}
	public void setSourceTag(String sourceTag) {
		this.sourceTag = sourceTag;
	}
	public String getWatchTime() {
		return watchTime;
	}
	public void setWatchTime(String watchTime) {
		this.watchTime = watchTime;
	}
	public long getWatchedTime() {
		return watchedTime;
	}
	public void setWatchedTime(long watchedTime) {
		this.watchedTime = watchedTime;
	}
	public String getWatchedDate() {
		return watchedDate;
	}
	public void setWatchedDate(String watchedDate) {
		this.watchedDate = watchedDate;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
