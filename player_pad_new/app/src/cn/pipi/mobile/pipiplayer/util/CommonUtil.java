package cn.pipi.mobile.pipiplayer.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pipi.mobile.pipiplayer.MovieDetialActivity;
import cn.pipi.mobile.pipiplayer.bean.SourceBean;
import cn.pipi.mobile.pipiplayer.bean.TypesBean;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.config.MessageMark;

public class CommonUtil {

	private static final int TIME_OUT = 10 * 1000;

	public static boolean isTablet(Context context) {

		return (context.getResources().getConfiguration().screenLayout

		& Configuration.SCREENLAYOUT_SIZE_MASK)

		>= Configuration.SCREENLAYOUT_SIZE_LARGE;

	}

	/**
	 * 检测网络是否正常
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnect(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.v("error", e.toString());
		}
		return false;
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public int getNetworkType(Context context) {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!TextUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = MessageMark.NETTYPE_CMNET;
				} else {
					netType = MessageMark.NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = MessageMark.NETTYPE_WIFI;
		}
		return netType;
	}

	public static boolean isWifi(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkINfo = cm.getActiveNetworkInfo();
		if (networkINfo != null
				&& networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	public static boolean is3rd(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkINfo = cm.getActiveNetworkInfo();
		if (networkINfo != null
				&& networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}

	/**
	 * 获取用户大致位置 如浙江省 杭州市
	 * 
	 * @return
	 */
	public static String getClientAddress() {
		URL infoUrl = null;
		InputStream inStream = null;
		String tmpAddress = "android手机端游客";
		HttpURLConnection httpConnection = null;
		try {
			infoUrl = new URL("http://iframe.ip138.com/ic.asp");
			URLConnection connection = infoUrl.openConnection();
			httpConnection = (HttpURLConnection) connection;
			httpConnection.setConnectTimeout(TIME_OUT);
			httpConnection.setReadTimeout(TIME_OUT);
			httpConnection.connect();
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				inStream = httpConnection.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inStream, "gb2312"));
				StringBuilder strber = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					strber.append(line + "\n");
				}
				inStream.close();
				// System.out.println("net-result----->"+strber);
				// 从反馈的结果中提取出IP地址
				// int start = strber.indexOf("[");
				// int end = strber.indexOf("]", start + 1);
				String content = "<center>";
				boolean isContain = strber.toString().contains(content);
				Log.d(AppConfig.Tag, "获取用户地址--是否获取到用户地址:" + isContain);
				if (isContain) {
					int start = strber.indexOf("来自：");
					int end = strber.indexOf("市");
					tmpAddress = strber.substring(start + 3, end + 1);
				}
				return tmpAddress;
			}
		} catch (MalformedURLException e) {
			Log.d(AppConfig.Tag, "获取用户地址-----------------MalformedURLException");
		} catch (IOException e) {
			Log.d(AppConfig.Tag, "获取用户地址-----------------IOException");
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
				httpConnection = null;
			}
		}
		return tmpAddress;
	}

	/**
	 * 带图片消息提示
	 * 
	 * @param context
	 * @param ImageResourceId
	 * @param text
	 * @param duration
	 */
	public static void ImageToast(Context context, int ImageResourceId,
			CharSequence text, int duration) {
		// 创建一个Toast提示消息
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		// 设置Toast提示消息在屏幕上的位置
		toast.setGravity(Gravity.CENTER, 0, 0);
		// 获取Toast提示消息里原有的View
		View toastView = toast.getView();
		// 创建一个ImageView
		ImageView img = new ImageView(context);
		img.setImageResource(ImageResourceId);
		// 创建一个LineLayout容器
		LinearLayout ll = new LinearLayout(context);
		// 向LinearLayout中添加ImageView和Toast原有的View
		ll.addView(img);
		ll.addView(toastView);
		// 将LineLayout容器设置为toast的View
		toast.setView(ll);
		// 显示消息
		toast.show();
	}

	public static String getDevicesUUID(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String tmDevice, tmSerial, tmPhone, androidId;

		tmDevice = "" + telManager.getDeviceId();

		tmSerial = "" + telManager.getSimSerialNumber();

		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		// UUID deviceUuid = new UUID(androidId.hashCode(),
		// ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32));
		String uniqueId = deviceUuid.toString();

		return uniqueId;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		// System.out.println("scale-->" + scale);
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		// System.out.println("scale-->" + scale);
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 根据像素拉伸 控件宽度
	 */
	public static int scaleWidgetWidth(int defaultWidth) {
		int temp = Math.round(defaultWidth * AppConfig.currentScreenWidth
				/ AppConfig.defaultScreenWidth);
		return temp;
	}

	/**
	 * 根据像素拉伸 控件宽度
	 */
	public static int scaleWidgetHeight(int defaultHeight) {

		int temp = Math.round(defaultHeight * AppConfig.currentScreenHeight
				/ AppConfig.defaultScreenHeight);
		return temp;
	}

	/**
	 * set 转换为list
	 */
	public List<String> setToList(Set<String> set) {
		List<String> list = new ArrayList<String>();
		if (set.size() != 0) {
			list = new ArrayList<String>(set);
		}
		return list;
	}

	/**
	 * 
	 * @param messageMark
	 *            消息标示
	 * @param handler
	 * @param object
	 */
	public static void sendMessage(int messageMark, Handler handler,
			Object object) {
		if (handler == null)
			return;
		Message message = handler.obtainMessage();
		if (object != null)
			message.obj = object;
		message.what = messageMark;
		handler.sendMessage(message);
	}

	/**
	 * 跳转到详情界面
	 */
	public static void toMovieDetialActivity(Context context,
			String... parameters) {
		if (context == null || TextUtils.isEmpty(parameters[0]))
			return;
		int subId = Integer.parseInt(parameters[0]);
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(AppConfig.GET_MOVIEDETIALINFO_URL);

		stringBuffer.append("" + parameters[0] + "_info.js");stringBuffer.append("" + (subId / 1000) + "/");
		// Log.d(AppConfig.Tag, "详情界面请求地址:"+stringBuffer.toString());
		Intent intent = new Intent();
		intent.setClass(context, MovieDetialActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("movieid", parameters[0]);
		// intent.putExtra("moviedetialUrl", stringBuffer.toString());
		if (parameters.length > 1) {
			intent.putExtra("moviedetialtitel", parameters[1]);
		}
		if(parameters.length>2){
			intent.putExtra("from",parameters[2]);
			intent.putExtra("currentPosition",parameters[3]);
			intent.putExtra("sourceTag",parameters[4]);
		}
		context.startActivity(intent);
	}



	/**
	 * 检测当前分类map 是否包含当前key
	 * 
	 * @param key
	 * @return
	 */
	public static boolean isMainClassifyMapContainsKey(String key) {
		boolean tmp = false;
		String itemKey = "cates";
		if (!AppConfig.classifyMap.containsKey(key))
			return tmp;
		Map<String, List<TypesBean>> map = AppConfig.classifyMap.get(key);
		if (map != null && map.containsKey(itemKey)) {
			tmp = true;
			return tmp;
		}
		return tmp;
	}
	
	

	/**
	 * 检测是否有皮皮来源
	 * @return
	 */
	public static boolean isContianPipiSource(List<SourceBean> list) {
		boolean tmp = false;
		if(list==null||list.size()==0) return tmp;
		String pipiSource[]={"默认", "标清", "高清", "超清"};
//		if (AppConfig.currentMovieDetialSources == null
//				|| AppConfig.currentMovieDetialSources.size() == 0)
//			return tmp;
		int count=list.size();
		int pipiSourceLength=pipiSource.length; //4
		for (int i = 0; i < count; i++) {
			SourceBean sourceBean=list.get(i);
			for (int j = 0; j < pipiSourceLength; j++) {
				if(sourceBean.getKey().equals(pipiSource[j])){
					return true;
				}
			}
		}
		return tmp;
	};

	/**
	 * 检测是否是聚合资源
	 * @return true 聚合资源
	 * false  皮皮资源
	 */
	public static boolean isPolySource(String url){
		boolean tmp=false;
		String header="http://";
		if(url.startsWith(header)){
			tmp=true;
		}
		return tmp;
	}
	
	/**
	 * 获取当前时间
	 * @return
	 */
	public static String getCurrentDate() {
		Date currentTime = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String dateStr = formatter.format(currentTime);
		if (formatter != null && currentTime != null) {
			dateStr = formatter.format(currentTime);
		}
		return dateStr;
	}
	
	/**
	 * 是否是合法的手机号码格式
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isTelePhone(String mobiles) {

		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

		Matcher m = p.matcher(mobiles);

		// System.out.println(m.matches() + "---");
		return m.matches();
	}

	/**
	 * 是否合法qq号码
	 */

	public static boolean isAvailableQQNumber(String qqNumber) {

		if (qqNumber == null || qqNumber.equals(""))
			return false;
		String regex = "[1-9][0-9]{4,14}";
		return qqNumber.matches(regex);
	}

	/**
	 * 验证邮箱格式
	 * 
	 * @param email
	 * @return
	 */
	public static boolean emailValidation(String email) {
		boolean flag = false;
		String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		flag = email.matches(regex);
		return flag;
	}
	
	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		// Pattern emailer = Pattern
		// .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		// if (email == null || email.trim().length() == 0)
		// return false;
		// return emailer.matcher(email).matches();

		if (email == null || email.length() == 0) {
			return false;
		}

		Pattern pattern = Pattern
				.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

		Matcher matcher = pattern.matcher(email);

		// System.out.println(matcher.matches());
		return matcher.matches();
	}

	/**
	 * 检测邮箱地址是否合法
	 * 
	 * @param email
	 * @return true合法 false不合法
	 */
	public static boolean isAvailableEmail(String email) {
		if (null == email || "".equals(email))
			return false;
		// Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
		Pattern p = Pattern
				.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
		Matcher m = p.matcher(email);
		return m.matches();
	}
	
}
