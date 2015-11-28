package cn.pipi.mobile.pipiplayer.async;

import java.util.ArrayList;
import java.util.List;

import cn.pipi.mobile.pipiplayer.bean.SpecialMovieInfo;
import cn.pipi.mobile.pipiplayer.config.MessageMark;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.JsonUtil;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;

public class SpecialAsync extends AsyncTask<Void, Void, List<SpecialMovieInfo>> {

	private Context context;

	private Handler handler;

	private boolean isNetworkOkay = false;

	private View loadingview;
	
	int mark=0; // 标示 大片  或  专题
	

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

	public SpecialAsync() {

	}

	public SpecialAsync(Context context,int mark) {
		this.context = context;
		this.mark=mark;
	}

	@Override
	protected List<SpecialMovieInfo> doInBackground(Void... params) {
		List<SpecialMovieInfo> list=new ArrayList<SpecialMovieInfo>();
		if(isNetworkOkay){
			switch (mark) {
			case 0:
				list=JsonUtil.getSpecialData();
				break;

			case 1:
				list=JsonUtil.getFireFilmsData();
				break;
			}
			
		}
		return list;
	}

	@Override
	protected void onPostExecute(List<SpecialMovieInfo> result) {
		if(loadingview!=null){
			loadingview.setVisibility(View.GONE);
		}
		if(isNetworkOkay){
			CommonUtil.sendMessage(MessageMark.OKAY, handler, result);
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
		System.out.println("isNetworkOkay--->"+isNetworkOkay);
		if(loadingview!=null){
			loadingview.setVisibility(View.VISIBLE);
		}
		super.onPreExecute();
	}
	
	

}
