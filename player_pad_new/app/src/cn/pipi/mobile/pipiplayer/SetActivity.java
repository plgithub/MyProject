package cn.pipi.mobile.pipiplayer;

import java.io.File;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Build.VERSION;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.pipi.mobile.pipiplayer.db.PIPISharedPreferences;
import cn.pipi.mobile.pipiplayer.fragment.AboutFragment;
import cn.pipi.mobile.pipiplayer.fragment.SetItemBaseFragment.HideActivityInterfaceCallBack;
import cn.pipi.mobile.pipiplayer.fragment.SuggestFragment;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.local.vlc.Util;
import cn.pipi.mobile.pipiplayer.updata.UpdateManager;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.FileUtils;
import cn.pipi.mobile.pipiplayer.util.SdcardUtil;
import cn.pipi.mobile.pipiplayer.util.ToastUtil;

@SuppressLint("NewApi")
public class SetActivity extends BaseActivity implements
		HideActivityInterfaceCallBack, OnClickListener {

	private FrameLayout frameLayout;

	private View view;

	private LayoutInflater mLayoutInflater;

	private RelativeLayout topRelativeLayout;

	private LinearLayout bottomLinearLayout;

	private Button backButton;

	private Button autoPlayNextBtn;

	private Button playFromHistoryBtn;

	private Button allow3GBtn;

	private PIPISharedPreferences pipiSharedPreferences;
	
	private boolean isDelCacheing=false;
	
	private final int DEL_APP_CACHE=1;
	
	private View set_cache;

	@Override
	public void widgetInit() {
		// TODO Auto-generated method stub
		this.setFinishOnTouchOutside(false);
		topRelativeLayout = (RelativeLayout) this
				.findViewById(R.id.set_toplayout);
		backButton = (Button) this.findViewById(R.id.set_cannelbtn);
		bottomLinearLayout = (LinearLayout) this
				.findViewById(R.id.set_linelayout);
		frameLayout = (FrameLayout) this.findViewById(R.id.set_framelayout);
		autoPlayNextBtn = (Button) this.findViewById(R.id.set_autonext);
		playFromHistoryBtn = (Button) this
				.findViewById(R.id.set_playfromhistory);
		allow3GBtn = (Button) this.findViewById(R.id.set_is3g);
		backButton.setOnClickListener(this);
		autoPlayNextBtn.setOnClickListener(this);
		playFromHistoryBtn.setOnClickListener(this);
		allow3GBtn.setOnClickListener(this);
		pipiSharedPreferences = PIPISharedPreferences.getInstance(this);
		set_cache = this.findViewById(R.id.set_cache);
		set_cache.setOnClickListener(this);
		setButtonBg();
	}

	private void setButtonBg() {
		if (pipiSharedPreferences.getAutoPlayNext()) {
			autoPlayNextBtn.setBackgroundResource(R.drawable.set_switch_on);
		} else {
			autoPlayNextBtn.setBackgroundResource(R.drawable.set_switch_off);
		}
		if (pipiSharedPreferences.getPlayFromHistory()) {
			playFromHistoryBtn.setBackgroundResource(R.drawable.set_switch_on);
		} else {
			playFromHistoryBtn.setBackgroundResource(R.drawable.set_switch_off);
		}
		if (pipiSharedPreferences.isAllow3G()) {
			allow3GBtn.setBackgroundResource(R.drawable.set_switch_on);
		} else {
			allow3GBtn.setBackgroundResource(R.drawable.set_switch_off);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// mLayoutInflater = LayoutInflater.from(this);
		// view = mLayoutInflater.inflate(R.layout.set, null);
		this.setContentView(R.layout.set);
		setActivitySize();
		widgetInit();
	}

	private void setActivitySize() {
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的1.0
		p.width = (int) (d.getWidth() * 0.54); // 宽度设置为屏幕的0.8
		// p.alpha = 1.0f; // 设置本身透明度
		// p.dimAmount = 0.0f; // 设置黑暗度
		getWindow().setAttributes(p);
	}

	public void itemDealOnclick(View view) {
		String tagPostion = (String) view.getTag();
		if (TextUtils.isEmpty(tagPostion)
				|| !TextUtils.isDigitsOnly(tagPostion)) {
			return;
		}
		int postion = Integer.parseInt(tagPostion);
		Fragment fragment = null;
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		switch (postion) {
		case 1:// 意见反馈
			fragment = new SuggestFragment();
			hideActivityView(true);
			fragmentTransaction.replace(R.id.set_framelayout, fragment);
			fragmentTransaction.commit();
			break;
		case 2:// 关于
			fragment = new AboutFragment();
			hideActivityView(true);
			fragmentTransaction.replace(R.id.set_framelayout, fragment);
			fragmentTransaction.commit();
			break;
		case 3:// 给皮皮打分

			break;
		case 4:// 检测新版本
			UpdateManager.getUpdateManager(this).checkAppUpdate(this);
			break;
		case 8: // 清空缓存
			delFileCache();
			break;
		}

	}
	
	@TargetApi(19)
	private void showDialog1() {
		Dialog dialog = null;
		// 获取路径
		// 检索本身选择的第几个
		int which = 0;
		String[] list;
		if (VERSION.SDK_INT >= 19) {
			File[] listFile = this.getExternalCacheDirs();
			list = new String[listFile.length];
			for (int i = 0; i < listFile.length; i++) {
				if (listFile[i] == null || listFile[i].getAbsolutePath() == null) {
					list[i] = null;
					continue;
				}
				list[i] = listFile[i].getAbsolutePath();
			}
		}else {
			list = Util.getStorageDirectories();
		}
		String[] listName = new String[list.length];
		for (int i = 0; i < list.length; i++) {
			if (list[i] == null) {
				continue;
			}
			StringBuffer name = new StringBuffer();
			name.append("存储卡 "+(i+1)+" (剩");
			name.append(SdcardUtil.formatSize(this,
					SdcardUtil.getAvailableStore(list[i])));
			name.append("/共");
			name.append(SdcardUtil.formatSize(this,
					SdcardUtil.getSdCardTotalStore(list[i])));
			name.append(")");
			listName[i] = name.toString();
			if(list[i].equals(pipiSharedPreferences.getInstance(this).getApkCacheFolderPath())){
				which = i;
			}
		}
		final String temp[] = list;
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择存储空间");
		builder.setSingleChoiceItems(listName, which,
				new DialogInterface.OnClickListener() {
					@TargetApi(19)
					@Override
					public void onClick(DialogInterface dialog, final int which) {
						pipiSharedPreferences.getInstance(SetActivity.this).putApkCacheFolderPath(temp[which]);
						if (!FileUtils.makeAppCacheFolder(SetActivity.this)) {
							Toast.makeText(SetActivity.this, "设置失败,自动改为默认存储", Toast.LENGTH_SHORT).show();
						}
						dialog.dismiss();
					}
				});
		builder.setNegativeButton(getString(R.string.cancel), null);
		dialog = builder.create();
		dialog.show();
	}
	
	private void delFileCache(){
		if(isDelCacheing){
			ToastUtil.ToastShort(this, "正在清理中...");
			return ;
		}
		isDelCacheing=true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				FileUtils.delFileCache(SetActivity.this);
				CommonUtil.sendMessage(DEL_APP_CACHE, handler, null);
			}
		}).start();
	}

	private void hideCurrentView(boolean isHide) {
		if (isHide) {
			frameLayout.setVisibility(View.VISIBLE);
			topRelativeLayout.setVisibility(View.GONE);
			bottomLinearLayout.setVisibility(View.GONE);
		} else {
			frameLayout.setVisibility(View.GONE);
			topRelativeLayout.setVisibility(View.VISIBLE);
			bottomLinearLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void hideActivityView(boolean isHide) {
		// TODO Auto-generated method stub
		hideCurrentView(isHide);
	}
	
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message message) {
			
			switch (message.what) {
			case DEL_APP_CACHE:
				isDelCacheing=false;
				ToastUtil.ToastShort(SetActivity.this, "清理完成!");
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.set_cannelbtn:
			finish();
			break;
		case R.id.set_autonext:
			if (pipiSharedPreferences.getAutoPlayNext()) {
				pipiSharedPreferences.setAutoPlayNext(false);
				autoPlayNextBtn.setBackgroundResource(R.drawable.set_switch_off);
			} else {
				pipiSharedPreferences.setAutoPlayNext(true);
				autoPlayNextBtn.setBackgroundResource(R.drawable.set_switch_on);
			}
			break;
		case R.id.set_playfromhistory:
			if (pipiSharedPreferences.getPlayFromHistory()) {
				pipiSharedPreferences.setPlayFromHistory(false);
				playFromHistoryBtn.setBackgroundResource(R.drawable.set_switch_off);
			} else {
				pipiSharedPreferences.setPlayFromHistory(true);
				playFromHistoryBtn.setBackgroundResource(R.drawable.set_switch_on);
			}
			break;
		case R.id.set_is3g:
			if (pipiSharedPreferences.isAllow3G()) {
				pipiSharedPreferences.setIsArrow3G(false);
				allow3GBtn.setBackgroundResource(R.drawable.set_switch_off);
			} else {
				pipiSharedPreferences.setIsArrow3G(true);
				allow3GBtn.setBackgroundResource(R.drawable.set_switch_on);
			}
			break;
		case R.id.set_cache:
			showDialog1();
			break;
		}
	}

}
