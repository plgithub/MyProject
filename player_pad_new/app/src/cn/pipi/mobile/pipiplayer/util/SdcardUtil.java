package cn.pipi.mobile.pipiplayer.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

/**
 * 
 */
public class SdcardUtil {

	/** 软件下载APK文件存储目录 **/
	public static final String APK_Download_DIR = "pipiplayerHD/";
	private List<String> list = new ArrayList<String>();
	private File file = null;

	/**
	 * 判断SD卡是否存在
	 * 
	 * @return boolean(true表示SD卡存在，false表示SD卡不存在)
	 */
	public static boolean existSDcard() {
		boolean sdExistFlag = android.os.Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED);
		return sdExistFlag;
	}
	
	/**
	 * 检查是否安装外置的SD卡
	 * 
	 * @return
	 */
	public static boolean checkExternalSDExists() {
		
		Map<String, String> evn = System.getenv();
		return evn.containsKey("SECONDARY_STORAGE");
	}

	/**
	 * 获取存储apk文件的目录
	 * 
	 * @return 根据SD卡是否存在,返回不同的目录
	 */
	public static String getStoreApkDirPath(Context context) {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			String str = android.os.Environment.getExternalStorageDirectory()
					.toString();
			if (!str.endsWith("/") && !str.endsWith("\\")) {
				str += '/';
			}
			return (str + APK_Download_DIR);
		} else {
			return context.getApplicationContext().getFilesDir()
					.getAbsolutePath()
					+ "/" + APK_Download_DIR;
		}
	}

	public List<String> getFile(String path) {
		try {
			file = new File(path);
			if (file.exists() && file.isDirectory()) {// 检查path是否存在,并且是一个目录
				File[] files = file.listFiles();
				if (files != null) {
					for (File f : files) {
						list.add(f.getPath());

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<String> getAllFile(String path) {
		this.getFile(path);
		for (int i = 0; i < list.size(); i++) {
			this.getFile(list.get(i));
		}
		return list;
	}

	/**
	 * 根据后缀名过滤文件
	 * 
	 * @param suffixs
	 *            后缀名数组
	 * @param path
	 *            路劲
	 * @return 指定后缀名的文件集合
	 */
	public List<String> filterFileBySuffix(String[] suffixs, String path) {

		List<String> files = this.getAllFile(path);
		List<String> suffixList = new ArrayList<String>();
		for (String filePath : files) {
			file = new File(filePath);
			if (file.exists() && file.isFile()) {// 判断该路劲存在,并且为文件
				String tempSuffix = filePath.substring(filePath
						.lastIndexOf(".") + 1);
				for (String suffix : suffixs) {
					if (suffix.toLowerCase().equals(tempSuffix.toLowerCase())) {
						suffixList.add(filePath);
						System.out.println("存在---->" + filePath);
					}
				}
			}
		}
		suffixs = null;
		files = null;
		return suffixList;
	}

	/**
	 * 　　* 获取存储卡的剩余容量，单位为字节
	 * 
	 * 　　* @param filePath
	 * 
	 * 　　* @return availableSpare
	 * 
	 * 　　
	 */
	public static long getAvailableStore(String filePath) {

		// 取得sdcard文件路径
		StatFs statFs = new StatFs(filePath);
		// 获取block的SIZE
		long blocSize = statFs.getBlockSize();
		// 获取BLOCK数量
		// long totalBlocks = statFs.getBlockCount();
		// 可使用的Block的数量
		long availaBlock = statFs.getAvailableBlocks();
		// long total = totalBlocks * blocSize;
		long availableSpare = availaBlock * blocSize;
		return availableSpare;
	}

	/**
	 * 获取存储卡的总容量，单位为字节
	 * 
	 * @param filePath
	 * @return
	 */
	public static long getSdCardTotalStore(String filePath) {
		// 取得sdcard文件路径
		StatFs statFs = new StatFs(filePath);
		// 获取block的SIZE
		long blocSize = statFs.getBlockSize();
		// 获取BLOCK数量
		long totalBlocks = statFs.getBlockCount();
		long total = totalBlocks * blocSize;
		return total;
	}

	// qiny
	// -----------------------------------------------------------------------
	public static final String STATE = Environment.getExternalStorageState();

	/**
	 * @author qiny
	 */
	// 这个是手机内存的总空间大小
	static public long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	// 这个是手机内存的可用空间大小
	static public long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	/**
	 * 获取sd 卡大小
	 * 
	 * @param context
	 * @return
	 */
	public static String getSDSize(Context context) {
		String temp = "";
		File file = null;
		StatFs statFs = null;
		long blockSize = 0;
		long totalBlock = 0;
		long totalSdsize = 0;

		if (STATE.equals(Environment.MEDIA_MOUNTED)) {
			// Environment.getDataDirectory(); 获取手机内存
			file = Environment.getExternalStorageDirectory();
			statFs = new StatFs(file.getPath());
			totalBlock = statFs.getBlockCount();
			blockSize = statFs.getBlockSize();
			totalSdsize = totalBlock * blockSize;
		}
		temp = formatSize(context, totalSdsize);
		return temp;
	}

	/**
	 * 获取sd 卡大小
	 * 
	 * @param context
	 * @return
	 */
	public static long computeSDSize1(Context context) {
		// String temp = "";
		File file = null;
		StatFs statFs = null;
		long blockSize = 0;
		long totalBlock = 0;
		long totalSdsize = 0;

		if (STATE.equals(Environment.MEDIA_MOUNTED)) {
			// Environment.getDataDirectory(); 获取手机内存
			file = Environment.getExternalStorageDirectory();
			statFs = new StatFs(file.getPath());
			totalBlock = statFs.getBlockCount();
			blockSize = statFs.getBlockSize();
			totalSdsize = totalBlock * blockSize;
		}
		// temp = formatSize(context, totalSdsize);
		return totalSdsize;
	}

	/**
	 * 其他文件 大小 byte
	 * 
	 * @param context
	 * @param file
	 * @return
	 */
	public static long computeOtherSdSize(Context context, File file) {
		long result = 0;
		long sdTotalSize = computeSDSize1(context);
		long avaliSdsize = computeAvaliableSDSize1(context);
		long localMovieDirSize = getFileSize(file);

		result = sdTotalSize - avaliSdsize - localMovieDirSize;
		return result;
	}

	/**
	 * 得到sdcard其他空间大小
	 * 
	 * @param context
	 * @param file
	 * @return
	 */
	public static String getSDcardOtherSpace(Context context, File file) {
		String temp = "";
		temp = formatSize(context, computeOtherSdSize(context, file));
		return temp;
	}

	/**
	 * 获取剩余sd 卡大小
	 * 
	 * @param context
	 * @return
	 */
	public static long getAvaliableSDSize(Context context) {
		String temp = "";
		File file = null;
		StatFs statFs = null;
		long blockSize = 0;
		long AvaliableBlock = 0;
		long totalAvaliableSdsize = 0;

		if (STATE.equals(Environment.MEDIA_MOUNTED)) {
			file = Environment.getExternalStorageDirectory();
			statFs = new StatFs(file.getPath());
			AvaliableBlock = statFs.getAvailableBlocks();
			blockSize = statFs.getBlockSize();
			totalAvaliableSdsize = AvaliableBlock * blockSize;
		}
//		temp = formatSize(context, totalAvaliableSdsize);
		return totalAvaliableSdsize;
	}

	/**
	 * 获取剩余sd 卡大小
	 * 
	 * @param context
	 * @return
	 */
	public static long computeAvaliableSDSize1(Context context) {
		// String temp = "";
		File file = null;
		StatFs statFs = null;
		long blockSize = 0;
		long AvaliableBlock = 0;
		long totalAvaliableSdsize = 0;

		if (STATE.equals(Environment.MEDIA_MOUNTED)) {
			file = Environment.getExternalStorageDirectory();
			statFs = new StatFs(file.getPath());
			AvaliableBlock = statFs.getAvailableBlocks();
			blockSize = statFs.getBlockSize();
			totalAvaliableSdsize = AvaliableBlock * blockSize;
		}
		// temp = formatSize(context, totalAvaliableSdsize);
		return totalAvaliableSdsize;
	}

	/**
	 * 计算已使用空间
	 * 
	 * @param context
	 * @param size
	 * @return
	 */
	public static long computeUsedSDCard(Context context) {
		// String temp = "";
		long used = 0;

		if (STATE.equals(Environment.MEDIA_MOUNTED)) {
			used = computeSDSize1(context) - computeAvaliableSDSize1(context);
		}
		// temp = formatSize(context, totalAvaliableSdsize);
		return used;
	}

	// 格式化 转化为.MB格式

	public static String formatSize(Context context, long size) {
		return Formatter.formatFileSize(context, size);
	}

	public static String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.0");
		StringBuffer fileSizeString = new StringBuffer();
		if (fileS < 1024) {
			fileSizeString.append(df.format((double) fileS) + "B");
		} else if (fileS < 1048576) {
			fileSizeString.append(df.format((double) fileS / 1024) + "K");
		} else if (fileS < 1073741824) {
			fileSizeString.append(df.format((double) fileS / 1048576) + "M");
		} else {
			fileSizeString.append(df.format((double) fileS / 1073741824) + "G");
		}
		return fileSizeString.toString();
	}

	// 递归
	public static long getFileSize(File file) // 取得文件夹大小
	{
		if (file == null || !file.exists()) {
			return -1;
		}
		long size = 0;
		File flist[] = file.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	/**
	 * 得到本地影片
	 * 
	 * @param context
	 * @param file
	 * @return
	 */
	public static String getLocalMovieSize(Context context, File file) {
		long size = getFileSize(file);
		String temp = "";
		return size == -1 ? temp : Formatter.formatFileSize(context, size);
	}

	
}
