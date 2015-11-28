package cn.pipi.mobile.pipiplayer.poly;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.lang.Thread.State;
import java.net.URL;
import java.net.URLConnection;

import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.DownTask;
import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;


import android.text.TextUtils;
import android.util.Log;
/**
 * 下载模块
 * @author hellogv   
 * 
 */
public class DownloadThread implements Runnable {
	/**
	 * 
	 */
	static private final String TAG="TAG999";
	private String mUrl;
	private String mPath;

	private long mDownloadSize=0;
	private long mTotalSize;
	private boolean mStop;
	private boolean mStarted;
	private boolean mError;
	private DownLoadInfo downInfo;
	private Thread mThread = null;
	public DownloadThread(DownLoadInfo downinfo) {
		this.downInfo=downinfo;
		mStop = false;
		mStarted = false;
		mError=false;
	}

	@Override
	public void run() {
		System.out.println("DownloadThread----run()");
		System.out.println("downInfo.getDown_index()-->"+downInfo.getCurrentDownloadIndex());
		
		if(TextUtils.isEmpty(mUrl)){
			mUrl = HtmlUtils.getRedirectUrl(downInfo.getTaskList().get(downInfo.getCurrentDownloadIndex()));
		}
		if(TextUtils.isEmpty(mPath)){
			mPath=HtmlUtils.urlToFileName(downInfo,mUrl);
		}
		System.out.println("DownloadThread mUrl----->"+mUrl);
		System.out.println("DownloadThread mPath----->"+mPath);
		mDownloadSize=downInfo.getDownloadComputeSize();
		if(mDownloadSize==0){
			// 更新文件总段数 和当前下载段数
			DownCenter.dao.updateDownloadCurrentIndexAndTotalCount(downInfo.getDownAddress(), downInfo);;
			File fis=null;
			try {
				fis = new File(mPath);
				if(fis!=null&&fis.exists()){
					mDownloadSize=fis.length();
				}
				downInfo.setDownloadComputeSize(mDownloadSize);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mTotalSize=downInfo.getDownloadTotalSize();
		if(mDownloadSize==0||mDownloadSize<mTotalSize){//未开始下或者未下完
			download();
		}
	}
	
	/** 启动下载线程 */
	public void start() {
		if (!mStarted) {
			if (mThread == null || mThread.getState() == State.TERMINATED) {
				mThread = new Thread(this);
				mThread.start();
			}
			// 只能启动一次
			mStarted = true;
		}
	}
	 public void resume() {
		 try {
			 mStop = false;
			 if (mThread == null || mThread.getState() == State.TERMINATED) {
					mThread = new Thread(this);
					mThread.start();
			            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	 }
	 public void pause() {//暂停
		 mStop = true;
		 if (mThread != null && mThread.getState() != State.TERMINATED) {
				mThread.interrupt();
			}
	 }
	/** 停止下载线程, deleteFile是否要删除临时文件 */
	public void stop() {
		mStop = true;
		if (mThread != null && mThread.getState() != State.TERMINATED) {
			mThread.interrupt();
		}
	}

	/**
	 * 是否下载异常
	 * @return
	 */
	public boolean isError(){
		return mError;
	}
	
	public long getDownloadedSize() {
		return mDownloadSize;
	}

	/** 是否下载成功 */
	public synchronized boolean isDownloadSuccessed() {
		return (mDownloadSize != 0 && mDownloadSize == mTotalSize);
	}

	private synchronized void download() {
		InputStream is = null;
		URLConnection con=null;
		if (mStop) {
			return;
		}
		try {
			URL url = new URL(mUrl);
			con = url.openConnection();
			con.setRequestProperty("Connection", "Keep-Alive");
			if(mDownloadSize>0&&mDownloadSize<mTotalSize){
				con.setRequestProperty("Range","bytes="+ mDownloadSize + "-"+ mTotalSize);
			}else{
				mTotalSize = con.getContentLength();
				downInfo.setDownloadTotalSize(mTotalSize);
//				DownCenter.dao.updataMovieStoreSize(downInfo);
			}
			is = con.getInputStream();
			int len = 0;
			byte[] bs = new byte[1024];
			if (mStop) {
				downInfo.setDownloadState(DownTask.TASK_PAUSE_DOWNLOAD);
				return;
			}
			RandomAccessFile rasf = new RandomAccessFile(mPath, "rwd");
			while (!mStop //未强制停止
					&& ((len = is.read(bs)) != -1)) {//未全部读取
//				    System.out.println("下载文件 ");
					rasf.seek(mDownloadSize);
					rasf.write(bs, 0, len);
					mDownloadSize += len;
			}
			stop();
		} catch (Exception e) {
			mError=true;
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e){e.printStackTrace();}
			}
		}
	}
}
