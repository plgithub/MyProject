package cn.pipi.mobile.pipiplayer.poly;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.pipi.mobile.pipiplayer.hd.BuildConfig;
import android.text.TextUtils;
import android.util.Log;

public class SoHu implements HtmlInterface{
	//http://v.qq.com/cover/z/z53amptli0891ds.html
	 private  final String TAG = "TAG999 sohu";
	 
	 public  List<String> getDownloadInfo(String url,int parseMode) {
		 String vid=HtmlUtils.getHtml(url,"vid=");
		 if(!TextUtils.isEmpty(vid)){
			 Log.i(TAG, "vid="+vid);
			 return parseVideoItem(vid, parseMode);
		 }
	        return null;
	    }
	 private  List<String> parseVideoItem(String vid, int parseMode){
	        List<String> downloadInfo = new ArrayList<String>();
	        String url = "http://hot.vrs.sohu.com/vrs_flash.action?vid=" + vid;
	        try {
	        	JSONObject jsonObject = HtmlUtils.readJsonFromUrl(url);
	            if(jsonObject == null) return downloadInfo;
	            String host=jsonObject.getString("allot");
	            String prot=jsonObject.getString("prot");
	            JSONObject data=jsonObject.getJSONObject("data");
	            JSONArray clipsURL=data.getJSONArray("clipsURL");
	            JSONArray su=data.getJSONArray("su");
	            if(clipsURL==null||su==null)return null;
	            for(int i=0;i<clipsURL.length()&&i<su.length();i++){
	            	StringBuffer textUrl=new StringBuffer("http://");
	            	textUrl.append(host);
	            	textUrl.append("/?prot=");
	            	textUrl.append(prot);
	            	textUrl.append("&file=");
	            	textUrl.append(clipsURL.getString(i));
	            	textUrl.append("&new=");
	            	textUrl.append(su.getString(i));
	            	String html=HtmlUtils.getHtml(textUrl.toString(), null);
	            	if(!TextUtils.isEmpty(html)){
	            		String realUrl=getRealUrl(html,su.getString(i));
	            		Log.i(TAG, "realUrl=="+realUrl);
	            		if(!TextUtils.isEmpty(html))downloadInfo.add(realUrl);
	            	}
	            }
	          
	        } catch (Exception e) {
	            if(BuildConfig.DEBUG)
	                Log.w(TAG, "failed to parse sohu video " + url, e);
	        }
	        return downloadInfo;
	    }

	  private  String getRealUrl(String html,String su) {
		  String[] text=html.split("\\|");
		  if(text.length>4){
			  StringBuffer realUrl=new StringBuffer(text[0]);
			  realUrl.append(su);
			  realUrl.append("?key=");
			  realUrl.append(text[3]);
			  return realUrl.toString();
		  }
	        return null;
	    }

}
