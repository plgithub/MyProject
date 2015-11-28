package cn.pipi.mobile.pipiplayer.poly;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;


import android.util.Log;


public  class YouKu implements HtmlInterface{
	//http://v.youku.com/v_show/id_XNjU2MTczMjQw.html
	  private  final String TAG = "TAG999 youku";
	 //大于2-->hd2  大于1->mp4  其他 -->flv
	 
	  public  ArrayList<String> getDownloadInfo(String url,int parseMode) {
	        Pattern pattern = Pattern.compile(".*/id_([A-Za-z0-9]+)\\.html.*");
	        Matcher matcher = pattern.matcher(url);
	        if (matcher.matches()) {
	        	try {
	        		  return parseYoukuVideoItem(matcher.group(1), parseMode);
				} catch (Exception e) {
					// TODO: handle exception
				}
	        } 
	        return null;
	    }

	  private  ArrayList<String> parseYoukuVideoItem(String vid, int parseMode){
		  ArrayList<String> downloadInfo = new ArrayList<String>();
	        String url = "http://v.youku.com/player/getPlayList/VideoIDS/" + vid;
	        try {
	        	JSONObject jsonObject = HtmlUtils.readJsonFromUrl(url);
	            if(jsonObject == null) return downloadInfo;
	            JSONObject data = jsonObject.getJSONArray("data").getJSONObject(0);
	            Double seed = data.getDouble("seed");
	            JSONObject fileids = data.getJSONObject("streamfileids");

	            String seg = null;
	            String fids = null;
	            if (parseMode >= 2 && fileids.has("hd2")) {
	                seg = "hd2";
	            } else if (parseMode >= 1 && fileids.has("flv")) {
	                seg = "flv";
	            } else if (fileids.has("mp4")) {
	                seg = "mp4";
	            }
	            fids = fileids.getString(seg);
	            String realFileid =getFileID(fids, seed);

	            JSONObject segs = data.getJSONObject("segs");

	            JSONArray vArray = segs.getJSONArray(seg);

	            String vPath = seg.equals("mp4")?"mp4":"flv";
	            for(int i=0;i<vArray.length();i++){
	                JSONObject part = vArray.getJSONObject(i);
	                String k = part.getString("k");
	                String k2 = part.getString("k2");
	                String num= i<16?"0"+Integer.toHexString(i):Integer.toHexString(i);
	                String su = "http://f.youku.com/player/getFlvPath/sid/00_" +
	                		num.toUpperCase()+ "/st/" + vPath + "/fileid/" +
	                        realFileid.substring(0, 8) + num.toUpperCase() +
	                        realFileid.substring(10) + "?K=" + k + ",k2:" + k2;
	                downloadInfo.add(su);
	            }
	        } catch (Exception e) {
	                Log.w(TAG, "failed to parse youku video " + url, e);
	        }
	        return downloadInfo;
	    }

	  private  String getFileIDMixString(double seed) {
	        StringBuilder mixed = new StringBuilder();
	        StringBuilder source = new StringBuilder(
	                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ/\\:._-1234567890");
	        int index, len = source.length();
	        for (int i = 0; i < len; ++i) {
	            seed = (seed * 211 + 30031) % 65536;
	            index = (int) Math.floor(seed / 65536 * source.length());
	            mixed.append(source.charAt(index));
	            source.deleteCharAt(index);
	        }
	        return mixed.toString();
	    }

	  private  String getFileID(String fileid, double seed) {
	        String mixed = getFileIDMixString(seed);
	        String[] ids = fileid.split("\\*");
	        StringBuilder realId = new StringBuilder();
	        int idx;
	        for (int i = 0; i < ids.length; i++) {
	            idx = Integer.parseInt(ids[i]);
	            realId.append(mixed.charAt(idx));
	        }
	        return realId.toString();
	    }

 
}
