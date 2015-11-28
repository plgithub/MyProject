package cn.pipi.mobile.pipiplayer.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pipi.mobile.pipiplayer.bean.SourceBean;
import cn.pipi.mobile.pipiplayer.bean.TypesBean;
import cn.pipi.mobile.pipiplayer.hd.R;

/**
 * 常量参数
 * 
 * @author qiny
 * 
 */
public class AppConfig {

	public static final String Tag = "pipiplayerpad";

	public static AppConfig constants;

	// 120.193.9.147

	public static AppConfig getInstance() {
		synchronized (AppConfig.class) {
			if (constants == null) {
				constants = new AppConfig();
			}
		}
		return constants;
	}

	// 是否检测更新 默认检测更新
	public static boolean isCheckUpdata = true;
	// 默认不允许3G情况下下载
	public static boolean isAllow3GDownload = false;
	// 是否获取用户地址 如浙江省 杭州市
	public static boolean isGetClientAddress = false;
	// 是否闪屏界面加载分类数据
	public static boolean isSplashGetClassify = true;

	public static boolean playfromhistroy = true;
	// 客户端版本
	public static String minVersionName = "1.0.0";
	// 客户端版本
	public static String currentVersionName = "1.0.0";
	// 默认服务器端版本
	public static String serverVersionName = "1.0.0";
	// apk升级下载地址
	public static String apkUpdataUrl;

	public static String minver;

	public static String newver;
	// 默认屏幕宽度
	public static final int defaultScreenWidth = 1280;
	// 默认屏幕高度
	public static final int defaultScreenHeight = 752;
	// 默认Density
	public static float currentScreenDensity = 1f;
	// 当前屏幕宽度
	public static int currentScreenWidth;
	// 当前屏幕高度
	public static int currentScreenHeight;
	// clientUUID
	public static String deviceUUID;
	// 设备类别 ios android
	public static String dev = "";
	// 当前使用设备版本
	public static String dver = "";

	// client address 杭州移动
	public static String clientAddress = "";

	// ..........................................
	// 首页获取json数据key
	public static final String homePagerKeys[] = { "movie", "dalu", "gangtai", "oumei", "rihan", "cartoon", "variety" };
	// 首页获取json广告数据key
	public static final String homePagerAdKeys[] = { "movieAds_androidpad", "daluAds_androidpad",
			"gangtaiAds_androidpad", "oumeiAds_androidpad", "rihanAds_androidpad", "cartoonAds_androidpad",
			"varietyAds_androidpad" };
	// 主分类map数据
	public static Map<String, Map<String, List<TypesBean>>> classifyMap = new HashMap<String, Map<String, List<TypesBean>>>();
	// 主分类搜索名字
	public static final String[] mainClassifyNames = { "电影", "电视剧", "综艺", "动漫" };
	// 影片来源
	public static final String[] sourcekeys = { "默认", "标清", "高清", "超清", "优酷", "乐视", "爱奇艺", "腾讯", "土豆", "搜狐", "网易",
			"新浪", "酷6", "迅雷", "华数", "PPS", "PPTV", "风行", "CNTV", "电影网" };
	// 影片来源正常资源图片
	public static final int sourceNormolBG[] = { R.drawable.logo_pipi, R.drawable.logo_pipi, R.drawable.logo_pipi,
			R.drawable.logo_pipi, R.drawable.logo_youku, R.drawable.logo_leshi, R.drawable.logo_iqiyi,
			R.drawable.logo_tencent, R.drawable.logo_tudou, R.drawable.logo_souhu, R.drawable.logo_wangyi,
			R.drawable.logo_xinlang, R.drawable.logo_ku6, R.drawable.logo_xunlei, R.drawable.logo_huashu,
			R.drawable.logo_pps, R.drawable.logo_pptv, R.drawable.logo_fengxing, R.drawable.logo_cntv,
			R.drawable.logo_m1905 };

	// 影片来源按下资源图片
	public static final int sourcePressBG[] = {};
	// 影片所有来源key 和图片集合
	public static Map<String, SourceBean> sourceMap;
	// 当前详情页面影片来源
	// public static List<SourceBean> currentMovieDetialSources;
	// -------------------相关请求地址-------------
	// 获取服务器端versionName 地址
	public static final String SERVICE_VERSIONNAME_URL = "http://m.pipi.cn/versioninfo.xml";
	// 首页数据地址
	public static final String HOMEPAGER_REQUEST_URL = "http://m.pipi.cn/indexdata.js";
	// 获取影片详情请求地址 例影片ID为25000 http://m.pipi.cn/25/25000_info.js
	public static final String GET_MOVIEDETIALINFO_URL = " http://m.pipi.cn/";
	// 获取分类标签
	public static final String GET_CLASSIFYTYPES_URL = "http://m.pipi.cn/types.js";
	// 获取专题数据
	public static final String GET_SPECIAL_URL = "http://m.pipi.cn/zhuanti.js";
	// 获取大片数据
	public static final String GET_HOTMOVIE_URL = "http://m.pipi.cn/dapian.js";
	// 热门搜索数据
	public static final String HOT_SEARCH_URL = "http://m.pipi.cn/hotkeys.xml";
	// 搜索请求地址
	// public static final String SEARCH_REQUEST_URL =
	// "http://ms.pipi.cn/search?";
	public static final String SEARCH_REQUEST_URL = "http://ms.pipi.cn/search?";

	public static final String SEARCH_AUTOCOMPLE = "http://ms.pipi.cn/suggest?q=";
	// 意见反馈
	public static final String SUGGEST_URL = "http://mu.pipi.cn/suggestCommit.action";
	// 验证码获取url
	public static final String VERIFY_URL = "http://user.pipi.cn/action/verify.jsp";
}
