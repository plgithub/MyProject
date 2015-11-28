package cn.pipi.mobile.pipiplayer;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.Toast;
import cn.pipi.mobile.pipiplayer.util.AppManager;

public abstract class BaseActivity extends FragmentActivity{

	// exitTime
	private long exitTime = 0;
		
	public boolean isPenentBack;
	public static final int START_DOWNLOAD_ACTIVITY = 11; 
	public static final String MOVIE_ID = "movieId";
	public static final String MOVIE_POSITION = "moviePosition";
		
	public boolean isPenentBack() {
		return isPenentBack;
	}
	
	public void showToast(String hint) {
		Toast.makeText(this, hint, Toast.LENGTH_SHORT);
	}

	/**
	 * 是否调用 再一次退出程序
	 * @param isPenentBack
	 */
	public void setPenentBack(boolean isPenentBack) {
		this.isPenentBack = isPenentBack;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
	}
	
	/**
	 * 界面控件初始化
	 * isPenentBack 是否
	 */
	public abstract void widgetInit();
	
	/**
	 * 跳转到目标类
	 */
	public void redirectTo(boolean isFinish,Class<? extends Object> toClass) {
		Intent intent = new Intent(this, toClass);
		startActivity(intent);
		if(isFinish)
		finish();
	}
	
	public void redirectTo(Class<? extends Object> toClass, String movieId, int moviePosition){
		Intent intent = new Intent(this, toClass);
		intent.putExtra(MOVIE_ID, movieId);
		intent.putExtra(MOVIE_POSITION, moviePosition);
//		System.out.println("当前播放的影片信息: "+ movieId +", position: "+ moviePosition);
		startActivityForResult(intent, START_DOWNLOAD_ACTIVITY);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == START_DOWNLOAD_ACTIVITY) {
			if (data != null) {
				if(data.getBooleanExtra(START_DOWNLOAD_ACTIVITY+"", false))
					finish();
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			
			if(!isPenentBack){
				return super.onKeyDown(keyCode, event);
			}
			
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				MobclickAgent.onKillProcess( this );
				AppManager.getAppManager().AppExit(this);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}
}
