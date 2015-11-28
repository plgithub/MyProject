package cn.pipi.mobile.pipiplayer.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.R.integer;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

/**
 * 异步线程加载图片工具类 使用说明： BitmapManager bmpManager; bmpManager = new
 * BitmapManager(BitmapFactory.decodeResource(context.getResources(),
 * R.drawable.loading)); bmpManager.loadBitmap(imageURL, imageView);
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-6-25
 */
@SuppressLint("NewApi")
public class BitmapManager {

	private static ExecutorService pool;
	private static Map<ImageView, String> imageViews;
	private Bitmap defaultBmp;
	private static LruCache<String, Bitmap> mMemoryCache;
	private static int OUTTIME = 5;// 5秒
	int imgWidth;
	int imgHeight;

	public static BitmapManager bitmapManager;

	public BitmapManager() {
		// 获取当前系统的CPU数目
		int cpuNums = Runtime.getRuntime().availableProcessors();
		// 根据系统资源情况灵活定义线程池大小
		pool = Executors.newFixedThreadPool(cpuNums + 1);
		// pool = Executors.newFixedThreadPool(5); // 固定线程池
		imageViews = Collections
				.synchronizedMap(new WeakHashMap<ImageView, String>());
		// ........................
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		// 使用最大可用内存值的1/8作为缓存的大小。
		int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

			@Override
			protected int sizeOf(String key, Bitmap value) {
				// 重写此方法来衡量每张图片的大小，默认返回图片数量。
				// return value.getByteCount() / 1024;
				return value.getRowBytes() * value.getHeight();
			}

		};
	}

	public static BitmapManager getInstance() {
		synchronized (BitmapManager.class) {
			if (bitmapManager == null) {
				bitmapManager = new BitmapManager();
			}
			return bitmapManager;
		}
	}

	public int getImgWidth() {
		return imgWidth;
	}

	public void setImgWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}

	public int getImgHeight() {
		return imgHeight;
	}

	public void setImgHeight(int imgHeight) {
		this.imgHeight = imgHeight;
	}

	public Bitmap getDefaultBmp() {
		return defaultBmp;
	}

	public BitmapManager(Bitmap def) {
		this.defaultBmp = def;
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	/**
	 * 加载图片
	 * 
	 * @param url
	 * @param imageView
	 */
	public void loadBitmap(String url, ImageView imageView) {
		loadBitmap(url, imageView, this.defaultBmp, imgWidth, imgHeight);
	}

	/**
	 * 加载图片-可设置加载失败后显示的默认图片
	 * 
	 * @param url
	 * @param imageView
	 * @param defaultBmp
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp) {
		loadBitmap(url, imageView, defaultBmp, 0, 0);
	}

	/**
	 * 加载图片-可指定显示图片的高宽
	 * 
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp,
			int width, int height) {
		imageViews.put(imageView, url);
		Bitmap bitmap = getBitmapFromCache(url);
		if (bitmap != null) {
			// 显示缓存图片
//			System.out.println("----从缓存获取图片---");
			imageView.setImageBitmap(bitmap);
		} else {
			if (defaultBmp != null)
				imageView.setImageBitmap(defaultBmp);
			queueJob(url, imageView, width, height);

		}
	}

	/**
	 * 从缓存中获取图片
	 * 
	 * @param url
	 */
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap = getBitmapFromMemCache(url);
		return bitmap;
	}

	/**
	 * 从网络中加载图片
	 * 
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void queueJob(final String url, final ImageView imageView,
			final int width, final int height) {
		/* Create handler in UI thread. */
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				String tag = imageViews.get(imageView);
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						imageView.setImageBitmap((Bitmap) msg.obj);
					}
				}
			}
		};

		pool.execute(new Runnable() {
			public void run() {
				Message message = Message.obtain();
				message.obj = downloadBitmap(url, width, height);
				handler.sendMessage(message);
			}
		});
	}

	/**
	 * 下载图片-可指定显示图片的高宽
	 * 
	 * @param url
	 * @param width
	 * @param height
	 */
	private Bitmap downloadBitmap(String url, int width, int height) {
		Bitmap bitmap = null;
		try {
			bitmap = getBitmapByUrl(url, width, height);
			addBitmapToMemoryCache(url, bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 获取网络图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) {
		Bitmap bitmap = null;
		if (url == null)
			return null;
		// 保存文件
		InputStream is;
		HttpURLConnection conn = null;
		try {

			conn = (HttpURLConnection) new URL(url).openConnection();
			// conn.setRequestMethod("GET");

			conn.setConnectTimeout(OUTTIME * 1000);
			is = conn.getInputStream();

			if (is == null || is.available() == 0) {
				return null;
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_4444; // 默认是Bitmap.Config.ARGB_8888
			/* 下面两个字段需要组合使用 */
			options.inPurgeable = true;
			options.inInputShareable = true;
			bitmap = BitmapFactory.decodeStream(is, null, options);
			is.close();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			is = null;
			conn = null;
		}
		return bitmap;
	}

	// .................................
	/**
	 * 从缓存文件或者网络端获取图片
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getBitmapByUrl(String url) {
		String filename = FileUtils.getFileName(url);
		File f = new File(FileUtils.imgCachePath + File.separator + filename);
		Bitmap b = ImageUtils.decodeFile(f);
		if (b != null)
			return b;
		return ImageUtils.loadBitmapFromWeb(url, f);
	}

	private Bitmap getBitmapByUrl(String url, int w, int h) {
		// 从sd卡获取图片
		String filename = FileUtils.getFileName(url);
		File file = new File(FileUtils.imgCachePath + File.separator + filename);
		if (file != null && file.exists()) {// 加入缓存
			file.setLastModified(System.currentTimeMillis());// 最后时间修改
		}
		Bitmap b = ImageUtils.decodeFile(file, w, h);
		if (b != null){
//			System.out.println("----从SD卡获取图片---");
			return b;
		}
		return ImageUtils.loadBitmapFromWeb(url, file, w, h);
	}

}