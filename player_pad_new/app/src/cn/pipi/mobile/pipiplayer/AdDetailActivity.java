package cn.pipi.mobile.pipiplayer;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;
import cn.pipi.mobile.pipiplayer.hd.R;

public class AdDetailActivity extends Activity {
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_detail_activity);

		ImageView img = (ImageView) findViewById(R.id.ad_detail_back);
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AdDetailActivity.this.finish();
			}
		});

		WebView view = (WebView) findViewById(R.id.ad_detail_web);
		String url = getIntent().getStringExtra("url");
		if (url == null || url.length() == 0)
			showToast("获取链接失败,请重试");
		view.getSettings().setJavaScriptEnabled(true);
		view.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// view.loadUrl(url);
				// System.out.println(url);
				if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
					showToast("抱歉,您没有安装SD卡,不能下载...");
					return true;
				}
				DownloadManager downloadManager = (DownloadManager) AdDetailActivity.this
						.getSystemService(Context.DOWNLOAD_SERVICE);
				Request request = new Request(Uri.parse(url));
				// 设置允许使用的网络类型，这里是移动网络和wifi都可以
				request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
						| DownloadManager.Request.NETWORK_WIFI);
				// 不显示下载界面
				request.setVisibleInDownloadsUi(false);
				/*
				 * 设置下载后文件存放的位置,如果sdcard不可用，那么设置这个将报错，因此最好不设置如果sdcard可用，下载后的文件
				 * 在/mnt/sdcard
				 * /Android/data/packageName/files目录下面，如果sdcard不可用,设置了下面这个将报错
				 * ，不设置，下载后的文件在/cache这个 目录下面
				 */
				request.setDestinationInExternalFilesDir(AdDetailActivity.this, "apk", name + ".apk");
				// request.setDestinationInExternalPublicDir("pipi_apk/",
				// FileUtils.getFileName(url));
				request.setTitle(name);
				request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				downloadManager.enqueue(request);
				return true;
			}
		});
		view.loadUrl(url);
	}

	private void showToast(String hint) {
		Toast.makeText(this, hint, Toast.LENGTH_SHORT);
	}
}
