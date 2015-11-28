package cn.pipi.mobile.pipiplayer.async;

import java.util.ArrayList;
import java.util.List;

import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.config.MessageMark;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.MainClassifyRequestManager;
import cn.pipi.mobile.pipiplayer.util.XMLPullParseUtil;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class ClassifyAsync extends AsyncTask<String, Void, List<MovieInfo>> {

	private View loadingView;

	private Context context;

	private Handler handler;

	private MainClassifyRequestManager mainClassifyRequestManager;

	private boolean isNetworkOkay = false;

	private int messageMark = 0;

	public ClassifyAsync(MainClassifyRequestManager mainClassifyRequestManager) {
		this.mainClassifyRequestManager = mainClassifyRequestManager;
	}

	public ClassifyAsync(int messageMark,
			MainClassifyRequestManager mainClassifyRequestManager) {
		this.messageMark = messageMark;
		this.mainClassifyRequestManager = mainClassifyRequestManager;
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
		// Log.d(AppConfig.Tag, "分类请求Url:"+params[0]);
		List<MovieInfo> list = new ArrayList<MovieInfo>();
		if (isNetworkOkay) {
			list = XMLPullParseUtil.getMainClassifyData(params[0],
					mainClassifyRequestManager);
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
				if (messageMark != 0) {
					CommonUtil.sendMessage(messageMark, handler, result);
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
