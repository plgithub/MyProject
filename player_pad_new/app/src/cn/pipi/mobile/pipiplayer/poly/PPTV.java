package cn.pipi.mobile.pipiplayer.poly;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import android.text.TextUtils;
import android.util.Log;

public class PPTV implements HtmlInterface{
	//http://v.qq.com/cover/z/z53amptli0891ds.html
	 private  final String TAG = "TAG999 pptv";
	 
	 public  List<String> getDownloadInfo(String url,int parseMode) {
		 String webcfg = HtmlUtils.getHtml(url,"\"id\":");
		 if(TextUtils.isEmpty(webcfg))return null;
		 String[] vid=webcfg.split("idencode");
		 if(vid.length==0)return null;
		 String newUrl="http://web-play.pptv.com/webplay3-151-"+vid[0]+".xml";
	        return getXmlData(newUrl);
	    }
	 
	  private  List<String> getXmlData(String url) {
			// 泛型暂时不写
			boolean isContinue = true;
			HttpURLConnection conn=null;
			InputStream inputStream = null;
			Map<String,String> map=new HashMap<String,String>();
			String host=null,time = null,rid=null;
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
						if (parser.getName().equals("sh")) {
							 host=parser.nextText();
						}else if (parser.getName().equals("id")) {
							 time=parser.nextText();
							 Log.i(TAG, "time="+time);
						}else if (parser.getName().equals("channel")) {
							 rid=parser.getAttributeValue(7);
							 Log.i(TAG, "rid="+rid);
						}else if (parser.getName().equals("sgm")) {
							map.put(parser.getAttributeValue(0), parser.getAttributeValue(5));
						}
						break;
					case XmlPullParser.END_TAG:
						if(parser.getName().equals("dragdata")){
							isContinue=false;
						}
						break;
					}
					eventType = parser.next();
				}
				ArrayList<String> list=new ArrayList<String>();
                 Iterator<String> iter = map.keySet().iterator();
                  while (iter.hasNext()) {
                       String key = iter.next();   //MD5.GetMD5Code(time)
                       String seed="http://"+host+":8082/"+key+"/"+rid+"?key="+time;
                       list.add(seed);
                         Log.i(TAG, seed);
                  }
                  return list;
			} catch (Exception e) {
				// TODO Auto-generated catch block
			//	//Log.i(PipiPlayerConstant.TAG, "解析异常");
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
			return null;
		}
}
