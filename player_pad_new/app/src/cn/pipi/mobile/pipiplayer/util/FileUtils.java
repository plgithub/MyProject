package cn.pipi.mobile.pipiplayer.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.DownTask;
import cn.pipi.mobile.pipiplayer.db.PIPISharedPreferences;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVLC;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class FileUtils {

	// 视频缓存路径
	public static String videoCachePath;

	// 图片缓存路径
	public static String imgCachePath = "";

	public static String confDirPath = "";

	public static String appUpdatePath;

	private static final String apkFolderName = "pipiplayerHD";

	private final static int AUTO_DELIMG_DAY = 7;// 图片过期时间 暂时设为7天

	/**
	 * 创建程序缓存目录
	 * 
	 * @param context
	 */
	public static boolean makeAppCacheFolder(Context context) {
		try {
			boolean isExistSdCard = SdcardUtil.existSDcard();

			boolean isExtraSdCard = SdcardUtil.checkExternalSDExists();

			String cachePath = PIPISharedPreferences.getInstance(context)
					.getApkCacheFolderPath();
			String sdCardPath = "";
			if (isExistSdCard || isExtraSdCard) {
				if (!TextUtils.isEmpty(cachePath)) {
					sdCardPath = cachePath;
				} else {
					sdCardPath = Environment.getExternalStorageDirectory()
							.getAbsolutePath();
					PIPISharedPreferences.getInstance(context)
							.putApkCacheFolderPath(sdCardPath);
				}
				StringBuffer superPath = new StringBuffer();
				superPath.append(sdCardPath);
				superPath.append(File.separator);
				superPath.append(apkFolderName);
				// .....................................
				makeApkFolder(superPath.toString());

			}
		} catch (Exception e) {
			return false;
		}
		return true;

	}

	private static void makeApkFolder(String path) {
		if (TextUtils.isEmpty(path))
			return;
		System.out.println("apk缓存目录:" + path);
		File file = new File(path);
		if (!file.exists()) {
			System.out.println("make " + path);
			file.mkdir();
		}
		String videoCacheName = "Caches";
		String imgCacheName = "CacheImageDir";
		String configCacheName = "ConfDir";
		String xmlCacheName = "CacheXMLDir";
		String updataCacheName = "updata";
		File videoCacheFile = new File(file, videoCacheName);
		File imgCacheFile = new File(file, imgCacheName);
		File configCacheFile = new File(file, configCacheName);
		File xmlCacheFile = new File(file, xmlCacheName);
		File updataCacheFile = new File(file, updataCacheName);
		if (!videoCacheFile.exists()) {
			videoCacheFile.mkdir();
		}
		if (!imgCacheFile.exists()) {
			imgCacheFile.mkdir();
		}
		if (!configCacheFile.exists()) {
			configCacheFile.mkdir();
		}
		if (!xmlCacheFile.exists()) {
			xmlCacheFile.mkdir();
		}
		if (!updataCacheFile.exists()) {
			updataCacheFile.mkdir();
		}
		videoCachePath = videoCacheFile.getAbsolutePath() + File.separator;
		imgCachePath = imgCacheFile.getAbsolutePath();
		confDirPath = configCacheFile.getAbsolutePath();
		appUpdatePath = updataCacheFile.getAbsolutePath() + File.separator;
	}

	/**
	 * 根据文件绝对路径获取文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		if (TextUtils.isEmpty(filePath))
			return "";
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}

	/**
	 * 删除过期图片
	 */
	private static void deleteUselessFile() {// 删除过期文件
		if (imgCachePath == null)
			return;
		File file = new File(imgCachePath);
		if (!file.exists())
			return;
		// 获取系统时间
		File[] childFiles = file.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return (DataUtil.DatetoInt() - DataUtil.DatetoInt(pathname)) >= AUTO_DELIMG_DAY;
			}
		});
		if (childFiles != null && childFiles.length != 0)
			for (File file1 : childFiles) {
				file1.delete();
			}
	}

	/**
	 * 删除目录(包括：目录里的所有文件)
	 * 
	 * @param fileName
	 * @return
	 */
	public static void deleteDirectory(String fileName) {
		// boolean status;
		SecurityManager checker = new SecurityManager();

		if (!TextUtils.isEmpty(fileName)) {

			// File path = Environment.getExternalStorageDirectory();
			File newPath = new File(fileName);
			System.out.println("newPath-->" + newPath.getAbsolutePath());
			if (!newPath.exists() || !newPath.isDirectory()) {
				return;
			}
			File childFile[] = newPath.listFiles();
			if (childFile != null) {
				for (File file2 : childFile) {
					System.out.println("子文件:" + file2.getAbsolutePath());
					file2.delete();
				}
			}
			newPath.delete();
			//
			// checker.checkDelete(newPath.toString());
			// if (newPath.isDirectory()) {
			// String[] listfile = newPath.list();
			// // delete all files within the specified directory and then
			// // delete the directory
			// try {
			// for (int i = 0; i < listfile.length; i++) {
			// File deletedFile = new File(newPath.toString() + "/"
			// + listfile[i].toString());
			// deletedFile.delete();
			// }
			// newPath.delete();
			// Log.i("DirectoryManager deleteDirectory", fileName);
			// status = true;
			// } catch (Exception e) {
			// e.printStackTrace();
			// status = false;
			// }
			//
			// } else
			// status = false;
		}
		// return status;
	}

	/**
	 * 删除播放缓存数据
	 */
	public static void delPlayCacheFiles(String url) {
		File file = new File(videoCachePath);
		if (!file.exists() || url == null)
			return;
		if (DownCenter.getExistingInstance() == null)
			return;
		if (url.startsWith("http://"))
			return;
		final String ppfilmHashID = LibVLC.getExistingInstance()
				.nativeGeneralHashID(url);
		if (TextUtils.isEmpty(ppfilmHashID))
			return;
		System.out.println("delPlayCacheFiles 删除文件  " + ppfilmHashID);
		File[] childFiles = file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.getAbsolutePath().contains(ppfilmHashID);
			}
		});
		if (childFiles != null && childFiles.length != 0)
			for (File file1 : childFiles) {
				System.out.println("delPlayCacheFiles---delfile-->"
						+ file1.getAbsolutePath());
				file1.delete();
			}

	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static void deleteFile(String filePath) {
		if (!TextUtils.isEmpty(filePath)) {
			File newPath = new File(filePath);
			if (newPath != null && newPath.isFile()) {
				newPath.delete();
			}
		}
	}

	public static void deleteFinishedFile(String url) {// 删除影片文件
		File file = new File(FileUtils.videoCachePath);
		if (!file.exists())
			return;
		final String ppfilmHashID = MD5Util.getMD5HashIDByUrl(url);
		if (ppfilmHashID == null)
			return;
		File[] childFiles = file.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return pathname.isFile()
						&& pathname.getAbsolutePath().contains(ppfilmHashID);
			}
		});
		if (childFiles != null && childFiles.length != 0)
			for (File file1 : childFiles) {
				file1.delete();
			}
	}

	/**
	 * 删除程序升级的apk 文件
	 */
	public static void delAppUpdataFile(final String path) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				File file = new File(path);
				if (file.exists() && file.isDirectory()) {
					File child[] = file.listFiles();
					if (child != null && child.length != 0)
						for (File file2 : child) {
							file2.delete();
						}

				}
			}
		}).start();
	}

	/**
	 * 是否是合法的HashID
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isLetterandNum(String mobiles) {
		if (mobiles == null)
			return false;
		Pattern p = Pattern.compile("[A-Z2-7_]+");

		Matcher m = p.matcher(mobiles);

		// System.out.println(m.matches() + "---");
		return m.matches();
	}

	/**
	 * 将皮皮播放地址转换为对应的HashID 值
	 * 
	 * @param pipiUrl
	 * @return
	 */
	private static String pipiUrltoHashID(String pipiUrl) {
		String hashID = "";
		int count = 5;
		int defaultLength = 32;// hashID 默认长度
		for (int i = 0; i < count; i++) {
			hashID = DownCenter.getExistingInstance()
					.getMovieInfoFromresultsInfos(pipiUrl);
			boolean isLetterandNum = isLetterandNum(hashID);
			System.out.println("-------------hashID" + hashID);
			if (hashID != null && hashID.length() == defaultLength
					&& isLetterandNum) {
				return hashID;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return hashID;
	}

	/**
	 * 去掉重复元素
	 * 
	 * @param list
	 */
	public static void removeDuplicate(List list) {
		HashSet h = new HashSet(list);
		list.clear();
		list.addAll(h);

	}

	/**
	 * 保存下载列表 和最后一次点播文件 hashid 集合
	 * 
	 * @param context
	 * @return
	 */
	private static List<String> getDownloadFileHashID(Context context) {
		List<String> hashIDList = new ArrayList<String>();
		// 最后一次播放的url 地址
		final String playUrl = PIPISharedPreferences.getInstance(context)
				.getLeastPlayUrl();
		// 最后一次点播文件hashid
		String playUrlfilmHashID = "";
		if (playUrl != null && !playUrl.equals("")) {
			// playUrlfilmHashID = DownCenter.getExistingInstance()
			// .getMovieInfoFromresultsInfos(playUrl);

			playUrlfilmHashID = pipiUrltoHashID(playUrl);
		}
		System.out.println("最后一次点播文件 HashID " + playUrlfilmHashID);

		if (playUrlfilmHashID != null && !playUrlfilmHashID.equals("")) {
			hashIDList.add(playUrlfilmHashID);
		}
		List<DownTask> downloadTasks = new ArrayList<DownTask>();
		if (DownCenter.getExistingInstance() != null) {
			downloadTasks = DownCenter.getExistingInstance().getDownTaskList();
		}
		if (downloadTasks != null && downloadTasks.size() != 0) {
			for (DownTask downTask : downloadTasks) {
				if (downTask.getDownLoadInfo() != null) {
					// String ppfilmHashID = DownCenter.getExistingInstance()
					// .getMovieInfoFromresultsInfos(
					// downTask.getMovieInfo().getMovieUrl());
					if (!downTask.getDownLoadInfo().getDownAddress()
							.startsWith("http://")) {

						String ppfilmHashID = pipiUrltoHashID(downTask
								.getDownLoadInfo().getDownAddress());
						// if (isLetterandNum(ppfilmHashID)) {
						hashIDList.add(ppfilmHashID);
						// }
					}
				}
			}
		}
		removeDuplicate(hashIDList);
		return hashIDList;

	}

	/**
	 * 检测文件hashID 列表中 是否存在乱码
	 * 
	 * @return true 存在乱码
	 */
	private static boolean isExistMessyCode(List<String> list) {
		boolean tmp = false;// 默认不存在乱码
		int defaultLength = 32;// hashID 默认长度
		if (list != null && !list.isEmpty()) {
			for (String string : list) {
				boolean isLetterandNum = isLetterandNum(string);
				if (string.length() != defaultLength || !isLetterandNum) {
					System.out.println("isExistMessyCode 乱码 HashID:" + string);
					tmp = true;
					break;
				}
			}
		} else {// list 为空返回 true
			tmp = true;
		}

		return tmp;
	}

	/**
	 * 清理缓存 判断是否是多余的文件
	 * 
	 * @param path
	 * @param list
	 * @return
	 */
	private static boolean isExtraFile(String path, List<String> list) {
		boolean tmp = false;
		if (list == null || list.size() == 0) {
			return tmp;
		}
		int count = 0;
		int size = list.size();
		// int stringLength = 32;
		for (String hashID : list) {
			// 如果hashID 为乱码 则跳过暂不清理文件
			// if(hashID.length()!=stringLength){
			// tmp=false;
			// break;
			// }
			if (!(path.contains(hashID))) {
				count++;
			}
		}
		if (count == size) {
			tmp = true;
		}
		return tmp;
	}

	/**
	 * 清理缓存 保留下载列表文件 保留最后一次点播的文件
	 */
	public static void delFileCache(final Context context) {
		
		deleteDirectory(videoCachePath);
		deleteDirectory(imgCachePath);
		File file = new File(videoCachePath);
		file.mkdirs();
		file = new File(imgCachePath);
		file.mkdirs(); 
		/* 秦元元
		 * final List<String> hashIDList = getDownloadFileHashID(context);
		System.out.println("-------hashIDList--------");
		for (String string : hashIDList) {
			System.out.println("hashIDList  hashID " + string);
		}
		System.out.println("-------hashIDList--------");
		if (hashIDList == null || hashIDList.size() == 0)
			return;
		if (isExistMessyCode(hashIDList)) {// 存在乱码 return
			return;
		}
		File file = new File(videoCachePath);
		if (!file.exists())
			return;
		File[] childFiles = file.listFiles();
		if (childFiles == null || childFiles.length == 0) {
			return; // cache 无文件时 return
		}
		for (File child : childFiles) {
			String path = child.getAbsolutePath();
			boolean isDel = isExtraFile(path, hashIDList);
			if (child.isFile() && isDel) {
				System.out.println("delFileCache 删除多余文件: " + child.getName());
				child.delete();
			}
		}*/

	}
}
