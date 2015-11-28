package cn.pipi.mobile.pipiplayer.async;

import java.util.ArrayList;
import java.util.List;

import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.XMLPullParseUtil;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class AutoSearchAsync extends AsyncTask<String, Void, List<String>>{
	
	private boolean isNetworkOkay = false;
	
	private Context context;
	
	private Handler handler;
	
	int messageMark;
	
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public AutoSearchAsync(Context context,int messageMark) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.messageMark=messageMark;
	}

	@Override
	protected List<String> doInBackground(String... params) {
		List<String> list=new ArrayList<String>();
		if(isNetworkOkay){
			list=XMLPullParseUtil.getEditSearchData(params[0], handler);
		}
		return list;
	}

	@Override
	protected void onPostExecute(List<String> result) {
		super.onPostExecute(result);
		CommonUtil.sendMessage(messageMark, handler, result);
	}

	@Override
	protected void onPreExecute() {
		if (CommonUtil.isNetworkConnect(context)) {
			isNetworkOkay = true;
		}
		super.onPreExecute();
	}
	
	

}
