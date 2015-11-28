package cn.pipi.mobile.pipiplayer.poly;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


import android.text.TextUtils;
import android.util.Log;

public class QQVideo implements HtmlInterface{
	//http://v.qq.com/cover/z/z53amptli0891ds.html
	 private  final String TAG = "TAG999 QQVideo";
	 
	 public  ArrayList<String> getDownloadInfo(String url,int parseMode) {
		 String vid=HtmlUtils.getHtml(url,"vid:");
		 if(!TextUtils.isEmpty(vid)){
			 StringBuffer buffer=new StringBuffer("http://vv.video.qq.com/geturl?vid=");
			 buffer.append(vid);
			 buffer.append("&otype=xml&platform=1&ran=0%2E9652906153351068");
			 return parseQQVideoItem(buffer.toString());
		 }
	        return null;
	    }
	 
	 private  ArrayList<String> parseQQVideoItem(String url) {
			// 泛型暂时不写
			boolean isContinue = true;
			HttpURLConnection conn=null;
			InputStream inputStream = null;
			ArrayList<String> fileList=new ArrayList<String>();
			try {
				conn = (HttpURLConnection) new URL(url).openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);
				conn.setRequestMethod("GET");//以get方式发起请求
				conn.setUseCaches(false);//不进行缓存
				conn.connect();
				if (conn.getResponseCode() != 200) {
					return null;
				} else {
					inputStream = conn.getInputStream();
				}
				XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
				XmlPullParser parser = pullParserFactory.newPullParser();
				parser.setInput(inputStream, "UTF-8");
				int eventType = parser.getEventType();
				while ((eventType != XmlPullParser.END_DOCUMENT)&&isContinue) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						break;
					case XmlPullParser.START_TAG:
						if (parser.getName().equals("url")) {
							isContinue=false;
							fileList.add(parser.nextText());
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
}
