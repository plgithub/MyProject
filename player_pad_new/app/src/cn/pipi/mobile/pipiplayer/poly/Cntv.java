package cn.pipi.mobile.pipiplayer.poly;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.pipi.mobile.pipiplayer.hd.BuildConfig;


import android.util.Log;

public class Cntv implements HtmlInterface{
	//http://v.qq.com/cover/z/z53amptli0891ds.html
	 private  final String TAG = "TAG999 cntv";
	    
	 public  List<String> getDownloadInfo(String url,int parseMode) {
		 String[] vids=url.split("\\/");
		 if(vids.length>0){
			 String vid=vids[vids.length-1];
			 return parseVideoItem(vid, parseMode);
		 }
	        return null;
	    }
	 
	 private  List<String> parseVideoItem(String vid, int parseMode){
	        List<String> downloadInfo = new ArrayList<String>();
	        String url ="http://vdn.apps.cntv.cn/api/getHttpVideoInfo.do?pid=" + vid;
	        try {
	        	JSONObject jsonObject = HtmlUtils.readJsonFromUrl(url);
	            if(jsonObject == null) return downloadInfo;
	            JSONObject data = jsonObject.getJSONObject("video");
	            String seed=null;
	            if(parseMode>=2&&data.has("chapters2")){
	            	seed="chapters2";
	            }else if(parseMode<1&&data.has("lowChapters")){
	            	seed="lowChapters";
	            }else{
	            	seed="chapters";
	            }
	            JSONArray array=data.getJSONArray(seed);
	            for(int i=0;i<array.length();i++){
	            	String u=array.getJSONObject(i).getString("url");
	            	if (BuildConfig.DEBUG) Log.i(TAG, "url= "+u);
	            	downloadInfo.add(u);
	            }
	           
	        } catch (Exception e) {
	            if(BuildConfig.DEBUG)
	                Log.w(TAG, "failed to parse cntv video =" +url , e);
	        }
	        return downloadInfo;
	    }


}
