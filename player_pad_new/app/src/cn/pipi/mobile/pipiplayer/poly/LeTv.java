package cn.pipi.mobile.pipiplayer.poly;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

public  class LeTv implements HtmlInterface{
	//http://v.youku.com/v_show/id_XNjU2MTczMjQw.html
	  private  final String TAG = "TAG999 letv";
	 //大于2-->hd2  大于1->mp4  其他 -->flv
	 //http://dynamic.search.app.m.letv.com/android/dynamic.php?ctl=videofile&mmsid=20239476&version=4.1.1
	  public  ArrayList<String> getDownloadInfo(String url,int parseMode) {
		  String vid=HtmlUtils.getHtml(url,"mmsid:");
		  try {
			  return parseVideoItem(vid.replace("mmsid", ""), parseMode);
		} catch (Exception e) {
			// TODO: handle exception
		}
		  return null;
	    }
// 1300 350 1000
	  private  ArrayList<String> parseVideoItem(String vid, int parseMode){
		  ArrayList<String> downloadInfo = new ArrayList<String>();
	        String mainUrl=getMainUrl(vid, parseMode);
	        Log.i(TAG, "mainUrl="+mainUrl);
	        if(!TextUtils.isEmpty(mainUrl)){
	        	String location=getLocation(mainUrl);
	        	if(!TextUtils.isEmpty(location)){
	        		downloadInfo.add(location);
	        	}
	        }
	        return downloadInfo;
	    }
	  
	  private String getMainUrl(String vid, int parseMode){
		  String mainUrl=null;
		  String url = "http://dynamic.search.app.m.letv.com/android/dynamic.php?ctl=videofile&mmsid=" + vid+"&version=4.1.1";
		  try {
	        	JSONObject jsonObject = HtmlUtils.readJsonFromUrl(url);
	            JSONObject infos = jsonObject.getJSONObject("body").getJSONObject("videofile").getJSONObject("infos");
	            String parse="mp4_1300";
	            if(parseMode>=2&&infos.has("mp4_1300")){
	            	parse="mp4_1300";
	            }else if(infos.has("mp4_1000")){
	            	parse="mp4_1000";
	            }else if(infos.has("mp4_350")){
	            	parse="mp4_350";
	            }
	            mainUrl = infos.getJSONObject(parse).getString("mainUrl");
	           
	        } catch (Exception e) {
	        }
	        return mainUrl;
	  }
	  
	  private String getLocation(String mainUrl){
		  String location=null;
	        try {
	        	JSONObject jsonObject = HtmlUtils.readJsonFromUrl(mainUrl);
	        	location = jsonObject.getString("location");
	        } catch (Exception e) {
	        }
	        return location;
	  }
	  
}
