package cn.pipi.mobile.pipiplayer.bean;

/**
 * 主菜单listview item info
 * @author qiny
 *
 */
public class MainItemInfo {
	
	private int iconID;
	
	private int selectIconID;
	
	private String Name;

	public int getIconID() {
		return iconID;
	}

	public void setIconID(int iconID) {
		this.iconID = iconID;
	}

	public int getSelectIconID() {
		return selectIconID;
	}

	public void setSelectIconID(int selectIconID) {
		this.selectIconID = selectIconID;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

}
