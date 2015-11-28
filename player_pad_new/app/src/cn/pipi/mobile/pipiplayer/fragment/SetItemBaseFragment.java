package cn.pipi.mobile.pipiplayer.fragment;

import android.app.Activity;
import android.util.Log;
import cn.pipi.mobile.pipiplayer.config.AppConfig;

public class SetItemBaseFragment extends BaseFragment {

	public HideActivityInterfaceCallBack hideActivityInterface;

	public SetItemBaseFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void widgetInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			hideActivityInterface = (HideActivityInterfaceCallBack) activity;
		} catch (ClassCastException e) {
			// TODO: handle exception
			Log.d(AppConfig.Tag, getClass().getName()+"  onAttach ClassCastException");
		}
	}

	public interface HideActivityInterfaceCallBack {
		public void hideActivityView(boolean isHide);
	}

}
