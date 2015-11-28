package cn.pipi.mobile.pipiplayer.bean;

import java.io.Serializable;

/**
 * 影片详细信息中 影片来源
 * @author qiny
 *
 */
public class SourceBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String key;
	
	public int values;
	
	public int values_press;

	public int getValues_press() {
		return values_press;
	}

	public void setValues_press(int values_press) {
		this.values_press = values_press;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getValues() {
		return values;
	}

	public void setValues(int values) {
		this.values = values;
	}
	
	

}
