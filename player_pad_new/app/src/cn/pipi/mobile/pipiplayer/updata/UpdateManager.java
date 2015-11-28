package cn.pipi.mobile.pipiplayer.updata;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.FileUtils;
import cn.pipi.mobile.pipiplayer.util.ToastUtil;

/**
 * 应用程序更新工具包
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-6-29
 */
public class UpdateManager {

	private static final int DOWN_NOSDCARD = 0;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;

	private static UpdateManager updateManager;

	private Context mContext;
	// 通知对话框
	private Dialog noticeDialog;
	// 下载对话框
	private Dialog downloadDialog;
	// 进度条
	private ProgressBar mProgress;
	private int progress = 0;
	// 终止标记
	private boolean interceptFlag = false;
	// 提示语
	private String updateMsg = "";
	// apk保存完整路径
	private String apkFilePath = "";

	private String curVersionName = "";
	private final int OUTTIME = 10 * 1000;

	public static String apkUpdataUrl = "";

	public static String minVersionName = "";

	public static String serverVersionName = "";

	private String fileName = "pipiplayerhd";

	private int downLoadSize = 0;

	private int filelength = 0;

	// 更新频率
	private int tmp = 0;

	public static boolean isCanUpdate = false;

	public static boolean isUpdateing = false;

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	public static UpdateManager getUpdateManager(Context context) {
		if (updateManager == null) {
			updateManager = new UpdateManager(context);
		}
		return updateManager;
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				downloadDialog.dismiss();
				break;
			case DOWN_NOSDCARD:
				downloadDialog.dismiss();
				break;
			}
		};
	};

	/**
	 * 检查App更新
	 * 
	 * @param context
	 * @param isShowMsg
	 *            是否显示提示消息
	 */
	public void checkAppUpdate(Context context) {
		this.mContext = context;
		isCanUpdate = isShowUpdataDialog();
		if (isCanUpdate) {
			if(!isUpdateing){
				showNoticeDialog();
			}else{
				ToastUtil.ToastShort(mContext, "正在更新中...");
			}
		}else{
			ToastUtil.ToastShort(mContext, "已是最新版本,无需升级!");
		}
	}

	private boolean isShowUpdataDialog() {
		boolean tmp = false;
		curVersionName = mContext.getString(R.string.pipiplayer_versionname);
		if (TextUtils.isEmpty(minVersionName)
				|| TextUtils.isEmpty(serverVersionName))
			return tmp;
		String mMinver = minVersionName;
		String mNewver = serverVersionName;
		System.out.println("curVersionName:" + curVersionName);
		System.out.println("serverVersionName:" + serverVersionName);
		System.out.println("minVersionName:" + minVersionName);
		if (mMinver.compareTo(curVersionName) > 0) { // 强制升级
			System.out.println("强制升级");
			tmp = true;
		} else if (mNewver.compareTo(curVersionName) > 0) { // 提示升级
			System.out.println("提示升级");
			tmp = true;
		} else {
			tmp = false;
			delAppFile();
		}
		return tmp;

	}

	/**
	 * 显示版本更新通知对话框
	 */
	private void showNoticeDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件版本更新");
		builder.setMessage(updateMsg);
		builder.setPositiveButton("立即更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(!isUpdateing){
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(UpdataService.ACTION);
					// intent.setClass(mContext, UpdataService.class);
					mContext.startService(intent);
				}else{
					ToastUtil.ToastShort(mContext, "正在更新中");
				}
				System.out.println("start service");
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// if (isDialogCanBack) {
				// dialog.dismiss();
				// } else {
				// // 强制升级 退出应用
				// AppManager.getAppManager().AppExit(mContext);
				// }
				dialog.dismiss();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * 显示下载对话框
	 */
	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("正在下载新版本");
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.update_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		downLoadSize = DBHelperDao.getDBHelperDaoInstace(mContext)
				.getAppUpdataDownloadSize(AppConfig.newver);// 赋值
		filelength = DBHelperDao.getDBHelperDaoInstace(mContext)
				.getAppUpdataFileLength(AppConfig.newver);
		if (filelength != 0)
			progress = (int) (((float) downLoadSize / filelength) * 100);
		mProgress.setProgress(progress);
		builder.setView(v);
		builder.setPositiveButton("后台下载", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();

	}

	public void isTips(){
		if(!isCanUpdate){
			ToastUtil.ToastShort(mContext, "无需升级");
			return ;
		}
		showNoticeDialog();
	}

	/**
	 * 删除文件和数据库记录
	 */
	private void delAppFile() {
		System.out.println("updataManager----delAppFile----");
		// del appupdate sql

		DBHelperDao.getDBHelperDaoInstace(mContext).delAppUpdata();
		// del file
		FileUtils.delAppUpdataFile(FileUtils.appUpdatePath);
	}

}
