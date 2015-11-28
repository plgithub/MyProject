package cn.pipi.mobile.pipiplayer.async;

import java.util.ArrayList;
import java.util.List;

import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.config.MessageMark;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.JsonUtil;
import cn.pipi.mobile.pipiplayer.util.MainClassifyRequestManager;
import cn.pipi.mobile.pipiplayer.util.XMLPullParseUtil;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class ItemClassifyAsync extends AsyncTask<String, Void, List<MovieInfo>> {

	private View loadingView;

	private Context context;

	private Handler handler;

	private boolean isNetworkOkay = false;

	private int messageMark;

	public int getMessageMark() {
		return messageMark;
	}

	public void setMessageMark(int messageMark) {
		this.messageMark = messageMark;
	}

	public ItemClassifyAsync() {

	}

	public View getLoadingView() {
		return loadingView;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setLoadingView(View loadingView) {
		this.loadingView = loadingView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected List<MovieInfo> doInBackground(String... params) {
		List<MovieInfo> list = new ArrayList<MovieInfo>();
		if (isNetworkOkay) {
			list = JsonUtil.getItemClassifyData(params[0]);
		}
		return list;
	}

	@Override
	protected void onPostExecute(List<MovieInfo> result) {
		super.onPostExecute(result);
		if (loadingView != null) {
			loadingView.setVisibility(View.GONE);
		}
		if (isNetworkOkay) {
			if (result != null && result.size() != 0) {
				if (getMessageMark() != 0) {
					CommonUtil.sendMessage(getMessageMark(), handler, result);
				} else {
					CommonUtil.sendMessage(MessageMark.OKAY, handler, result);
				}
			} else {
				CommonUtil.sendMessage(MessageMark.NODATA, handler, null);
			}
		} else {
			CommonUtil
					.sendMessage(MessageMark.NETTYPE_NONETWORK, handler, null);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (CommonUtil.isNetworkConnect(context)) {
			isNetworkOkay = true;
		}
		if (loadingView != null) {
			loadingView.setVisibility(View.VISIBLE);
		}
	}

}
