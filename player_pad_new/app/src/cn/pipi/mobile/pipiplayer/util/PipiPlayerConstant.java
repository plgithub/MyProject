package cn.pipi.mobile.pipiplayer.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;

/**
 * 常量类 及相关共用静态变量
 *
 * @author Administrator qy
 *
 */
public class PipiPlayerConstant {

    static PipiPlayerConstant sInstance = null;

    public static PipiPlayerConstant getInstance() {
        synchronized (PipiPlayerConstant.class) {
            if (sInstance == null) {
                sInstance = new PipiPlayerConstant();
            }
        }
        return sInstance;
    }

    String[] sources = { "默认", "标清", "高清", "超清", "优酷", "乐视", "爱奇艺", "腾讯", "土豆",
            "搜狐", "网易", "新浪", "酷6", "迅雷", "激动网", "华数", "PPS", "PPTV", "风行",
            "CNTV", "电影网" };
    Integer[] iconSource = { R.drawable.iconpipi, R.drawable.iconpipi,
            R.drawable.iconpipi, R.drawable.iconpipi, R.drawable.iconyouku,
            R.drawable.iconleshi, R.drawable.iconqiy, R.drawable.icontengxun,
            R.drawable.icontudou, R.drawable.iconsouhu, R.drawable.iconwangyi,
            R.drawable.iconxinlang, R.drawable.iconku6, R.drawable.iconxunlei,
            R.drawable.iconjidong, R.drawable.iconhuashu, R.drawable.iconpps,
            R.drawable.iconpptv, R.drawable.iconfengxing, R.drawable.iconcntv,
            R.drawable.iconm1905 };

    public PipiPlayerConstant() {

        sourcesList = new HashMap<String, Integer>();
        for (int i = 0; i < sources.length; i++) {
            sourcesList.put(sources[i], iconSource[i]);
        }
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(VLCApplication.getAppContext());
        playnext = sharedPreferences.getBoolean("playnext", true);
        playfromhistroy = sharedPreferences.getBoolean("playfromhistroy", true);
        allowdown = sharedPreferences.getBoolean("allowdown", false);
        try {
            dev = URLEncoder.encode("android", "UTF-8");
            dver = URLEncoder.encode(VLCApplication.getAppResources()
                    .getString(R.string.pipiplayer_versionname), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        UUID = DataUtil.getDevicesUUID(VLCApplication.getAppContext());
    }

    public HashMap<String, Integer> sourcesList;// 片源
    // 设备类别 ios android
    public String dev = "";
    // 当前使用设备版本
    public String dver = "";
    // 设备唯一 识别码 UUID
    public String UUID = "";
    // 主界面请求地址 电影 综艺 电视剧
    public final String MAIN_REQUEST_URLBUFFER = "http://ms.pipi.cn/search?";
    // 搜索请求主地址
    public final String MAIN_SEARCH_URL = "http://ms.pipi.cn/search?";
    // 热门搜索数据
    public final String HOT_SEARCH_URL = "http://m.pipi.cn/hotkeys.xml";
    // 普通搜索数据
    public final String NORMAL_SEARCH_URL = "http://ms.pipi.cn/search?q=";
    // 注册和登陆 url 地址：
    public final String LOGIN_AND_REGISTER = "http://ucapi.pipi.cn/api/mobilepost.php?";
    // 意见反馈 请求地址
    public final String SUGGEST_URL = "http://mu.pipi.cn/suggestCommit.action";
    public final String KEY = "pipi_mobile_player_client";
    // 用户所属分线城市url
    public String PIPI_LINE_URL = "http://m.pipi.cn/line.html";
    // 热播 同步 动漫 综艺 新闻 url
    public final String HOTSHOW_URL = "http://m.pipi.cn/thehotmovie.xml";
    public final String SYNCHRONISM_URL = "http://m.pipi.cn/synchronismtheathe.xml";
    public final String CARTOON_URL = "http://m.pipi.cn/outstandinganimation.xml";
    public final String VARIETY_URL = "http://m.pipi.cn/popularvariety.xml";
    public final String NEWS_URL = "http://m.pipi.cn/newsinformation.xml";
    // 电影电视剧动漫等分类
    public final String CLASS_URL = "http://m.pipi.cn/videoCategory.xml";
    // URL 查看电影详细信息
    public final String MOVIE_DETIAL_URL = "http://m.pipi.cn/";
    // 验证码获取
    public final String VERIFY_URL = "http://user.pipi.cn/action/verify.jsp";
    // 版本检测
    public final String VERSIONINFO = "http://m.pipi.cn/versioninfo.xml";
    // 本地图片缓存目录
    public String CacheImageDir;
    // 访问网络session
    public String SESSION = null;
    public static final int GO_TO_START = 0x100;
    // 正常解析xml
    public static final int EXEC_NORMOL = 0x101;
    // http 状态码 非 200
    public static final int HTTP_STATE_NOTOK = 0x102;
    // http IOEXCEPTION
    public static final int HTTP_IOEXCEPTION = 0x103;
    // http 无网络
    public static final int NONETWORK = 0x104;
    // 没有数据返回,可能相应失败
    public static final int NO_DATA_RETURN = 0x105;
    // 数据为空
    public static final int DATA_RETURN_ZERO = 0x106;
    // 添加一页数据
    public static final int ADDPAGE_NORMOL = 0x107;
    // 删除数据数据
    public static final int DELETE_NORMOL = 0x108;
    // 插入数据 更新数据 查询数据 数据已经存在
    public static final int INSERT_NORMOL = 0x109;
    public static final int UPDATE_NORMOL = 0x10a;
    public static final int SELECT_NORMOL = 0x10b;
    public static final int ISEXIT_NORMOL = 0x10c;
    // 更新下载界面进度条
    public static final int UPDATEPROGRESS_VIEW = 0x10d;
    // 更新热门搜索词汇
    public static final int EXEC_NORMOL_HOTKEY = 0x10e;
    // 更新热门搜索词汇
    public static final int GETVERSION = 0x110;
    // 更新热门搜索词汇
    public static final int GETMOVIEBYCATE = 0x118;
    // 历史记录
    public final String HISTROY = "HISTROY";
    // 上次刷新时间
    public final String FLASHTIME = "FLASHTIME";
    // 是否点击了tab
    public boolean clickTab = false;
    // 是否自动播放下一集
    public boolean playnext = true;
    // 是否从历史位置开始播放
    public boolean playfromhistroy = true;
    // 是否允许gprs下载
    public boolean allowdown = true;
    // 当前查看得电影类别
    public String movieType = null;
    // 强制版本升级标签
    public final static int UPDATE_MINVER = 0x111;
    // 提示版本升级标签
    public final static int UPDATE_NEWVER = 0x112;
    // 提示版本升级标签
    public final static int UPDATE_NO = 0x113;// 无需升级
    // 跳往播放界面3G提示
    public final static int playmovieTip = 0x114;
    // 更新数字标识
    public final static int BadgeView = 0x115;
    // 选集
    public final static int RESOUT_SELECT = 0x116;
    // 获取所有分类数据
    public final static int GETMOVIETYPE = 0x117;
    // 刷新数据
    public static final int DATA_REFLASH = 0x118;
    public static final int ADS = 0x119;
    public final static String APIKEY = "Yy9CGF6GzoGxXhPDqkgSaWLz";
    // social demo
    public final static String SINA_APP_KEY = "319137445";
    // actionbar 高度
    public static int ActionBarHeight = 0;
}
