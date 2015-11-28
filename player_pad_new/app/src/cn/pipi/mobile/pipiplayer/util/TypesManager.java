package cn.pipi.mobile.pipiplayer.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 主分类  管理
 * @author qiny
 *
 */
public class TypesManager {
	
	public static final String mainTypes[]={"电影","电视剧","动漫","综艺"};
	
	public static final String tags[] = { "type", "area", "year", "cates" };
	
	Map<String, ClassifyTypes>  tpyesMap;
	
	public static TypesManager typesManager;
	
	
    public TypesManager(){
    	tpyesMap=new HashMap<String, ClassifyTypes>();
    }
    
    public static TypesManager getInstance(){
    	synchronized (TypesManager.class) {
    		if(typesManager==null){
    			typesManager=new TypesManager();
    		}
    		return typesManager;
		}
    }

}
