package cn.pipi.mobile.pipiplayer.async;

import java.util.ArrayList;
import java.util.List;

import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.config.MessageMark;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.XMLPullParseUtil;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class HotSearchAsync extends AsyncTask<String, Void, List<String>> {

	private Context context;
	
    private boolean isNetworkOkay=false;
    
    private Handler handler;
	
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public HotSearchAsync(Context context) {
		this.context=context;
	}

	@Override
	protected List<String> doInBackground(String... params) {
		 List<String> list=new ArrayList<String>();
		if(isNetworkOkay){
			Log.d(AppConfig.Tag, "search--->params[0]:"+params[0]);
			list=XMLPullParseUtil.getHotSearchData(params[0]);
			Log.d(AppConfig.Tag, "list.size():"+list.size());
			for (String string : list) {
				Log.d(AppConfig.Tag, string);
			}
			
		}
		return list;
	}

	@Override
	protected void onPostExecute(List<String> result) {
		if(!isNetworkOkay){
			CommonUtil.sendMessage(MessageMark.NETTYPE_NONETWORK, handler, null);
		}else{
			CommonUtil.sendMessage(MessageMark.GET_HOTSEARCH, handler, result);
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(CommonUtil.isNetworkConnect(context)){;
		isNetworkOkay=true;
		}
	}

	


}
