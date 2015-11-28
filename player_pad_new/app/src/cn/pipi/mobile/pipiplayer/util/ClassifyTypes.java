package cn.pipi.mobile.pipiplayer.util;

import java.util.List;

import cn.pipi.mobile.pipiplayer.bean.TypesBean;

public class ClassifyTypes {
	// 排序
	List<String> sortBeans;
	// 类型
	List<TypesBean> typeBeans;
	// 地区
	List<TypesBean> areaBeans;
	// 年份
	List<TypesBean> yearsBeans;
	// 分类
	List<TypesBean> catesBeans;
	public List<String> getSortBeans() {
		return sortBeans;
	}
	public void setSortBeans(List<String> sortBeans) {
		this.sortBeans = sortBeans;
	}
	public List<TypesBean> getTypeBeans() {
		return typeBeans;
	}
	public void setTypeBeans(List<TypesBean> typeBeans) {
		this.typeBeans = typeBeans;
	}
	public List<TypesBean> getAreaBeans() {
		return areaBeans;
	}
	public void setAreaBeans(List<TypesBean> areaBeans) {
		this.areaBeans = areaBeans;
	}
	public List<TypesBean> getYearsBeans() {
		return yearsBeans;
	}
	public void setYearsBeans(List<TypesBean> yearsBeans) {
		this.yearsBeans = yearsBeans;
	}
	public List<TypesBean> getCatesBeans() {
		return catesBeans;
	}
	public void setCatesBeans(List<TypesBean> catesBeans) {
		this.catesBeans = catesBeans;
	}
	
	

}
