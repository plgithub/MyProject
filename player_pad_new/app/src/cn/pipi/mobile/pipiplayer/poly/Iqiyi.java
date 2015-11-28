package cn.pipi.mobile.pipiplayer.poly;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


import android.text.TextUtils;
import android.util.Log;

public class Iqiyi implements HtmlInterface{
	//http://v.qq.com/cover/z/z53amptli0891ds.html
	 private  final String TAG = "TAG999 iqiyi";
	   
	 public  List<String> getDownloadInfo(String url,int parseMode) {
		 List<String> downloadInfo = new ArrayList<String>();
		 String vid=HtmlUtils.getHtml(url,"videoid=");
		 Log.i(TAG, "videoid="+vid);
		 if(!TextUtils.isEmpty(vid)){
			 url="http://cache.video.qiyi.com/v/"+vid;
			 List<String> list=getXmlData(url);
			 if(list!=null&&list.size()>0){
				 for(String string:list){
					 if(string.endsWith(".f4v")){
						 long time = 0;
						try {
							time = getTime();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//time^ 2391462251  (time+1921658928) (time ^ -1775748160) +Math.pow(2, 32)
						long x=2391462251l;
						long v=time^ x;//(time+1921658928)
						 url=string.substring(0, string.length()-3)+"hml?v="+v;
						 Log.i(TAG, "url="+url+",time="+time);
						 Log.i(TAG, "html="+HtmlUtils.getHtml(url, null));
						// downloadInfo.add(url);
					 }
				 }
			 }
		 }
	        return downloadInfo;
	    }
	 
	 private  List<String> getXmlData(String url) {
			// 泛型暂时不写
			boolean isContinue = true;
			HttpURLConnection conn=null;
			InputStream inputStream = null;
			List<String> fileList=new ArrayList<String>();
			try {
				conn = (HttpURLConnection) new URL(url).openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);
				conn.setRequestMethod("GET");//以get方式发起请求
				conn.setUseCaches(false);//不进行缓存
				conn.connect();
				if (conn.getResponseCode() != 200) {
					conn.disconnect();
					return null;
				} else {
					inputStream = conn.getInputStream();
				}
				XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
				XmlPullParser parser = pullParserFactory.newPullParser();
				parser.setInput(inputStream, "UTF-8");
				int eventType = parser.getEventType();
				while ((eventType != XmlPullParser.END_DOCUMENT&&isContinue)) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if (parser.getName().equals("file")) {
							isContinue=false;
							fileList.add(parser.nextText());
							break;
						}
						break;
					case XmlPullParser.END_TAG:
						if (parser.getName().equals("fileUrl")) {
							isContinue=false;
							break;
						}
						break;
					}
					eventType = parser.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// 扫尾操作
				try {
					   if(inputStream!=null)
							inputStream.close();
							 inputStream=null;
					   if(conn!=null)
							 conn.disconnect();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			}
			return fileList;
		}
	 
	 private long getTime() throws Exception{
		 JSONObject jsonObject=HtmlUtils.readJsonFromUrl("http://data.video.qiyi.com/t.hml?tn=1");
		 if(jsonObject == null) return new Date().getTime();
		 return Integer.valueOf(jsonObject.getString("t"));
	 }
}
