package cn.pipi.mobile.pipiplayer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.updata.UpdateManager;

public class XMLPullParseUtil {

	private static final int TIMEOUT = 10 * 1000;

	// 评论session
	private static String writeReviewSession = "";

	// 获取所有的网页信息以String 返回
	private static String getStringFromHttp(HttpEntity entity) {

		StringBuffer buffer = new StringBuffer();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entity.getContent()));
			// 将返回的数据读到buffer中
			String temp = null;

			while ((temp = reader.readLine()) != null) {
				buffer.append(temp);
			}
		} catch (IllegalStateException e) {

			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return buffer.toString();
	}

	/**
	 * 
	 * @param context
	 *            上下文
	 * @param requestUrl
	 *            服务器端 版本号 请求地址
	 * @param handler
	 *            句柄
	 * @return 服务器端versionName
	 */
	public static void getServiceVersionName(Context context) {
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		try {
			URL url = new URL(AppConfig.SERVICE_VERSIONNAME_URL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			conn.connect();
			inputStream = null;
			if (conn.getResponseCode() != 200) {
				conn.disconnect();
				return;
			} else {
				inputStream = conn.getInputStream();
			}
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory
					.newInstance();
			// 获取XmlPullParser的实例
			XmlPullParser parser = pullParserFactory.newPullParser();

			// 设置输入流 xml文件
			parser.setInput(inputStream, "UTF-8");

			// 开始
			int eventType = parser.getEventType();

			String name = null;

			boolean isPad = false;

			while ((eventType != XmlPullParser.END_DOCUMENT)) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();
					if (name.equals("android-pad")) {
						isPad = true;
					}
					if (isPad && name.equals("minver")) {
						String tmp = parser.nextText();
						// AppConfig.minVersionName = tmp;
						UpdateManager.minVersionName = tmp;
					}
					if (isPad && name.equals("newver")) {
						String tmp = parser.nextText();
						// AppConfig.serverVersionName = tmp;
						UpdateManager.serverVersionName = tmp;
					}
					if (isPad && name.equals("datasource")) {
						// apk 升级所对应下载地址
						// AppConfig.apkUpdataUrl = parser.nextText();
						UpdateManager.apkUpdataUrl = parser.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					if (parser.getName().equals("android-pad")) {
						isPad = false;
					}
					break;
				}
				eventType = parser.next();
			}

		} catch (Exception e) {

		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				inputStream = null;
				if (conn != null)
					conn.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 获取热门搜索数据
	 */
	public static List<String> getHotSearchData(String urlPath) {
		List<String> tempList = new ArrayList<String>();
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		try {
			URL url = new URL(urlPath.replace(" ", ""));
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			conn.connect();
			inputStream = null;
			if (conn.getResponseCode() != 200) {
				Log.d(AppConfig.Tag, "获取热门搜索数据 失败!");
				conn.disconnect();
				return tempList;
			} else {
				inputStream = conn.getInputStream();
			}
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory
					.newInstance();
			// 获取XmlPullParser的实例
			XmlPullParser parser = pullParserFactory.newPullParser();

			// 设置输入流 xml文件
			parser.setInput(inputStream, "UTF-8");

			// 开始
			int eventType = parser.getEventType();

			String name = null;

			while ((eventType != XmlPullParser.END_DOCUMENT)) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					tempList = new ArrayList<String>();
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();// 获取解析器当前指向的元素的名称
					if (name.equals("video_name")) {
						tempList.add(parser.nextText());
					}
					// if (parser.getEventType() != XmlPullParser.END_TAG) {
					// parser.nextTag();
					// }
					break;
				case XmlPullParser.END_TAG:

					break;
				}
				eventType = parser.next();
			}

			return tempList;
		} catch (Exception e) {

		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				inputStream = null;
				if (conn != null)
					conn.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tempList;
	}

	/**
	 * 获取搜索结果
	 */
	public static SearchResultManager getSearchResult(String movieName) {
		int defaultPagersize = 10;
		String tmpMovieName = "";
		SearchResultManager searchResultManager = new SearchResultManager();
		if (TextUtils.isEmpty(movieName)) {
			return searchResultManager;
		}
		try {
			tmpMovieName = URLEncoder.encode(movieName, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuffer requestBuffer = new StringBuffer();
		requestBuffer.append(AppConfig.SEARCH_REQUEST_URL);
		requestBuffer.append("q=" + tmpMovieName + "&");
		requestBuffer.append("cl=" + "4" + "&");
		// requestBuffer.append("ps=" + defaultPagersize + "&");
		// requestBuffer.append("np=" + "1" + "&");
		// requestBuffer.append("tp=" + "" + "&");
		// requestBuffer.append("stp=" + "" + "&");
		// requestBuffer.append("ft=" + "" + "&");
		// requestBuffer.append("ar=" + "" + "&");
		// requestBuffer.append("la=" + "" + "&");
		// requestBuffer.append("pa=" + "" + "&");
		// requestBuffer.append("dr=" + "" + "&");
		// requestBuffer.append("sb=" + "" + "&");
		// requestBuffer.append("ord=" + "");
		requestBuffer.append("" + "dev=" + AppConfig.dev + "&");
		requestBuffer.append("dver=" + AppConfig.dver);
		Log.d(AppConfig.Tag, "搜索地址-->" + requestBuffer.toString());
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		try {
			URL url = new URL(requestBuffer.toString());
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			conn.connect();
			if (conn.getResponseCode() != 200) {
				conn.disconnect();
				return searchResultManager;
			} else {
				inputStream = conn.getInputStream();
			}

			String name = "";

			Set<String> typeSet = null;

			List<MovieInfo> list = null;

			MovieInfo movieInfo = null;

			XmlPullParserFactory pullParserFactory = XmlPullParserFactory
					.newInstance();
			// 获取XmlPullParser的实例
			XmlPullParser parser = pullParserFactory.newPullParser();
			// 设置输入流 xml文件
			parser.setInput(inputStream, "UTF-8");
			// 开始
			int eventType = parser.getEventType();

			final String imgUrl = "http://img.pipi.cn/movies/126X168/";

			while ((eventType != XmlPullParser.END_DOCUMENT)) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					typeSet = new HashSet<String>();
					list = new ArrayList<MovieInfo>();
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();// 获取解析器当前指向的元素的名称
					if (name.equals("videoitemtotal")) {
						String videoitemtotal = parser.nextText();
						if (TextUtils.isDigitsOnly(videoitemtotal)) {
							searchResultManager.setTotalCount(Integer
									.parseInt(videoitemtotal));
						}
					} else if (name.equals("pp_data")) {
						movieInfo = new MovieInfo();
					} else if (name.equals("id")) {
						movieInfo.setMovieID(parser.nextText());
					} else if (name.equals("img")) {
						movieInfo.setMovieImgPath(imgUrl + parser.nextText());
					} else if (name.equals("name")) {
						movieInfo.setMovieName(parser.nextText());
					} else if (name.equals("type")) {
						String type = parser.nextText();
						System.out.println(type);
						movieInfo.setMovieType(type);
						typeSet.add(type);
					} else if (name.equals("dafen_num")) {
						String grade = parser.nextText();
						if (!TextUtils.isEmpty(grade)) {
							DecimalFormat df = new DecimalFormat("#.0");
							String tmp = df
									.format(Double.parseDouble(grade) * 2);
							movieInfo.setGrade(tmp);
						} else {
							movieInfo.setGrade("");
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if ("pp_data".equals(parser.getName())) {
						list.add(movieInfo);
					}
					if ("SearchResult".equals(parser.getName())) {
						searchResultManager.setList(list);
						searchResultManager.setTypeSet(typeSet);
					}
					break;
				}
				eventType = parser.next();

			}

		} catch (Exception e) {
			Log.d(AppConfig.Tag, "搜索 Exception");
			conn = null;
			inputStream = null;
		} finally {
			conn = null;
			inputStream = null;
		}
		return searchResultManager;
	}

	/**
	 * 获取 电影 综艺 动漫 娱乐 数据
	 * 
	 */
	public static List<MovieInfo> getMainClassifyData(String requestUrl,
			MainClassifyRequestManager mainClassifyRequestManager) {
		List<MovieInfo> list = new ArrayList<MovieInfo>();
		if (TextUtils.isEmpty(requestUrl))
			return list;
		URL url = null;
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		try {
			url = new URL(requestUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			conn.connect();
			inputStream = null;
			if (conn.getResponseCode() != 200) {
				conn.disconnect();
				return list;
			}
			inputStream = conn.getInputStream();
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory
					.newInstance();
			// 获取XmlPullParser的实例
			XmlPullParser parser = pullParserFactory.newPullParser();
			// 设置输入流 xml文件
			parser.setInput(inputStream, "UTF-8");
			// 开始
			int eventType = parser.getEventType();
			String name = null;
			MovieInfo movieInfo = null;
			final String imgUrl = "http://img.pipi.cn/movies/126X168/";
			while ((eventType != XmlPullParser.END_DOCUMENT)) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					name = parser.getName();// 获取解析器当前指向的元素的名称
					if (name.equals("videoitemtotal")) {
						mainClassifyRequestManager.setTotalMovieSum(Integer
								.parseInt(parser.nextText()));
					}
					if (name.equals("pp_data")) {
						movieInfo = new MovieInfo();
					}
					if (name.equals("id")) {
						movieInfo.setMovieID(parser.nextText());
					} else if (name.equals("img")) {
						movieInfo.setMovieImgPath(imgUrl + parser.nextText());
					} else if (name.equals("name")) {
						movieInfo.setMovieName(parser.nextText());
					} else if (name.equals("type")) {
						movieInfo.setMovieType(parser.nextText());
					} else if (name.equals("dafen_num")) {
						String grade = parser.nextText();
						if (!TextUtils.isEmpty(grade)) {
							DecimalFormat df = new DecimalFormat("#.0");
							String tmp = df
									.format(Double.parseDouble(grade) * 2);
							movieInfo.setGrade(tmp);
						} else {
							movieInfo.setGrade("");
						}

					}
					// if (parser.getEventType() != XmlPullParser.END_TAG) {
					// parser.nextTag();
					// }
					break;
				case XmlPullParser.END_TAG:
					if ("pp_data".equals(parser.getName())) {
						list.add(movieInfo);
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			Log.d(AppConfig.Tag, "解析:" + requestUrl + ":Exception");
		}
		return list;
	}

	/**
	 * 获取推荐信息 String [] movieId 影片ID
	 */
	private static String[] getRecommendationSqit(String movieId) {
		final String requestUrl = "http://dm.pipi.cn/re?mid=" + movieId;
		Log.d(AppConfig.Tag, "推荐请求地址：" + requestUrl);
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);// 超时10�?
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT);// 等待数据10�?
		HttpClient HttpClient = new DefaultHttpClient(httpParams);
		HttpGet httpGet = new HttpGet(requestUrl);
		// httpost.setHeader("Content-Type", "application/json;charset=UTF-8");
		HttpResponse response = null;
		String getdata = "";
		String sqit[] = null;
		try {
			response = HttpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				getdata = getStringFromHttp(response.getEntity());
				if (getdata != null && !getdata.equals("")
						&& getdata.length() != 0) {
					String result = replaceBlank(getdata);
					if (!result.equals(""))
						sqit = result.split(";");
				}
			}
		} catch (ClientProtocolException e) {
			System.out.println("+++++++++ClientProtocolException++++++++");
			e.printStackTrace();
		} catch (ConnectException e) {
			System.out
					.println("+++++++++ConnectException++++++++ClientProtocolException");
		} catch (ConnectTimeoutException timeOutException) {
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("++++++IOException++++++++++");
		}
		return sqit;
	}

	/**
	 * 替换空格
	 * 
	 * @param str
	 * @return
	 */
	private static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 获取推荐信息 list
	 * 
	 * @param movieId
	 * @return
	 */
	public static List<MovieInfo> getRecommendationMovieList(String movieId) {
		String sqit[] = getRecommendationSqit(movieId);
		List<MovieInfo> list = new ArrayList<MovieInfo>();
		final String surPlus = "jse.movList.searchData[" + movieId + "].push";
		if (sqit != null && sqit.length != 0) {
			int start = surPlus.length() + 1;
			final String mov_id = "mov_id";
			final String mov_name = "mov_name";
			final String desc = "desc";
			final String mark = "mark";
			final String mov_pic = "mov_pic";
			final String imgUrl = "http://img.pipi.cn/movies/126X168/";
			try {
				for (int i = 1; i < sqit.length - 1; i++) {
					String item = sqit[i]
							.substring(start, sqit[i].length() - 1);
					JSONObject jsonbObject = new JSONObject(item);
					MovieInfo movieInfo = new MovieInfo();
					movieInfo.setMovieID(jsonbObject.getString(mov_id));
					String name = URLDecoder.decode(
							jsonbObject.getString(mov_name), "UTF-8");
					if(!TextUtils.isEmpty(name)){
						movieInfo.setMovieName(name.trim());
					}
					movieInfo.setMovieImgPath(jsonbObject.getString(mov_pic));
					movieInfo.setDesc(jsonbObject.optString(desc));
					String tmp = jsonbObject.optString(mark);
					if (!TextUtils.isEmpty(tmp)) {
						DecimalFormat df = new DecimalFormat("#.0");
						String movieMark = df.format(Double.parseDouble(tmp)*2);
						movieInfo.setGrade(movieMark);
					} else {
						movieInfo.setGrade("");
					}
					list.add(movieInfo);
				}
			} catch (Exception e) {
				// if(list!=null&&list.size()!=0){
				// list.clear();
				// }
			}

		}
		return list;
	}

	/**
	 * 意见与反馈 相关http请求 暂放这里 后续封装
	 */
	public static String requestSuggestion(String adviceContext,
			String telePhone, String userQQ, String userEmail) {
		String result = "";
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 30 * 1000);// 30
		HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);// 30
		HttpClient HttpClient = new DefaultHttpClient(httpParams);
		HttpPost httpRequest = new HttpPost(AppConfig.SUGGEST_URL);
		httpRequest.setHeader("Content-Type", "text/xml");
		HttpResponse httpResponse = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("adviceContent", adviceContext));
		params.add(new BasicNameValuePair("tp", telePhone));
		params.add(new BasicNameValuePair("qq", userQQ));
		params.add(new BasicNameValuePair("em", userEmail));
		// params.add(new BasicNameValuePair("deviceVersion", "6.0"));
		// params.add(new BasicNameValuePair("iOSVersion", "iPadSimulator"));
		params.add(new BasicNameValuePair("deviceVersion",
				android.os.Build.BRAND + android.os.Build.MODEL));
		params.add(new BasicNameValuePair("iOSVersion", ""
				+ android.os.Build.VERSION.SDK_INT));
		params.add(new BasicNameValuePair("UUID", AppConfig.deviceUUID));
		// params.add(new BasicNameValuePair("UUID",
		// "3285caafa3145b2b2012201701fe6570"));
		try {
			httpRequest.setEntity((HttpEntity) new UrlEncodedFormEntity(params,
					HTTP.UTF_8));
			httpResponse = HttpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// result = EntityUtils.toString(httpResponse.getEntity(),
				// HTTP.UTF_8);
				result = getStringFromHttp(httpResponse.getEntity());
				System.out.println("200---意见反馈----服务器返回--->" + result);
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 获取验证码图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getAuthImgNetBitmap(String url) {
		Bitmap bitmap = null;
		if (TextUtils.isEmpty(url))
			return null;
		// 保存文件
		writeReviewSession = "";
		InputStream inputStream = null;
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setReadTimeout(5 * 1000);
			conn.connect();
			if (conn.getResponseCode() != 200) {
				conn.disconnect();
				return null;
			} else {
				String tmpSession = conn.getHeaderField("Set-Cookie");
				if (!TextUtils.isEmpty(tmpSession)) {
					writeReviewSession = tmpSession.substring(0,
							tmpSession.indexOf(";"));
					System.out.println("session-->" + writeReviewSession);
				}
				inputStream = conn.getInputStream();
			}
			bitmap = BitmapFactory.decodeStream(inputStream);
			inputStream.close();
		} catch (Exception e) {
			System.out.println("获取验证码图片失败");
		} finally {
			inputStream = null;
			conn.disconnect();
			conn = null;
		}
		return bitmap;
	}

	/**
	 * 写影评
	 * 
	 * @param handler
	 * @param address
	 * @return
	 */
	public static Integer WriteMovieInfoSms(Handler handler, String address) {
		HttpURLConnection conn = null;
		try {
			// Log.i("MovieInfoSms", " WriteMovieInfoSms=="+address);

			URL url = new URL(address);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			conn.setRequestMethod("GET");// 以get方式发起请求
			conn.setUseCaches(false);// 不进行缓存
			if (!TextUtils.isEmpty(writeReviewSession))
				conn.setRequestProperty("session", writeReviewSession);
			conn.connect();
			if (conn.getResponseCode() != 200) {
				conn.disconnect();
				return -1;
			}
			conn.disconnect();
		} catch (Exception e) {
			return -1;
		}
		return 1;
	}

	/**
	 * 输入框搜索词汇
	 */
	@SuppressLint("NewApi")
	public static ArrayList<String> getEditSearchData(String movieName,
			Handler handler) {
		StringBuffer stringBuffer = new StringBuffer();
		String newText = "";
		try {
			newText = URLEncoder.encode(movieName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int num = 15;
		stringBuffer.append(AppConfig.SEARCH_AUTOCOMPLE);
		stringBuffer.append("" + newText + "&type=1&num=" + num);
		System.out.println("自动搜索请求地址:"+stringBuffer.toString());
		ArrayList<String> tempList = new ArrayList<String>();
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		try {
			URL url = new URL(stringBuffer.toString());
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			conn.setRequestMethod("GET");// 以get方式发起请求
			conn.setUseCaches(false);// 不进行缓存
			// 判断是否有sessionid；
			// if (PipiPlayerConstant.SESSION != null) {
			// conn.setRequestProperty("Cookie", PipiPlayerConstant.SESSION);
			// }
			conn.connect();
			inputStream = null;
			if (conn.getResponseCode() != 200) {
				// Log.i(PipiPlayerConstant.TAG,
				// "getHotSearchData--------------->抛异常");
				conn.disconnect();
				return tempList;
			} else {
				inputStream = conn.getInputStream();
				String cookieVal = conn.getHeaderField("Set-Cookie");
				// if (cookieVal != null) {
				// // 存储sessionid；
				// String sid = cookieVal.substring(0, cookieVal.indexOf(";"));
				// if (sid != null && !sid.isEmpty()) {
				// PipiPlayerConstant.SESSION = sid;
				// }
				// }
			}
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory
					.newInstance();
			// 获取XmlPullParser的实例
			XmlPullParser parser = pullParserFactory.newPullParser();

			// 设置输入流 xml文件
			parser.setInput(inputStream, "UTF-8");
			// 开始
			int eventType = parser.getEventType();
			String name = null;
			while ((eventType != XmlPullParser.END_DOCUMENT)) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					// Log.i(PipiPlayerConstant.TAG, "开始解析");
					break;
				case XmlPullParser.START_TAG:
					// Log.i(PipiPlayerConstant.TAG, "开始解析");
					name = parser.getName();// 获取解析器当前指向的元素的名称
					// Log.i(PipiPlayerConstant.TAG, "start:" + name);
					if (name.equals("Rs")) {
						// RecommBeans recommBeans = new RecommBeans();
						// recommBeans.setContent(parser.nextText());
						if (parser.getAttributeValue(1) != null) {
							// Log.i("TAG9994",
							// "edit key="+parser.getAttributeValue(1));
							tempList.add(parser.getAttributeValue(1));
						}
					}
					break;
				case XmlPullParser.END_TAG:
					name = null;
					break;
				}
				eventType = parser.next();
			}

			// PipiControl.sendMsgToHandler(handler,
			// PipiPlayerConstant.EXEC_NORMOL);
			return tempList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Log.i(PipiPlayerConstant.TAG, "解析异常");
			// PipiControl.sendMsgToHandler(handler,
			// PipiPlayerConstant.HTTP_IOEXCEPTION);
			// e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
				inputStream = null;
				if (conn != null)
					conn.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return tempList;
	}

	//获取评分信息
	// 获取评分信息
	public static ArrayList<Float> getMoviePingfen(Handler handler, int moiveID) {
		ArrayList<Float> result = new ArrayList<>();
//        http://user.pipi.cn/common/js/90/90764.js?d=1436335057463
		String httpURL = "http://user.pipi.cn/common/js/" + String.valueOf(moiveID / 1000) + "/" + String.valueOf(moiveID) + ".js?d=";
		java.util.Date dateNow = new java.util.Date(System.currentTimeMillis());
		httpURL += String.valueOf(dateNow.getTime());
		//Log.i("TAG999", "url=" + url);

		HttpURLConnection conn=null;
		InputStream inputStream = null;
		try {
			URL url = new URL(httpURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);//避免时间过长造成体验不好
			if (conn.getResponseCode() != 200) {
				conn.disconnect();
			} else {
				inputStream = conn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

				int nFound = 0, nPeople = -1;
				float flPingfen = 0;
				String strLine = null;
				while ((strLine = reader.readLine()) != null) {
					int index = 0;
					if ( (index = strLine.indexOf("markCount_")) != -1 ) {
						index = strLine.indexOf("=");
						if ( index >= 0 )
						{
							strLine = strLine.substring(index + 1);
							index = strLine.indexOf(";");
							if ( index >= 0 )
							{
								strLine = strLine.substring(0, index);
							}
							nPeople = Integer.parseInt(strLine);
							nFound++;
						}
					} else if ( (index = strLine.indexOf("mark_")) != -1 ) {
						index = strLine.indexOf("=");
						if ( index >= 0 )
						{
							strLine = strLine.substring(index + 1);
							index = strLine.indexOf(";");
							if ( index >= 0 )
							{
								strLine = strLine.substring(0, index);
							}
							flPingfen = Float.parseFloat(strLine);
							nFound++;
						}
					}
					if ( nFound >= 2 )
					{
						result.add((float)nPeople);
						result.add(flPingfen);
						break;
					}
				}
				conn.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
