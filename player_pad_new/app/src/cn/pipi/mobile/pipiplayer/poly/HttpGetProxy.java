package cn.pipi.mobile.pipiplayer.poly;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.Thread.State;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.DownTask;
import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.util.FileUtils;

import android.sax.StartElementListener;
import android.text.TextUtils;
import android.util.Log;

/**
 * 代理服务器类
 * 
 * @author hellogv
 * 
 */
public class HttpGetProxy implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static public String TAG = "HttpGetProxy";
	/** 下载线程 */
	private DownloadThread download = null;
	private DownLoadInfo downInfo = null;
	private long mDownloadSize;// 用于两次比较得出下载速度
	private boolean isStoping = false;

	/**
	 * 初始化代理服务器
	 * 
	 * @param localport
	 *            代理服务器监听的端口
	 */
	public HttpGetProxy(DownLoadInfo downInfo) {
		try {
			this.downInfo = downInfo;
			start();
		} catch (Exception e) {
			// System.exit(0);
		}
	}

	public void start() {// 开始下载
		try {
			timer.schedule(task, 1000, 1000);
			prebuffer();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 开始下载
	public void prebuffer() throws Exception {
		if (download != null)
			download.stop();

		download = new DownloadThread(downInfo);
		download.start();
	}

	// 重启
	public void ResumeDownload() {
		if (download != null)
			download.resume();
		isStoping = false;
	}

	// 暂停
	public void PauseDownload() {
		if (download != null)
			download.pause();
		isStoping = true;
	}

	// 删除任务
	public void DeleteDownload() {
		isStoping = true;
		if (download != null)
			download.stop();
		task.cancel();
		timer.cancel();
	}

	Timer timer = new Timer();
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!isStoping) {
				try {
					downInfo.setDownloadComputeSize(download
							.getDownloadedSize());
					// 数据显示
					if (downInfo.getDownloadComputeSize() > 0
							&& downInfo.getDownloadTotalSize() > 0
							&& mDownloadSize > 0) {
						long speed = downInfo.getDownloadComputeSize()
								- mDownloadSize;
						System.out.println("speed-->" + speed);
						downInfo.setDownloadSpeed((int) (speed > 0 ? speed : 0));
						downInfo.setDownloadProgress((int) (downInfo
								.getDownloadComputeSize() * 100 / downInfo
								.getDownloadTotalSize()));
						 Log.i("TAG999",
						 "name = "+downInfo.getDownloadName()+",progress"+downInfo.getDownloadProgress()+",index"+downInfo.getCurrentDownloadIndex());
						// 检测是否下个
						if (downInfo.getDownloadComputeSize() >= downInfo
								.getDownloadTotalSize()) {
							int index = downInfo.getCurrentDownloadIndex();
							Log.i("DownTask", "下载完成  下一个 =" + (index + 1));
							System.out.println("DownTask 下载完成  下一个 ="
									+ (index + 1));
							// 判断是否下载完毕
							if (index + 1 < downInfo.getTaskList().size()) {
								downInfo.setCurrentDownloadIndex(index + 1);
								downInfo.setDownloadProgress(0);
								downInfo.setDownloadComputeSize(0);
								downInfo.setDownloadTotalSize(0);
								try {
									prebuffer();
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								isStoping = true;
								download.stop();
								downInfo.setDownloadSpeed(0);
								// downInfo.setDown_state(DownTask.TASK_FINISHED);
								downInfo.setDownloadProgress(100);
								// DownCenter.dao
								// .updataMoviePlayProgress(downInfo);
								DownCenter.dao.updateDownloadProgressFromUrl(downInfo.getDownAddress(), 100);
								DeleteDownload();
								System.out.println("全部下载完成   =" + index);
								Log.i("DownTask", "全部下载完成   =" + index);
							}
						}
					}
					mDownloadSize = downInfo.getDownloadComputeSize();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}
	};

}