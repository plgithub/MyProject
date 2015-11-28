package cn.pipi.mobile.pipiplayer.updata;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.RemoteViews;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.util.FileUtils;
import cn.pipi.mobile.pipiplayer.util.SdcardUtil;

public class UpdataService extends IntentService {
	private static final int DOWN_NOSDCARD = 0;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private static final int DOWN_EXCEPTION = 3;
	// 下载文件不存在
	private static final int DOWN_EXCEPTION_NOTFOUND = 4;
	private final int OUTTIME = 10 * 1000;
	private Notification notification;
	private NotificationManager notificationManager;
	// apk保存完整路径
	private String apkFilePath = "";

	private static boolean interceptFlag = false;

	private int progress = 0;

	public static final String ACTION = "cn.pipiplayer.appupdata";

	private String apkName;

	private String fileName = "pipiplayerhd";
	// 渠道名字
	private String channelName = "";

	private int downLoadSize = 0;

	private int filelength = 0;

	// 更新频率
	private int tmp = 0;

	private Context mContext;

	private String realUpdataUrl = "";

	public UpdataService() {
		super("qiny");
		mContext = VLCApplication.getAppContext();
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	/**
	 * 初始化 拼接下载地址 文件长度 已下载进度
	 */
	public void valuesInit() {
		UpdateManager.isUpdateing=true;
		channelName = mContext.getString(R.string.channel_name);
		apkName = fileName + "_" + UpdateManager.serverVersionName + "_" + ".apk";
		StringBuffer stringBuffer = new StringBuffer();
		if (!TextUtils.isEmpty(channelName)
				&& UpdateManager.apkUpdataUrl != null
				&& UpdateManager.apkUpdataUrl.endsWith(".apk")) {
			stringBuffer.append(UpdateManager.apkUpdataUrl);
			stringBuffer.insert(UpdateManager.apkUpdataUrl.length() - 4,
					"_" + channelName);
			realUpdataUrl = stringBuffer.toString();
		}

		if (DBHelperDao.getDBHelperDaoInstace(mContext).isFinishedAppUpdata(
				UpdateManager.serverVersionName)) {
			progress = 100;
		} else {
			downLoadSize = DBHelperDao.getDBHelperDaoInstace(mContext)
					.getAppUpdataDownloadSize(UpdateManager.serverVersionName);// 赋值
			filelength = DBHelperDao.getDBHelperDaoInstace(mContext)
					.getAppUpdataFileLength(UpdateManager.serverVersionName);
			if (filelength != 0)
				progress = (int) (((float) downLoadSize / filelength) * 100);
		}
	}

	private void notificationInit() {
		interceptFlag = false;
		notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		// 点击消息后自动退出
		// notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags = Notification.FLAG_ONGOING_EVENT
				| Notification.FLAG_NO_CLEAR;
		// 自定义的一个布局
		notification.contentView = new RemoteViews(VLCApplication
				.getAppContext().getPackageName(), R.layout.down_notification);
		notification.contentView.setImageViewResource(R.id.down_iv,
				R.drawable.ic_launcher);
		// 设置进度条的最大值和初始值

		if (progress != 100) {
			notification.contentView.setTextViewText(R.id.nf_tVpb, progress
					+ "%");
			notification.contentView.setProgressBar(R.id.nf_pb, 100, progress,
					false);
		} else {
			notification.tickerText = "下载完成";
			notification.contentView.setTextViewText(R.id.nf_tVpb, "100%");
			notification.contentView.setProgressBar(R.id.nf_pb, 100, progress,
					false);
		}
		notification.contentView.setTextViewText(R.id.nf_name, VLCApplication
				.getAppContext().getResources().getText(R.string.app_name));
		Intent notificationIntent = new Intent();
		// notificationIntent.setClass(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(
				VLCApplication.getAppContext(), 0, notificationIntent, 0);
		notification.contentIntent = contentIntent;
		String service = Context.NOTIFICATION_SERVICE;
		notificationManager = (NotificationManager) VLCApplication
				.getAppContext().getSystemService(service);
		// 显示通知
		notificationManager.notify(0, notification);
	}

