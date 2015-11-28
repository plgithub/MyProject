package cn.pipi.mobile.pipiplayer.poly;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.pipi.mobile.pipiplayer.hd.BuildConfig;


import android.text.TextUtils;
import android.util.Log;

public class PPS implements HtmlInterface{
	//http://v.qq.com/cover/z/z53amptli0891ds.html
	 private  final String TAG = "TAG999 pps";
	    
	 public  List<String> getDownloadInfo(String url,int parseMode) {
		 Pattern pattern = Pattern.compile(".*/play_([A-Za-z0-9]+)\\.html.*");
	        Matcher matcher = pattern.matcher(url);
	        if (matcher.matches()) {
	            return parseVideoItem(matcher.group(1), parseMode);
	        } else {
	            return null;
	        }
	    }
	 
	 private  List<String> parseVideoItem(String vid, int parseMode){
	        List<String> downloadInfo = new ArrayList<String>();
	        String url = "http://dp.ppstream.com/get_play_url_cdn.php?sid="+vid+"&flash_type=1" ;
	        Log.i(TAG, "url= "+url);
	        try {
	        	String su = HtmlUtils.getHtml(url, null);
	        	if (BuildConfig.DEBUG) Log.i(TAG, "su= "+su);
	           if(!TextUtils.isEmpty(su)&&su.startsWith("http://vurl.pps")){
	                downloadInfo.add(su);
	           }
	        } catch (Exception e) {
	            if(BuildConfig.DEBUG)
	                Log.w(TAG, "failed to parse pps video " + url, e);
	        }
	        return downloadInfo;
	    }


}
