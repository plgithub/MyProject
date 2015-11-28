package cn.pipi.mobile.pipiplayer.fragment;

import com.umeng.analytics.MobclickAgent;

import cn.pipi.mobile.pipiplayer.config.AppConfig;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {

	abstract public void widgetInit();

	Context mContext;

	final String mPageName = "BaseFragment";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(AppConfig.Tag, getClass().getName() + "-----------------onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		Log.d(AppConfig.Tag, getClass().getName() + "-----------------onAttach");
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(AppConfig.Tag, getClass().getName() + "-----------------onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(AppConfig.Tag, getClass().getName() + "-----------------onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy() {
		Log.d(AppConfig.Tag, getClass().getName() + "-----------------onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		Log.d(AppConfig.Tag, getClass().getName() + "-----------------onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		Log.d(AppConfig.Tag, getClass().getName() + "-----------------onDetach");
		super.onDetach();
	}

	@Override
	public void onPause() {
		Log.d(AppConfig.Tag, getClass().getName() + "-----------------onPause");
		super.onPause();
		if (getActivity() != null) {
			MobclickAgent.onPageEnd(mPageName);
		}
	}

	@Override
	public void onResume() {
		Log.d(AppConfig.Tag, getClass().getName() + "-----------------onResume");
		super.onResume();
		if (getActivity() != null) {
			MobclickAgent.onPageStart(mPageName);
		}
	}

	@Override
	public void onStart() {
		Log.d(AppConfig.Tag, getClass().getName() + "-----------------onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.d(AppConfig.Tag, getClass().getName() + "-----------------onStop");
		super.onStop();
	}

	@Override
	public String toString() {
		Log.d(AppConfig.Tag, "currentFragment:" + getClass().getName());
		return super.toString();
	}

}
