package cn.pipi.mobile.pipiplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import cn.pipi.mobile.pipiplayer.db.PIPISharedPreferences;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVlcException;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.DataUtil;
import cn.pipi.mobile.pipiplayer.util.ToastUtil;

public class NetWorkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			// ConnectivityManager connectivityManager = (ConnectivityManager)
			// context.getSystemService(Context.CONNECTIVITY_SERVICE);
			// State state =
			// connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			// if(state == State.CONNECTED||state == State.CONNECTING) {
			// DataUtil.getToast("切换为3G网络");
			// if(!PipiPlayerConstant.getInstance().allowdown){
			// try {
			// DownCenter.getInstance().pauseAllTaskByError();
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
			// return ;
			// }

			// NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			// if(info!=null&&info.isConnected()){
			// DataUtil.getToast("切换为WIFI网络");
			// try {
			// DownCenter.getInstance().ResumeAllTask();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }else{
			// DataUtil.getToast("没有可用网络");
			// try {
			// DownCenter.getInstance().pauseAllTaskByError();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// }
			if (context == null)
				return;
			// 是否允许2/3g 下载
			boolean isAllow3G = PIPISharedPreferences.getInstance(context)
					.isAllow3G();
			if (!CommonUtil.isNetworkConnect(context)) {
				// 无网络
				// ToastUtil.ToastShort(context, "无网络");
				try {
					DownCenter.getInstance(context).pauseAllTaskByError();
				} catch (LibVlcException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}

			if (!isAllow3G && CommonUtil.is3rd(context)) {
				// 3G状态下
				// ToastUtil.ToastShort(context, "2/3G网络");
				try {
					DownCenter.getInstance(context).pauseAllTaskByError();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}

			if (CommonUtil.isWifi(context)) {
				// wifi 状态下
				// ToastUtil.ToastShort(context, "wifi网络");
				try {
					DownCenter.getInstance(context).ResumeAllTask();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}

		}
	}

}
