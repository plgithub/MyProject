package cn.pipi.mobile.pipiplayer.bean;

import java.io.Serializable;

public class TypesBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String typesID;
	
	private String typesName;

	public String getTypesID() {
		return typesID;
	}

	public void setTypesID(String typesID) {
		this.typesID = typesID;
	}

	public String getTypesName() {
		return typesName;
	}

	public void setTypesName(String typesName) {
		this.typesName = typesName;
	}

}
