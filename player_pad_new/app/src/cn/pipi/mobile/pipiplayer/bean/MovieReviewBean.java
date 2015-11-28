package cn.pipi.mobile.pipiplayer.bean;

import java.io.Serializable;

/**
 * 
 * @author qiny
 * 影评bean 对象
 *
 */
public class MovieReviewBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int getPagerCount() {
		return pagerCount;
	}

	public void setPagerCount(int pagerCount) {
		this.pagerCount = pagerCount;
	}

	public String getUserNickName() {
		return userNickName;
	}

	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRevDate() {
		return revDate;
	}

	public void setRevDate(String revDate) {
		this.revDate = revDate;
	}

	String userNickName;
	
	String content;
	
	String revDate;
	
	int pagerCount;
	
}
