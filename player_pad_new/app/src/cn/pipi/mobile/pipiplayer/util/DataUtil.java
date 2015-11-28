package cn.pipi.mobile.pipiplayer.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class DataUtil {
	// 时间转换
	public static String DatetoString() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	// 时间转换
	public static int DatetoInt() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String dateString = formatter.format(currentTime);
		return Integer.parseInt(dateString);
	}

	// 文件创建时间转换
	public static int DatetoInt(File file) {
		Date currentTime = new Date(file.lastModified());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String dateString = formatter.format(currentTime);
		return Integer.parseInt(dateString);
	}

	// 时间转换
	public static String IntoData(int data, String defaultstring) {
		String datastring = String.valueOf(data);
		if (datastring == null || datastring.length() < 8) {
			return defaultstring;
		}
		StringBuffer string = new StringBuffer();
		string.append(datastring.substring(0, 4));
		string.append("-");
		string.append(datastring.substring(4, 6));
		string.append("-");
		string.append(datastring.substring(6, 8));
		datastring = null;
		return string.toString();
	}

	// 生成toast
	public static void getToast(Context context, String string) {
		Toast.makeText(context, string, 1000).show();
	}

	public static String getDevicesUUID(Context context) {

		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, tmPhone, androidId;

		tmDevice = "" + telManager.getDeviceId();

		tmSerial = "" + telManager.getSimSerialNumber();

		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

		String uniqueId = deviceUuid.toString();

		return uniqueId;
	}

	// 检测是否为3G网络
	public static boolean Check3GNet(Context context) {
		try {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			State mobile = manager.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE).getState();
			if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	// 检测是否为wifi网络
	public static boolean CheckWIFINet(Context context) {
		try {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.getState();
			if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

}
