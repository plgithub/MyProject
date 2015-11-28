package cn.pipi.mobile.pipiplayer.async;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pipi.mobile.pipiplayer.PipiApplication;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.config.MessageMark;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.JsonUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;

@SuppressLint("NewApi")
public class HomePagerAsync extends AsyncTask<Void, Void, Map<String, List<MovieInfo>>>{
	
	private Context context;
	
	private Handler handler;
	
	private boolean isNetworkOkay=false;
	
	private View loadingview;
	
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public View getLoadingview() {
		return loadingview;
	}

	public void setLoadingview(View loadingview) {
		this.loadingview = loadingview;
	}

	public HomePagerAsync(Context context){
		this.context=context;
	}

	@Override
	protected Map<String, List<MovieInfo>> doInBackground(Void... params) {
		Map<String, List<MovieInfo>> map=new HashMap<String, List<MovieInfo>>();
		if(isNetworkOkay)
			map=	JsonUtil.getHomePagerData();
		return map;
	}

	@Override
	protected void onPostExecute(Map<String, List<MovieInfo>> result) {
		if(loadingview!=null){
			loadingview.setVisibility(View.GONE);
		}
		if(isNetworkOkay){
			if(result!=null&&result.keySet().size()!=0){
				CommonUtil.sendMessage(MessageMark.OKAY, handler, result);
			}else{
				CommonUtil.sendMessage(MessageMark.NODATA, handler, null);
			}
		}else {
			CommonUtil.sendMessage(MessageMark.NETTYPE_NONETWORK, handler, null);
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		if(CommonUtil.isNetworkConnect(context)){;
		isNetworkOkay=true;
		}
		if(loadingview!=null){
			loadingview.setVisibility(View.VISIBLE);
		}
		super.onPreExecute();
	
	}
	
	

}
