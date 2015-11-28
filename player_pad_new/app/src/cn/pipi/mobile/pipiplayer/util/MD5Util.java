package cn.pipi.mobile.pipiplayer.util;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pipi.mobile.pipiplayer.DownCenter;

public class MD5Util {
	/**
	 * 校验网址hashid生成
	 */
	 public static String getMD5HashIDByUrl(String url){
		 if(url.startsWith("http://"))return null;
    	 final int TIMES=5;//循环遍历5次
    	 for(int i=0;i<TIMES;i++){
			   String ppfilmHashID= DownCenter.getExistingInstance().getDownLoadInfoFromresultsInfos(url);
			   if(ppfilmHashID!=null){
				   Pattern p = Pattern.compile("[A-Z2-7_]+");
				   Matcher m = p.matcher(ppfilmHashID);
	               if(m.matches()&&ppfilmHashID.length()==32){//验证成功，跳出循环
	            	   return ppfilmHashID;
	               }
			   }
			   try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		   }
    	 return null;
     }
	 /**
		 * 校验文件名hashidhe合法性
		 */
	 public static boolean getMD5HashIDByName(File pathname,List<String> HashIDList){
		  if(pathname!=null&&pathname.getName()!=null){
				// 不是hash长度  或者 不在下载列表hashid数组内的均被删除
				String filename=pathname.getName().split("\\.")[0];//截取文件名
				 Pattern p = Pattern.compile("[A-Z2-7_]+");
				   Matcher m = p.matcher(filename);
	             if(m.matches()&&filename.length()==32&&HashIDList.contains(filename)){//验证成功，跳出循环
	          	   return false;
	             }
			}
			return true;
     }
	 
	 /**
		 * 校验文件名hashidhe合法性
		 */
	 public static boolean getFromHttpfilm(String url){
			return url.startsWith("http://");
  }
	
	

}
