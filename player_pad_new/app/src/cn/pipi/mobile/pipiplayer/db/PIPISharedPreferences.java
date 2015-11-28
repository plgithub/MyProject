package cn.pipi.mobile.pipiplayer.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class PIPISharedPreferences {

	private static PIPISharedPreferences pipiSharedPreferences;

	private SharedPreferences sp;

	private SharedPreferences.Editor editor;

	private final String NAME = "PIPI";

	private final String serverVersionNameKey = "versionName";

	private final String apkCacheFolderKey = "pipiCacheFolder";

	private final String leastPlayUrlKey = "leastPlayKey";

	private final String autoPlayNext = "autoPlayNext";

	private final String playFromHistory = "playFromHistory";

	private final String isAllow3G = "isAllow3G";

	private final String isFirstSet = "isFirstSet";

	public PIPISharedPreferences(Context context) {
		sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	
	public static PIPISharedPreferences getInstance(Context context) {
		synchronized (PIPISharedPreferences.class) {
			if (pipiSharedPreferences == null) {
				pipiSharedPreferences = new PIPISharedPreferences(context);
			}
			return pipiSharedPreferences;
		}
	}

	public PIPISharedPreferences getExtraInstance() {

		return pipiSharedPreferences;
	}

	public void defaultAppConfigSet() {
		if (!isFirstSet()) {
			setAutoPlayNext(true);
			setPlayFromHistory(true);
			setIsArrow3G(false);
			firstSet(true);
		}
	}
	
	public void putHomeData(String json){
		if(json!=null&&json.length()>0)
			editor.putString("home_data", json).commit();
	}
	
	public String getHomeData(){
		return sp.getString("home_data", null);
	}

	public void putServerVersionName(String versionName) {
		String defaultVersionName = "1.0.0";
		if (!TextUtils.isEmpty(versionName)) {
			editor.putString(serverVersionNameKey, versionName);
		} else {
			editor.putString(serverVersionNameKey, defaultVersionName);
		}
		editor.commit();
	}

	public void putApkCacheFolderPath(String path) {
		if (TextUtils.isEmpty(path))
			return;
		editor.putString(apkCacheFolderKey, path);
		editor.commit();
	}

	public String getApkCacheFolderPath() {
		String tmpPath = sp.getString(apkCacheFolderKey, "");
		return tmpPath;
	}

	public void putLeastPlayUrl(String playUrl) {
		if (TextUtils.isEmpty(playUrl))
			return;
		editor.putString(leastPlayUrlKey, playUrl);
		editor.commit();
	}

	public String getLeastPlayUrl() {
		String tmpPlayUrl = sp.getString(leastPlayUrlKey, "");
		return tmpPlayUrl;
	}

	public void setAutoPlayNext(boolean isAuto) {
		editor.putBoolean(autoPlayNext, isAuto);
		editor.commit();
	}

	public boolean getAutoPlayNext() {
		boolean tmp = sp.getBoolean(autoPlayNext, false);
		return tmp;
	}

	public void setPlayFromHistory(boolean isAuto) {
		editor.putBoolean(playFromHistory, isAuto);
		editor.commit();
	}

	public boolean getPlayFromHistory() {
		boolean tmp = sp.getBoolean(playFromHistory, false);
		return tmp;
	}

	public void setIsArrow3G(boolean isAuto) {
		editor.putBoolean(isAllow3G, isAuto);
		editor.commit();
	}

	public boolean isAllow3G() {
		boolean tmp = sp.getBoolean(isAllow3G, false);
		return tmp;
	}

	public void firstSet(boolean isFirst) {
		editor.putBoolean(isFirstSet, isFirst);
		editor.commit();
	}

	public boolean isFirstSet() {
		boolean tmp = sp.getBoolean(isFirstSet, false);
		return tmp;
	}

}
