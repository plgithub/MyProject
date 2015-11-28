package cn.pipi.mobile.pipiplayer;


import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.util.FileUtils;
import android.os.Environment;
import android.util.Log;

public class PlayTask implements Runnable{
	
	private static final String ACTION_UPDATE_LOADBAR = "cn.pipi.mobile.pipiplayer.ui.updateLoadBar";;
	private String urlstr = null;
	private String ppfilmstr = null;  //use downloadinfo function general String
	private DownCenter downCenter;
	private boolean isStopping = false;
	PlayTask(DownLoadInfo downInfo)
	{
			this.urlstr = downInfo.getDownAddress();
	}
	PlayTask(String ppfilmURL)
	{
		this.urlstr = ppfilmURL;
	}
	public void start() 
	{
		try {
			if (downCenter ==null)
				downCenter = DownCenter.getExistingInstance();
			downCenter.pauseLoadingTask(urlstr);//暂停当前正在下载的任务
			if(downCenter.getTaskByUrl(urlstr) != null)
				downCenter.getTaskByUrl(urlstr).start();//设置正在下载状态,交给应用层处理下载数据
			isStopping = false;	
			ppfilmstr = downCenter.GeneralPlayerTask(urlstr, FileUtils.videoCachePath);
			Log.e("video", "urlstr===" + urlstr+"---FileUtils.videoCachePath="+FileUtils.videoCachePath);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		}

	public int getstate() {
		String mediainfo=null;
		try {
			mediainfo = downCenter.GetCurDownloadInfo(urlstr);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		//int outspeed;
		// float outPercent;
		// long  outFileSize;		
		String tmp[]=mediainfo.split("&");
	    String percent=tmp[1];
		       	
		int state =(int) Float.parseFloat(percent);
		return state==100.0 ? 1:0;
	}
	//获取下载速度
	public int getspeed() {
		String mediainfo=null;
		try {
			mediainfo = downCenter.GetCurDownloadInfo(urlstr);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		//int outspeed;
		// float outPercent;
		// long  outFileSize;
		
		String tmp[]=mediainfo.split("&");
        String speed=tmp[0];
        
        return Integer.parseInt(speed);
		
		
	}
	public void stop() 
	{
		try {
			isStopping = true;
			if(!downCenter.isExsitJob(urlstr))//避免播放结束造成下载线程丢失
				downCenter.DeleteDownload(urlstr,false);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	//@Override
	public void run() 
	{
	}
	
	public String getppfilmstr()
	{
		return ppfilmstr;
	}
	public String getppUrl()
	{
		return urlstr;
	}
	
	public boolean isPlaying(){
		return !isStopping;
	}
}

	
	

