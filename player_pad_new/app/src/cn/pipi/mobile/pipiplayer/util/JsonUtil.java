package cn.pipi.mobile.pipiplayer.util;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pipi.mobile.pipiplayer.bean.AdInfo;
import cn.pipi.mobile.pipiplayer.bean.Const;
import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.bean.MovieReviewBean;
import cn.pipi.mobile.pipiplayer.bean.SourceBean;
import cn.pipi.mobile.pipiplayer.bean.SpecialMovieInfo;
import cn.pipi.mobile.pipiplayer.bean.TypesBean;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.db.PIPISharedPreferences;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;

public class JsonUtil {

	public static List<AdInfo> getAds(String url, String key) {
		String json = JsonGet.readJsonFromUrl(url, null);
		if (json == null)
			return null;
		List<AdInfo> list = new ArrayList<AdInfo>();
		try {
			JSONObject obj = new JSONObject(json);
			JSONArray array = obj.optJSONArray(key);
			for (int i = 0; i < array.length(); i++) {
				list.add(getAdInfo(array.optJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	private static AdInfo getAdInfo(JSONObject obj) {
		AdInfo info = new AdInfo();
		info.setId(obj.optString("id"));
		info.setImageUrl(obj.optString("imgurl"));
		info.setAppName(obj.optString("appname"));
		info.setAppDescription(obj.optString("appbriefing"));
		info.setHomePageUrl(obj.optString("homepageurl"));
		info.setDownloadUrl(obj.optString("downloadurl"));
		info.setPosition(obj.optInt("position") - 1);
		info.setAd(true);
		return info;
	}

	// 超时时间
	private static final int TIME_OUT = 15 * 1000;

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

	// 获取所有的网页信息以String 返回
	private static String getStringFromHttp(HttpEntity entity) {

		StringBuffer buffer = new StringBuffer();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
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
	 * 获取首页数据
	 * 
	 * @return
	 */
	public static Map<String, List<MovieInfo>> getHomePagerData() {
		String jsonString = getServerJsonString(Const.MAIN_LIST_AD_URL);
		if (TextUtils.isEmpty(jsonString))
			return null;
		PIPISharedPreferences.getInstance(VLCApplication.getAppContext()).putHomeData(jsonString);
		return parseHomepageData(jsonString);
	}

	public static Map<String, List<MovieInfo>> getHomePageDataByCache() {
		String json = PIPISharedPreferences.getInstance(VLCApplication.getAppContext()).getHomeData();
		if (json != null && json.length() > 0)
			return parseHomepageData(json);
		else
			return null;
	}

	private static Map<String, List<MovieInfo>> parseHomepageData(String jsonString) {
		final Map<String, List<MovieInfo>> map = new HashMap<String, List<MovieInfo>>();
		String id = "id";
		String name = "name";
		String subtitle = "subtitle";
		String img = "img";
		String state = "state";
		String dafen_num = "dafen_num";
		String type = "type";
		// System.out.println(jsonString);
		JSONObject jsonObject = null;
		JSONArray array = null;
		try {
			jsonObject = new JSONObject(jsonString);
			List<MovieInfo> list = null;
			for (int i = 0; i < AppConfig.homePagerKeys.length; i++) {
				array = jsonObject.getJSONArray(AppConfig.homePagerKeys[i]);
				list = new ArrayList<MovieInfo>();
				for (int j = 0; j < array.length(); j++) {
					JSONObject tmpJsonObject = array.getJSONObject(j);
					MovieInfo movieInfo = new MovieInfo();
					movieInfo.setMovieID(tmpJsonObject.getString(id));
					movieInfo.setMovieName(tmpJsonObject.getString(name));
					movieInfo.setMovieSubTitel(tmpJsonObject.getString(subtitle));
					movieInfo.setMovieImgPath(tmpJsonObject.getString(img));
					movieInfo.setMovieUpstate(tmpJsonObject.getString(state));
					String tmpDafen = jsonObject.optString(dafen_num);
					if (!TextUtils.isEmpty(tmpDafen)) {
						DecimalFormat df = new DecimalFormat("#.0");
						String tmp = df.format(Double.parseDouble(jsonObject.optString(dafen_num)) * 2);
						movieInfo.setGrade(tmp);
					} else {
						movieInfo.setGrade("");
					}
					movieInfo.setMovieType(tmpJsonObject.getString(type));
					list.add(movieInfo);
				}
				array = jsonObject.optJSONArray(AppConfig.homePagerAdKeys[i]);
				for (int j = 0; j < array.length(); j++) {
					JSONObject obj = array.optJSONObject(j);
					AdInfo info = new AdInfo();
					info.setId(obj.optString("id"));
					info.setImageUrl(obj.optString("imgurl"));
					info.setAppName(obj.optString("appname"));
					info.setAppDescription(obj.optString("appbriefing"));
					info.setHomePageUrl(obj.optString("homepageurl"));
					info.setDownloadUrl(obj.optString("downloadurl"));
					info.setPosition(obj.optInt("position") - 1);
					info.setAd(true);
					list.add(info.getPosition(), info);
					// list.remove(list.size()-1);
				}
				map.put(AppConfig.homePagerKeys[i], list);
			}
			list = new ArrayList<MovieInfo>();
			array = jsonObject.optJSONArray("roll");
			for (int i = 0; i < array.length(); i++) {
				JSONObject o = array.optJSONObject(i);
				MovieInfo info = new MovieInfo();
				info.setMovieName(o.optString("name"));
				info.setMovieID(o.optString("id"));
				info.setMovieImgPath(o.optString("img"));
				list.add(info);
			}
			map.put("scroll", list);
		} catch (Exception e) {
			Log.d(AppConfig.Tag, "首页数据获取异常");
			return null;
		} finally {
			jsonObject = null;
			array = null;
		}
		return map;
	}

	/**
	 * 获取json string
	 * 
	 * @return
	 */
	public static String getServerJsonString(String url) {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, TIME_OUT);// 超时10�?
		HttpConnectionParams.setSoTimeout(httpParams, TIME_OUT);// 等待数据10�?
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		String jsonString = "";
		try {
			// URLEncoder.encode(url,"UTF-8")
			HttpGet httpGet = new HttpGet(url.replace(" ", ""));
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				jsonString = getStringFromHttp(response.getEntity());
			}
		} catch (Exception e) {
			Log.d(AppConfig.Tag, "获取server Json 异常:" + url);
			e.printStackTrace();
		} finally {
			httpClient = null;
		}
		return jsonString;
	}

	/**
	 * 获取专题数据
	 * 
	 * @return
	 */
	public static List<SpecialMovieInfo> getSpecialData() {
		List<SpecialMovieInfo> list = new ArrayList<SpecialMovieInfo>();
		String jsonString = getServerJsonString(AppConfig.GET_SPECIAL_URL);
		// Log.d(AppConfig.Tag, "----专题json---");
		// Log.d(AppConfig.Tag, jsonString);
		// Log.d(AppConfig.Tag, "----专题json---");
		if (jsonString == null || jsonString.equals(""))
			return list;
		JSONObject jsonObject = null;
		JSONArray jsonArray = null;
		String tag = "data";
		String id = "id";
		String name = "name";
		String img = "img";
		String show_name = "show_name";
		int count = 0;
		try {
			jsonObject = new JSONObject(jsonString);
			jsonArray = jsonObject.getJSONArray(tag);
			count = jsonArray.length();
			if (count == 0)
				return list;
			for (int i = 0; i < count; i++) {
				JSONObject tmpJsonObject = jsonArray.getJSONObject(i);
				SpecialMovieInfo specialMovieInfo = new SpecialMovieInfo();
				specialMovieInfo.setMovieId(tmpJsonObject.getString(id));
				specialMovieInfo.setMovieImgPath(tmpJsonObject.getString(img));
				specialMovieInfo.setMovieName(tmpJsonObject.getString(name));
				specialMovieInfo.setMovieShowName(tmpJsonObject.optString(show_name));
				list.add(specialMovieInfo);
			}
		} catch (Exception e) {
			Log.d(AppConfig.Tag, "获取专题数据异常");
		} finally {
			jsonObject = null;
			jsonArray = null;
		}
		return list;
	}

	/**
	 * 获取所有分类
	 */
	// public static Map<String, ClassifyTypes> getAllClassifyTypes() {
	// Map<String, ClassifyTypes> map = new HashMap<>();
	// String jsonString = getServerJsonString(AppConfig.GET_CLASSIFYTYPES_URL);
	// if (TextUtils.isEmpty(jsonString))
	// return map;
	// // Log.d(AppConfig.Tag, "------分类标签Json----");
	// // Log.d(AppConfig.Tag, jsonString);
	// // Log.d(AppConfig.Tag, "------分类标签Json-----");
	// // final String tags[] = { "type", "area", "year", "cates" };
	// String id = "id";
	// String name = "name";
	// JSONObject jsonObject = null;
	// JSONObject tmpJsonObject = null;
	// JSONArray tmpJsonArray = null;
	// try {
	// jsonObject = new JSONObject(jsonString);
	// int length = jsonObject.length();
	// Log.d(AppConfig.Tag, "分类标签个数-->" + length);
	// for (int i = 0; i < length; i++) {// 电影 电视剧
	// ClassifyTypes classifyTypes = new ClassifyTypes();
	// tmpJsonObject = jsonObject
	// .getJSONObject(TypesManager.mainTypes[i]);
	// int tmplength = tmpJsonObject.length();
	// Log.d(AppConfig.Tag, "子分类标签个数-->" + length);
	// for (int j = 0; j < tmplength; j++) {// type area year cates
	// tmpJsonArray = tmpJsonObject.getJSONArray(TypesManager.tags[j]);
	// int jsonArrayLength = tmpJsonArray.length();
	// List<TypesBean> list = new ArrayList<>();
	// for (int k = 0; k < jsonArrayLength; k++) {
	// TypesBean typesBean = new TypesBean();
	// if (j != tmplength - 1) {
	// typesBean.setTypesName(tmpJsonArray.getString(k));
	// } else {
	// JSONObject itemJsonObject = tmpJsonArray
	// .getJSONObject(k);
	// typesBean.setTypesID(itemJsonObject.getString(id));
	// typesBean.setTypesName(itemJsonObject
	// .getString(name));
	// }
	// list.add(typesBean);
	// }
	// switch (j) {
	// case 0:
	// classifyTypes.setTypeBeans(list);
	// break;
	// case 1:
	// classifyTypes.setAreaBeans(list);
	// break;
	// case 2:
	// classifyTypes.setYearsBeans(list);
	// break;
	// case 3:
	// classifyTypes.setCatesBeans(list);
	// break;
	//
	// }
	// }
	// map.put(TypesManager.mainTypes[i], classifyTypes);
	// }
	// // Log.d(AppConfig.Tag, "map--->" + map);
	// // for (int i = 0; i < TypesManager.mainTypes.length; i++) {
	// // ClassifyTypes classifyTypes = map
	// // .get(TypesManager.mainTypes[i]);
	// // Log.d(AppConfig.Tag,
	// // "<----------------start-------------------->");
	// // for (TypesBean typesBean : classifyTypes.getTypeBeans()) {
	// // Log.d(AppConfig.Tag, "classifyTypes.getTypeBeans()-->"
	// // + typesBean.getTypesName());
	// // }
	// // for (TypesBean typesBean : classifyTypes.getAreaBeans()) {
	// // Log.d(AppConfig.Tag, "classifyTypes.getAreaBeans()-->"
	// // + typesBean.getTypesName());
	// // }
	// // for (TypesBean typesBean : classifyTypes.getYearsBeans()) {
	// // Log.d(AppConfig.Tag, "classifyTypes.getYearsBeans()-->"
	// // + typesBean.getTypesName());
	// // }
	// // for (TypesBean typesBean : classifyTypes.getCatesBeans()) {
	// // Log.d(AppConfig.Tag, "classifyTypes.getCatesBeans()-->"
	// // + typesBean.getTypesName());
	// // }
	// // Log.d(AppConfig.Tag,
	// // "<-----------------end------------------->");
	// // }
	//
	// } catch (Exception e) {
	// Log.d(AppConfig.Tag, "解析分类标签异常");
	// } finally {
	// jsonObject = null;
	// tmpJsonObject = null;
	// tmpJsonArray = null;
	// }
	// return map;
	// }
	/**
	 * 获取所有分类
	 */
	public static Map<String, Map<String, List<TypesBean>>> getAllClassifyTypes() {
		// Map<String, ClassifyTypes> map = new HashMap<>();

		Map<String, Map<String, List<TypesBean>>> map = new HashMap<String, Map<String, List<TypesBean>>>();

		String jsonString = getServerJsonString(AppConfig.GET_CLASSIFYTYPES_URL);
		if (TextUtils.isEmpty(jsonString))
			return map;
		// Log.d(AppConfig.Tag, "------分类标签Json----");
		// Log.d(AppConfig.Tag, jsonString);
		// Log.d(AppConfig.Tag, "------分类标签Json-----");
		// final String tags[] = { "type", "area", "year", "cates" };
		String id = "id";
		String name = "name";
		JSONObject jsonObject = null;
		JSONObject tmpJsonObject = null;
		JSONArray tmpJsonArray = null;
		try {
			jsonObject = new JSONObject(jsonString);
			int length = jsonObject.length();
			// Log.d(AppConfig.Tag, "分类标签个数-->" + length);
			for (int i = 0; i < length; i++) {// 电影 电视剧
				// ClassifyTypes classifyTypes = new ClassifyTypes();
				Map<String, List<TypesBean>> childMap = new HashMap<String, List<TypesBean>>();
				tmpJsonObject = jsonObject.getJSONObject(TypesManager.mainTypes[i]);
				int tmplength = tmpJsonObject.length();
				// Log.d(AppConfig.Tag, "子分类标签个数-->" + length);
				for (int j = 0; j < tmplength; j++) {// type area year cates
					tmpJsonArray = tmpJsonObject.getJSONArray(TypesManager.tags[j]);
					int jsonArrayLength = tmpJsonArray.length();
					List<TypesBean> list = new ArrayList<TypesBean>();
					for (int k = 0; k < jsonArrayLength; k++) {
						TypesBean typesBean = new TypesBean();
						if (j != tmplength - 1) {
							typesBean.setTypesName(tmpJsonArray.getString(k));
						} else {
							JSONObject itemJsonObject = tmpJsonArray.getJSONObject(k);
							typesBean.setTypesID(itemJsonObject.getString(id));
							typesBean.setTypesName(itemJsonObject.getString(name));
						}
						list.add(typesBean);
					}
					childMap.put(TypesManager.tags[j], list);
				}
				map.put(TypesManager.mainTypes[i], childMap);
			}
			// Log.d(AppConfig.Tag, "----test_getallClassify---start");
			// for (int i = 0; i < TypesManager.mainTypes.length; i++) {
			// Map<String, List<TypesBean>>
			// testMap=map.get(TypesManager.mainTypes[i]);
			// Log.d(AppConfig.Tag, "----"+TypesManager.mainTypes[i]+"---");
			// for (Entry<String, List<TypesBean>> child : testMap.entrySet()) {
			// List<TypesBean> list=child.getValue();
			// for (TypesBean typesBean : list) {
			// if(!TextUtils.isEmpty(typesBean.getTypesID()))
			// Log.d(AppConfig.Tag, typesBean.getTypesID());
			// Log.d(AppConfig.Tag, typesBean.getTypesName());
			// }
			// }
			// Log.d(AppConfig.Tag, "----"+TypesManager.mainTypes[i]+"---");
			// }
			// Log.d(AppConfig.Tag, "----test_getallClassify---end");
		} catch (Exception e) {
			Log.d(AppConfig.Tag, "解析分类标签异常");
			return null;
		} finally {
			jsonObject = null;
			tmpJsonObject = null;
			tmpJsonArray = null;
		}
		return map;
	}

	/**
	 * 获取影片详情
	 * 
	 * @return MovieInfo
	 */
	public static MovieInfo getMovieDetialInfo(String requestUrl) {
		MovieInfo movieInfo = new MovieInfo();
		if (TextUtils.isEmpty(requestUrl))
			return null;
		String jsonString = getServerJsonString(requestUrl);
		if (TextUtils.isEmpty(jsonString))
			return null;
		Log.d(AppConfig.Tag, "--------获取影片详情----");
		Log.d(AppConfig.Tag, jsonString);
		Log.d(AppConfig.Tag, "--------获取影片详情----");
		String id = "id";
		String img = "img";
		String name = "name";
		String actor = "actor";
		String director = "director";
		String desc = "desc";
		String area = "area";
		String year = "year";
		String dafen_num = "dafen_num";
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			movieInfo.setMovieID(jsonObject.getString(id));
			movieInfo.setMovieImgPath(jsonObject.getString(img));
			movieInfo.setMovieName(jsonObject.getString(name));
			movieInfo.setActor(jsonObject.getString(actor));
			movieInfo.setDirector(jsonObject.getString(director));
			movieInfo.setDesc(jsonObject.getString(desc));
			movieInfo.setArea(jsonObject.getString(area));
			movieInfo.setYear(jsonObject.getString(year));
			DecimalFormat df = new DecimalFormat("#.0");
			String grade = jsonObject.getString(dafen_num);
			if (!TextUtils.isEmpty(grade)) {
				String tmp = df.format(Double.parseDouble(grade) * 2);
				movieInfo.setGrade(tmp);
			} else {
				movieInfo.setGrade("");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.d(AppConfig.Tag, "获取影片详情:JSONException");
		}
		return movieInfo;
	}

	/**
	 * 获取电影 电视剧 动漫 子分类数据
	 * 
	 * @return
	 */
	public static List<MovieInfo> getItemClassifyData(String classifyId) {
		List<MovieInfo> list = new ArrayList<MovieInfo>();
		if (TextUtils.isEmpty(classifyId)) {
			return list;
		}
		;
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("http://m.pipi.cn/typedata/");
		stringBuffer.append(classifyId);
		stringBuffer.append(".js");
		Log.d(AppConfig.Tag, "子分类请求url:" + stringBuffer.toString());
		String jsonString = getServerJsonString(stringBuffer.toString());
		if (TextUtils.isEmpty(jsonString)) {
			Log.d(AppConfig.Tag, "子分类json null");
			return list;
		}
		final String key = "movie_info";
		final String id = "id";
		final String name = "name";
		final String dafen_num = "dafen_num";
		final String img = "img";
		final String area = "area";
		final String year = "year";
		final String state = "state";
		final String actors = "actors";
		final String sub_title = "sub_title";

		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray(key);
			int size = jsonArray.length();
			for (int i = 0; i < size; i++) {
				MovieInfo movieInfo = new MovieInfo();
				JSONObject tmpJsonObject = jsonArray.getJSONObject(i);
				movieInfo.setMovieID(tmpJsonObject.getString(id));
				movieInfo.setMovieName(tmpJsonObject.getString(name));
				String dafen = jsonObject.optString(dafen_num);
				if (!TextUtils.isEmpty(dafen)) {
					DecimalFormat df = new DecimalFormat("#.0");
					String tmp = df.format(Double.parseDouble(dafen) * 2);
					movieInfo.setGrade(tmp);
				} else {
					movieInfo.setGrade("");
				}
				movieInfo.setMovieImgPath(tmpJsonObject.getString(img));
				movieInfo.setDesc(tmpJsonObject.getString(area));
				movieInfo.setMovieSubTitel(tmpJsonObject.getString(sub_title));
				movieInfo.setMovieUpstate(tmpJsonObject.getString(state));
				list.add(movieInfo);
			}

		} catch (Exception e) {
			Log.d(AppConfig.Tag, "解析子分类 json Exception");
		}
		return list;
	}

	/**
	 * 获取大片数据
	 * 
	 * @return
	 */
	public static List<SpecialMovieInfo> getFireFilmsData() {
		List<SpecialMovieInfo> list = new ArrayList<SpecialMovieInfo>();
		String jsonString = getServerJsonString(AppConfig.GET_HOTMOVIE_URL);
		if (TextUtils.isEmpty(jsonString)) {
			Log.d(AppConfig.Tag, "getFireFilmsData:jsonString=null");
			return list;
		}
		final String id = "id";
		final String name = "name";
		final String img = "img";
		final String show_name = "show_name";
		Log.d(AppConfig.Tag, jsonString);
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			int count = jsonArray.length();
			if (count != 0) {
				for (int i = 0; i < count; i++) {
					SpecialMovieInfo movieInfo = new SpecialMovieInfo();
					JSONObject tmpJsonObject = jsonArray.getJSONObject(i);
					movieInfo.setMovieId(tmpJsonObject.getString(id));
					movieInfo.setMovieName(tmpJsonObject.getString(name));
					movieInfo.setMovieImgPath(tmpJsonObject.getString(img));
					movieInfo.setMovieShowName(tmpJsonObject.getString(show_name));
					list.add(movieInfo);
				}
			}
		} catch (JSONException e) {
			Log.d(AppConfig.Tag, "解析大片数据：JSONException");
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取影评 pager 第一页
	 */
	// public static List<MovieReviewBean> getMovieReview(String movieID, int
	// pager) {
	// List<MovieReviewBean> list = new ArrayList<>();
	// if (TextUtils.isEmpty(movieID) || !TextUtils.isDigitsOnly(movieID)) {
	// return list;
	// }
	// String tmpMovieID = movieID.substring(0, 3);
	// String requestUrl = "http://user.pipi.cn/common/minirev/" + tmpMovieID
	// + "/" + movieID + "_" + pager + ".js";
	// Log.d(AppConfig.Tag, "影评请求地址:" + requestUrl);
	// String result = getServerJsonString(requestUrl);
	// Log.d(AppConfig.Tag, "---------------------");
	// Log.d(AppConfig.Tag, result);
	// Log.d(AppConfig.Tag, "---------------------");
	// if (TextUtils.isEmpty(result)) return list;
	// String tmpResult=replaceBlank(result);
	// String sqit[]=tmpResult.split(";");
	// if (sqit != null && sqit.length != 0) {
	// String totalPager = "";
	// int PagerSum = 0;
	// int firstPlus = "mini.review.pInfo=".length();
	// // int sencondPlus="mini.review.contents=".length();
	// int thirdPlus = "mini.review.contents.push".length();
	// String content = "content";
	// String userNickName = "userNickName";
	// String revDate = "revDate";
	// try {
	// for (int i = 0; i < sqit.length - 1; i++) {
	// String item = sqit[i];
	// if (i == 0) {
	// String child = item.substring(firstPlus, item.length());
	// JSONObject jsonbObject = new JSONObject(child);
	// totalPager = jsonbObject.getString("pageCount");
	// PagerSum = Integer.parseInt(totalPager);
	// System.out.println("PagerSum " + PagerSum);
	// }
	//
	// if (i > 1) {
	// String child = item.substring(thirdPlus + 1,
	// sqit[i].length() - 1);
	// JSONObject jsonbObject = new JSONObject(child);
	// MovieReviewBean movieReviewBean = new MovieReviewBean();
	// movieReviewBean.setContent(jsonbObject
	// .getString(content));
	// String name = URLDecoder.decode(
	// jsonbObject.getString(userNickName), "UTF-8");
	// movieReviewBean.setUserNickName(name);
	// movieReviewBean.setRevDate(jsonbObject
	// .getString(revDate));
	// movieReviewBean.setPagerCount(PagerSum);
	// list.add(movieReviewBean);
	// }
	// }
	// } catch (Exception e) {
	// // if(list!=null&&list.size()!=0){
	// // list.clear();
	// // }
	// System.out.println("getMovieReviewList Exception");
	// }
	//
	// }
	// return list;
	// }

	/**
	 * 获取影评 pager 第一页
	 */
	public static MovieReviewManager getMovieReview(String movieID, int pager) {
		MovieReviewManager manager = new MovieReviewManager();
		List<MovieReviewBean> list = new ArrayList<MovieReviewBean>();
		if (TextUtils.isEmpty(movieID) || !TextUtils.isDigitsOnly(movieID)) {
			return manager;
		}
		String tmpMovieID = movieID.substring(0, 3);
		String requestUrl = "http://user.pipi.cn/common/minirev/" + tmpMovieID + "/" + movieID + "_" + pager + ".js";
		Log.d(AppConfig.Tag, "影评请求地址:" + requestUrl);
		String result = getServerJsonString(requestUrl);
		Log.d(AppConfig.Tag, "---------------------");
		Log.d(AppConfig.Tag, result);
		Log.d(AppConfig.Tag, "---------------------");
		if (TextUtils.isEmpty(result))
			return manager;
		String tmpResult = replaceBlank(result);
		String sqit[] = tmpResult.split(";");
		if (sqit != null && sqit.length != 0) {
			String totalPager = "";
			int PagerSum = 0;
			int firstPlus = "mini.review.pInfo=".length();
			// int sencondPlus="mini.review.contents=".length();
			int thirdPlus = "mini.review.contents.push".length();
			String content = "content";
			String userNickName = "userNickName";
			String revDate = "revDate";
			try {
				for (int i = 0; i < sqit.length - 1; i++) {
					String item = sqit[i];
					if (i == 0) {
						String child = item.substring(firstPlus, item.length());
						JSONObject jsonbObject = new JSONObject(child);
						totalPager = jsonbObject.getString("pageCount");
						PagerSum = Integer.parseInt(totalPager);
						manager.setPagerCount(PagerSum);
						System.out.println("PagerSum " + PagerSum);
					}

					if (i > 1) {
						String child = item.substring(thirdPlus + 1, sqit[i].length() - 1);
						JSONObject jsonbObject = new JSONObject(child);
						MovieReviewBean movieReviewBean = new MovieReviewBean();
						movieReviewBean.setContent(jsonbObject.getString(content));
						String name = URLDecoder.decode(jsonbObject.getString(userNickName), "UTF-8");
						movieReviewBean.setUserNickName(name);
						movieReviewBean.setRevDate(jsonbObject.getString(revDate));
						movieReviewBean.setPagerCount(PagerSum);
						list.add(movieReviewBean);
					}
				}
				manager.setList(list);
			} catch (Exception e) {
				// if(list!=null&&list.size()!=0){
				// list.clear();
				// }
				System.out.println("getMovieReviewList Exception");
			}

		}
		return manager;
	}

	/**
	 * 获取下载地址集合
	 * 
	 * @return
	 */
	public static Map<String, List<DownLoadInfo>> getMovieDownLoadList(String movieId, MovieInfo movieInfo) {
		// AppConfig.currentMovieDetialSources=new ArrayList<>();
		// if(AppConfig.currentMovieDetialSources.size()!=0){
		// AppConfig.currentMovieDetialSources.clear();
		// }
		List<SourceBean> sourcelist = new ArrayList<SourceBean>();
		Map<String, List<DownLoadInfo>> map = new HashMap<String, List<DownLoadInfo>>();
		if (TextUtils.isEmpty(movieId) || !TextUtils.isDigitsOnly(movieId)) {
			return map;
		}
		StringBuffer stringBuffer = new StringBuffer();
		int subId = Integer.parseInt(movieId);
		stringBuffer.append(AppConfig.GET_MOVIEDETIALINFO_URL);
		stringBuffer.append("" + (subId / 1000) + "/");
		stringBuffer.append("" + movieId + "_hash.js");
		Log.d(AppConfig.Tag, "详情影片 获取影片下载列表 请求Url-->" + stringBuffer.toString());
		String jsonString = getServerJsonString(stringBuffer.toString());
		String data = "data";
		String source_name = "source_name";
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONObject dataJsonObject = (JSONObject) jsonObject.get(data);
			JSONArray sourceArray = (JSONArray) jsonObject.opt(source_name);
			int length = dataJsonObject.length();
			int sourceLength = sourceArray.length();
			if (length == 0 || sourceLength == 0)
				return map;
			for (int i = 0; i < sourceLength; i++) {
				String key = (String) sourceArray.opt(i);
				JSONArray jsonArray = dataJsonObject.getJSONArray(key);
				List<DownLoadInfo> list = new ArrayList<DownLoadInfo>();
				int jsonarrayLength = jsonArray.length();
				for (int j = 0; j < jsonarrayLength; j++) {
					DownLoadInfo downloadInfo = new DownLoadInfo();
					downloadInfo.setDownAddress(jsonArray.optString(j));
					list.add(downloadInfo);
				}
				map.put(key, list);
				if (AppConfig.sourceMap.containsKey(key)) {
					// AppConfig.currentMovieDetialSources.add(AppConfig.sourceMap
					// .get(key));
					sourcelist.add(AppConfig.sourceMap.get(key));
				}
			}
			movieInfo.setCurrentMovieDetialSources(sourcelist);
			// Log.d(AppConfig.Tag, "" + map);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {

		}
		return map;
	}
}
