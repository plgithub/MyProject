package cn.pipi.mobile.pipiplayer.poly;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.util.FileUtils;


import android.text.TextUtils;
import android.util.Log;

public class HtmlUtils {
   
	//***************html数据获取*******************************
	  @SuppressWarnings("resource")
	public static String readStream(InputStream inputStream,String tag)  {
		 String line = null;
		 try {
			 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			 int index=-1;
			 StringBuffer string=new StringBuffer();
			 while ((line = reader.readLine()) != null) {
				// Log.i("TAG999", "line="+line);
				 if(TextUtils.isEmpty(tag)){
					 string.append(line);
				 }else if((index=line.indexOf(tag))!=-1){
						 Pattern p = Pattern.compile("[^A-Za-z0-9]");
			    		 return p.matcher(line.substring(index+tag.length())).replaceAll("");
				 }
			 }
			 return string.toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		 return null;
		 }

	 
	  public static String getHtml(String urlpath,String tag)  {
		  HttpURLConnection conn = null;
		  try {
			  URL url = new URL(urlpath);
				 conn = (HttpURLConnection) url.openConnection();
				 conn.setConnectTimeout(10 * 1000);
				 conn.setRequestMethod("GET");
				 conn.setRequestProperty("Content-Type", "x-application/hessian");
				 conn.setRequestProperty("User-agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
				 conn.connect();
				// Log.i("TAG999", "urlpath="+urlpath);
				 if (conn.getResponseCode() == 200) {
				 InputStream inputStream = conn.getInputStream();
				 return readStream(inputStream,tag);
				 }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(conn!=null)
				   conn.disconnect();
			}
		 return null;
		 }
	//***************json获取*******************************
	  public static  JSONObject readJsonFromUrl(String url) {  
		  try {
			  InputStream is = new URL(url).openStream();  
		        try {  
		          BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));  
		          String jsonText = readAll(rd);  
		          JSONObject json = new JSONObject(jsonText);  
		          return json;  
		        } finally {  
		          is.close();  
		        }  
		} catch (Exception e) {
			// TODO: handle exception
		}
		  return null;
	      }  
	    
	  public static  String readAll(Reader rd) throws IOException {  
	        StringBuilder sb = new StringBuilder();  
	        int cp;  
	        while ((cp = rd.read()) != -1) {  
	          sb.append((char) cp);  
	        }  
	        return sb.toString();  
	      }


	/**
	  	 * 获取重定向后的URL，即真正有效的链接
	  	 */
	      public static String getRedirectUrl(String urlString){
	      	URL url;
	  		try {
	  			url = new URL(urlString);
	  			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	  			urlConnection.setInstanceFollowRedirects(false);
	  			if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_MOVED_PERM)
	  				return urlConnection.getHeaderField("Location");
	  			
	  			if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_MOVED_TEMP)
	  				return urlConnection.getHeaderField("Location");
	  			
	  		} catch (Exception e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	      	return urlString;
	      }
	      
	  	public static String getFileDir(DownLoadInfo downInfo){
			if(downInfo.getDownloadPath()==null){
				//临时创建  天龙八部001乐视235673
		  		StringBuffer local=new StringBuffer(FileUtils.videoCachePath);
//		  		local.append("/");
		  		local.append(downInfo.getDownloadName());
		  		local.append(String.format("%03d", downInfo.getDownloadPosition()));
		  		local.append(downInfo.getDownloadSourceTag());
		  		local.append(downInfo.getDownloadID());
		  		File file =new File(local.toString());
		  		if(!file.exists()||!file.isDirectory()){
		  			file.mkdir();
		  		}
		  		downInfo.setDownloadPath(file.getAbsolutePath());//文件夹存放路径
//		  		DownCenter.dao.updataMovieStoreLocal(downInfo.getDownUrl(), file.getAbsolutePath());
			}
			return downInfo.getDownloadPath();
	      }
	      
	      public static String urlToFileName(DownLoadInfo downInfo,String realUrl)
	      {
	  		StringBuffer local=new StringBuffer(getFileDir(downInfo));
	  		local.append("/");
	  		local.append(downInfo.getDownloadName());
	  		local.append(String.format("%03d", downInfo.getDownloadPosition()));
	  		local.append("_");
	  		local.append(String.format("%02d", downInfo.getCurrentDownloadIndex()));
	  		if(realUrl.contains(".flv")){
	  			local.append(".flv");
	  		}else{
	  			local.append(".mp4");
	  		}
	          return local.toString();
	      }
	  	 
}
