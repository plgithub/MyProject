package cn.pipi.mobile.pipiplayer.poly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.pipi.mobile.pipiplayer.hd.BuildConfig;

import android.text.TextUtils;
import android.util.Log;

public  class W56 implements HtmlInterface{
	//http://v.youku.com/v_show/id_XNjU2MTczMjQw.html
	  private  final String TAG = "TAG999 w56";
	 //大于2-->hd2  大于1->mp4  其他 -->flv
	 
	  public  List<String> getDownloadInfo(String url,int parseMode) {
	        Pattern pattern = Pattern.compile(".*/v_([A-Za-z0-9]+)\\.html.*");
	        Matcher matcher = pattern.matcher(url);
	        if (matcher.matches()) {
	            return parseYoukuVideoItem(matcher.group(1), parseMode);
	        } else {
	            return null;
	        }
	    }

	  private  List<String> parseYoukuVideoItem(String vid, int parseMode){
	        List<String> downloadInfo = new ArrayList<String>();
	        String url ="http://vxml.56.com/json/" + vid+"/?src=site";
	        try {
	        	JSONObject jsonObject = HtmlUtils.readJsonFromUrl(url);
	            if(jsonObject == null) return downloadInfo;
	            String su=null;
	            JSONArray data=jsonObject.getJSONObject("info").getJSONArray("rfiles");
	            Map<String, Integer> seeds=new HashMap<String, Integer>();
	            for(int i=0;i<data.length();i++){
	            seeds.put(data.getJSONObject(i).getString("type"),i);
	            }
	            if(parseMode>=2&&seeds.containsKey("super")){
           	       su=data.getJSONObject(seeds.get("super")).getString("url");
               }else if(parseMode>=1&&seeds.containsKey("clear")){
            	   su=data.getJSONObject(seeds.get("clear")).getString("url");
               }else{
            	   su=data.getJSONObject(seeds.get("normal")).getString("url");
               }
	            if(!TextUtils.isEmpty(su)){
	            	downloadInfo.add(su);
	            }
	        } catch (Exception e) {
	            if(BuildConfig.DEBUG)
	                Log.w(TAG, "failed to parse w56 video =" +url , e);
	        }
	        return downloadInfo;
	    }

	  
}