	private void showErrorNotification() {
		// notification.defaults = Notification.DEFAULT_SOUND;
		// notification.audioStreamType =
		// android.media.AudioManager.ADJUST_LOWER;
		// notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// notification.tickerText = "下载失败";
		// notificationManager.notify(0, notification);
		Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				UpdataService.this, 0, intent,
				notification.flags |= Notification.FLAG_AUTO_CANCEL);
		notification.setLatestEventInfo(UpdataService.this, "皮皮影视更新失败",
				"请检查您的网络状况", pendingIntent);
		notificationManager.notify(0, notification);
	}

	/**
	 * 重新下载apk
	 */
	private void reDownloadUpdataApkFile() {
		// 删除已创建文件
		File ApkFile = new File(apkFilePath);
		if (ApkFile.exists()) {
			ApkFile.delete();
		}
		// 删除数据库记录
		DBHelperDao.getDBHelperDaoInstace(mContext).delAppUpdata(
				UpdateManager.serverVersionName);
		// 修改为原始下载地址 重置 notification
		realUpdataUrl = UpdateManager.apkUpdataUrl;
		progress = 0;
		downLoadSize = 0;
		filelength = 0;
		notification.contentView.setTextViewText(R.id.nf_tVpb, progress + "%");
		notification.contentView.setProgressBar(R.id.nf_pb, 100, progress,
				false);
		notificationManager.notify(0, notification);
		// 开始下载
		new Thread(mdownApkRunnable).start();
	}

	// 更新通知栏进度条
	public void updateProgressbar(int progressCount) {
		// 下载未完成，显示进度条
//		if (progressCount != 100) {
			notification.contentView.setProgressBar(R.id.nf_pb, 100,
					progressCount, false);
			notification.contentView.setTextViewText(R.id.nf_tVpb,
					progressCount + "%");
//		} else {
//			notification.defaults = Notification.DEFAULT_SOUND;
//			notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;
//			notification.flags |= Notification.FLAG_AUTO_CANCEL;
//			notification.tickerText = "下载完成";
//			notification.contentView
//					.setProgressBar(R.id.nf_pb, 100, 100, false);
//			notification.contentView.setTextViewText(R.id.nf_tVpb, "下载完成");
//			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			File apkfile = new File(apkFilePath);
//			if (!apkfile.exists()) {
//				return;
//			}
//			intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),
//					"application/vnd.android.package-archive");
//			PendingIntent contentIntent = PendingIntent.getActivity(
//					VLCApplication.getAppContext(), 0, intent, 0);
//			notification.contentIntent = contentIntent;
//		}
		notificationManager.notify(0, notification);
	}

	private void appUpdataFinish() {
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.tickerText = "皮皮影视HD新版本安装提示";
		File apkfile = new File(apkFilePath);
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, notification.flags |= Notification.FLAG_AUTO_CANCEL);
		// 点击状态栏的图标出现的提示信息设置
		notification.setLatestEventInfo(this, "皮皮影视新版本", "下载完成,点击直接安装",
				pendingIntent);
		notificationManager.notify(0, notification);
		 //跳转安装界面
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		valuesInit();
		notificationInit();
		new Thread(mdownApkRunnable).start();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				updateProgressbar(progress);
				break;
			case DOWN_OVER:
