package cn.pipi.mobile.pipiplayer.async;

import android.os.Handler;
import android.os.Message;
import cn.pipi.mobile.pipiplayer.bean.Const;
import cn.pipi.mobile.pipiplayer.config.MessageMark;
import cn.pipi.mobile.pipiplayer.util.JsonUtil;

public class GetAdData extends Thread{
	private Handler mHandler;
	public GetAdData(Handler handler){
		mHandler = handler;
	}
	public void run() {
		Message msg = mHandler.obtainMessage();
		msg.obj = JsonUtil.getAds(Const.SEARCH_AD_URL, Const.SEARCH_AD_KEY);
		if(msg.obj==null) return;
		msg.what = MessageMark.ADD_AD;
		mHandler.sendMessage(msg);
	};
}
