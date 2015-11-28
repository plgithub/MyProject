package cn.pipi.mobile.pipiplayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.LogcatFileManager;

public class HtmlPlayer extends Activity {
	PowerManager powerManager = null;
	WakeLock wakeLock = null;
	private String url = null;
	private String tag = null;
	// "http://tv.sohu.com/20130614/n378869578.shtml";
	private View myView = null;
	private WebView mWebView;
	TimerTask mPreloadTask;
	Handler handler = new Handler();
	LinearLayout logoLayout;
	TextView t_progress;
	private boolean isFinish;// 页面是否结束;
	private boolean isYouku = false;
	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			finish();
		}
		if (android.os.Build.VERSION.SDK_INT >= 14) {// 优酷视频必须加加速器，否则放不出来
			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		}
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.htmlplayer);
		// logoLayout = (LinearLayout) findViewById(R.id.logoLayout);
		// t_progress = (TextView) findViewById(R.id.t_progress);

		init1();
//		startLog();
	}

	public void startLog() {
		LogcatFileManager.getInstance().startLogcatManager(this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// mWebView.saveState(outState);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	public void init1() {
		 if(getIntent()==null)return ;
		this.powerManager = (PowerManager) this
				.getSystemService(Context.POWER_SERVICE);
		this.wakeLock = this.powerManager.newWakeLock(
				PowerManager.FULL_WAKE_LOCK, "My Lock");
		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.setWebChromeClient(mChromeClient);
		mWebView.setWebViewClient(mWebViewClient);
		WebSettings webSetting = mWebView.getSettings();
		webSetting.setJavaScriptEnabled(true);
		// 适应屏幕
		// webSetting.setLoadWithOverviewMode(true);
		webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		// webSetting.setSupportZoom(true);
		// webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
		// mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 10);//
		// 设置缓冲大小，我设的�?0M
		// webSetting.setAllowFileAccess(true);
		webSetting.setPluginState(PluginState.ON);
		// webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			webSetting.setRenderPriority(RenderPriority.HIGH);
		}
		if (android.os.Build.VERSION.SDK_INT >= 8) {
			webSetting.setPluginState(WebSettings.PluginState.ON);
		}
		// webSetting.setBuiltInZoomControls(true);
		webSetting.setUseWideViewPort(true);
		// webSetting.setLoadWithOverviewMode(true);
		// webSetting.setBlockNetworkImage( true );//禁止加载图片，加快访问�?�?
		url = getIntent().getStringExtra("playurl");
		if(!TextUtils.isEmpty(url))
		mWebView.loadUrl(url);
	}

	private WebViewClient mWebViewClient = new WebViewClient() {
		// 处理页面导航
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// mWebView.loadUrl(url);
			Log.i(TAG, "shouldOverrideUrlLoading===" + url);
			// mWebView.loadUrl(url);
			// return true;
			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Log.i(TAG, "onPageFinished===" + url);
			// if (!isFinish)
			// DataUtil.getToast(HtmlPlayer.this,
			// getString(R.string.waitintformovie));
			// try {
			// mWebView.loadUrl("javascript: var v=document.getElementsByTagName('video')[0]; "
			// + "v.webkitEnterFullScreen();" + "v.play();");
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			//
			// if (isYouku && !isFinish)// 优酷
			// Toast.makeText(HtmlPlayer.this,
			// getString(R.string.waitintforyouku), 4000).show();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			Log.i(TAG, "onPageStarted===" + url);
		}

		@Override
		// 页面加载失败
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			Toast.makeText(HtmlPlayer.this, "Oh no! " + description,
					Toast.LENGTH_SHORT).show();
		}
	};

	// 浏览网页历史记录
	// goBack()和goForward()
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// mChromeClient.onHideCustomView();
			quit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void quit() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			finish();
		}
	}

	private WebChromeClient mChromeClient = new WebChromeClient() {

		private CustomViewCallback myCallback = null;

		// 配置权限 （在WebChromeClinet中实现）
		@Override
		public void onGeolocationPermissionsShowPrompt(String origin,
				GeolocationPermissions.Callback callback) {
			callback.invoke(origin, true, false);
			super.onGeolocationPermissionsShowPrompt(origin, callback);
		}

		// 扩充数据库的容量（在WebChromeClinet中实现）
		@Override
		public void onExceededDatabaseQuota(String url,
				String databaseIdentifier, long currentQuota,
				long estimatedSize, long totalUsedQuota,
				WebStorage.QuotaUpdater quotaUpdater) {

			quotaUpdater.updateQuota(estimatedSize * 2);
		}

		// 扩充缓存的容�?
		@Override
		public void onReachedMaxAppCacheSize(long spaceNeeded,
				long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {

			quotaUpdater.updateQuota(spaceNeeded * 2);
		}

		// Android 使WebView支持HTML5 Video（全屏）播放的方�?
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			if (myCallback != null) {
				myCallback.onCustomViewHidden();
				myCallback = null;
				return;
			}

			ViewGroup parent = (ViewGroup) mWebView.getParent();
			parent.removeView(mWebView);
			parent.addView(view);
			myView = view;
			setWidthAndHeight(view);
			myCallback = callback;
			mChromeClient = this;
			Log.i(TAG, "onShowCustomView");
		}

		private void setWidthAndHeight(View view) {
			LayoutParams para = view.getLayoutParams();
			para.width = LayoutParams.FILL_PARENT;
			para.height = LayoutParams.FILL_PARENT;
			view.setLayoutParams(para);
		}

		@Override
		public void onHideCustomView() {
			if (myView != null) {
				if (myCallback != null) {
					myCallback.onCustomViewHidden();
					myCallback = null;
				}

				ViewGroup parent = (ViewGroup) myView.getParent();
				parent.removeView(myView);
				parent.addView(mWebView);
				myView = null;
				Log.i(TAG, "onHideCustomView");
			}
		}

		@Override
		public void onProgressChanged(WebView view, int progress) {
			// if (progress == 0) {
			// logoLayout.setVisibility(0);
			// t_progress.setText(getString(R.string.buffer_loading));
			// } else if (progress > 0 && progress < 100) {
			// StringBuffer string = new StringBuffer(
			// getString(R.string.buffer_loading));
			// string.append(progress);
			// string.append("%");
			// t_progress.setText(string.toString());
			// } else if (progress == 100) {
			// logoLayout.setVisibility(8);
			// t_progress.setText(getString(R.string.buffer_loading));
			// }
			Log.i(TAG, "onProgressChanged=" + progress);
		}
	};

	private void callHiddenWebViewMethod(String name) {
		if (mWebView != null) {
			try {
				Method method = WebView.class.getMethod(name);
				method.invoke(mWebView);
			} catch (NoSuchMethodException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// mWebView.resumeTimers();
		// mWebView.onResume();
		callHiddenWebViewMethod("onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		mChromeClient.onHideCustomView();
		callHiddenWebViewMethod("onPause");
		// mWebView.onPause();
	}

	private String TAG = "HtmlPlayer";

	@Override
	protected void onDestroy() {
		LogcatFileManager.getInstance().stop();
		super.onDestroy();
		isFinish = true;
		mWebView.loadUrl("about:blank");
		mWebView.stopLoading();
		// callHiddenWebViewMethod("onPause");
	}

}