//				updateProgressbar(progress);
				appUpdataFinish();
				stopUpdataSerVice();
				break;
			case DOWN_NOSDCARD:
				stopUpdataSerVice();
				break;
			case DOWN_EXCEPTION:
				showErrorNotification();
				stopUpdataSerVice();
				break;
			case DOWN_EXCEPTION_NOTFOUND:// 404 重新下载
				reDownloadUpdataApkFile();
				break;
			}
		};
	};

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			DownLoadApkFile();
		}
	};

	/**
	 * down appfile method
	 */
	private void DownLoadApkFile() {
		if (SdcardUtil.existSDcard()) {
			apkFilePath = FileUtils.appUpdatePath + apkName;
		} else {
			mHandler.sendEmptyMessage(DOWN_NOSDCARD);
			return;
		}
		URL url = null;
		HttpURLConnection conn = null;
		try {
			int downLoadSize = 0;// 定义下载的本地文件的大小
			File ApkFile = new File(apkFilePath);
			// 是否已下载更新文件
			if (ApkFile.exists()) {
				if (progress == 100) {
					installApk();
					progress = 100;
					mHandler.sendEmptyMessage(DOWN_OVER);
					return;
				}
			} else {
				ApkFile.createNewFile();
			}
			DBHelperDao.getDBHelperDaoInstace(mContext).InsertAppUpdata(
					apkName, UpdateManager.serverVersionName, 0, downLoadSize);
			downLoadSize = DBHelperDao.getDBHelperDaoInstace(mContext)
					.getAppUpdataDownloadSize(UpdateManager.serverVersionName);// 赋值
			System.out.println("已下载  downLoadSize -->" + downLoadSize);
			// url = new URL(PipiPlayerConstant.apkUpdataUrl);
			if (realUpdataUrl != null)
				System.out.println("当前升级请求的地址是:" + realUpdataUrl);
			url = new URL(realUpdataUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Accept-Encoding", "identity");
			conn.setConnectTimeout(OUTTIME);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept-Ranges", "bytes");// 设置断点请求参数
			conn.setRequestProperty("Range", "bytes=" + downLoadSize + "-");
			// conn.setRequestProperty("Connection", "Keep-Alive");
			conn.connect();

			System.out.println("mHttpURLConnection.getResponseCode() "
					+ conn.getResponseCode());
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK
					|| conn.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
				System.out.println(" 得到文件下载流 ");
				int filelength = DBHelperDao.getDBHelperDaoInstace(mContext)
						.getAppUpdataFileLength(UpdateManager.serverVersionName);
				if (filelength == 0) {
					filelength = conn.getContentLength();
					DBHelperDao.getDBHelperDaoInstace(mContext)
							.updataAppFileLenghth(filelength,
									UpdateManager.serverVersionName);
				}

				InputStream instream = conn.getInputStream();
				// conn.disconnect();
				RandomAccessFile rasf = new RandomAccessFile(ApkFile, "rwd");
				// rasf.setLength(filelength);
				byte[] mByte = new byte[1024 * 10];// 每10个字节写入一次
				int length = -1;
				int completeload = (int) downLoadSize;
				rasf.seek(downLoadSize);// 设置文件续传的偏移量
				while ((length = instream.read(mByte)) != -1 && !interceptFlag) {
					rasf.write(mByte, 0, length);// 写入数据
					completeload += length;
//					System.out.println("progress " + progress + "  "
//							+ "completeload  " + completeload + "  "
//							+ " filelength  " + filelength);
					if (completeload < filelength) {
						// 更新进度
						if (progress - tmp >= 2) {
							mHandler.sendEmptyMessage(DOWN_UPDATE);
							DBHelperDao.getDBHelperDaoInstace(mContext)
									.updataApp(completeload, 0,
											UpdateManager.serverVersionName);
							tmp = progress;
						}
						progress = (int) (((float) completeload / filelength) * 100);
					} else { // 下载完毕
						progress = 100;
						DBHelperDao.getDBHelperDaoInstace(mContext).updataApp(
								filelength, 1, UpdateManager.serverVersionName);
						mHandler.sendEmptyMessage(DOWN_OVER);
						System.out.println(" DOWN_OVER ");
						break;
					}
					if (length < 0) {
						break;
					}

				}
			} else if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
				// 拼接地址有误,下载文件不存在时
				mHandler.sendEmptyMessage(DOWN_EXCEPTION_NOTFOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(DOWN_EXCEPTION);
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
			url = null;
		}
	}

	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	private void installApk() {
		File apkfile = new File(apkFilePath);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		VLCApplication.getAppContext().startActivity(i);

		// apkfile.delete();
	}

	public void stopUpdataSerVice() {
		UpdateManager.isUpdateing=false;
		interceptFlag = true;
		// Intent intent = new Intent();
		// intent.setAction(ACTION);
		// VLCApplication.getAppContext().stopService(intent);
		stopSelf();
	}
}
