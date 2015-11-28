package cn.pipi.mobile.pipiplayer.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.pipi.mobile.pipiplayer.DownTask;
import cn.pipi.mobile.pipiplayer.adapter.DownloadDetailAdapter;
import cn.pipi.mobile.pipiplayer.adapter.MovieDetialGridAdapter;
import cn.pipi.mobile.pipiplayer.adapter.MovieDetialSourceAdapter;
import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.bean.SourceBean;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.JsonUtil;

/**
 * Created by admin on 2015/11/4.
 */
public class CustomDialog implements View.OnClickListener, AdapterView.OnItemClickListener {
    private int x;
    private int y;
    private LayoutInflater inflater;
    private int screenWidth;
    private int screenHeight;
    //子控件的宽和高
    private int childWidth;
    private int childHeight;
    private int position_x;
    private int position_y;
    private ImageView top_triangle;
    private ImageView bottom_triangle;
    private View anchorView;
    private Dialog dialog;
    private View dialogview;
    //记录控件在窗口中的位置
    private int location[] = new int[2];
    private Context context;
    private PopupWindow popupWindow;
    private boolean isMeasured = false;
    private RelativeLayout download_view;
    private TextView movie_name;
    private TextView allpause;
    private TextView edit;
    private TextView add;
    private GridView detailGrid;
    private List<DownTask> list;
    private static CustomDialog instance;
    private boolean isEidtable = false;
    public static boolean isAdd = false;
    private boolean isExpand = false;
    public DownloadDetailAdapter adapter;
    private ImageView current_sourceIcon;
    private TextView current_sourceTag;
    private ImageView sourceIcon_arrowdown;
    private Button moviedetial_descbtn;
    private MyGridView source_gridview;
    private MyGridView position_gridview;
    private String movieName;
    private int resid;
    private String lastSourcTag;
    private MovieInfo movieInfo;
    public final int GET_MOVIE_SOURCES = 1;
    public static final int UPDATE_DETIALGRIADAPTER = 2;
    private MovieDetialSourceAdapter source_adapter;
    public MovieDetialGridAdapter detial_adapter;
    private Drawable upShadowDrawable;
    private Drawable downShadowDrawable;
    private String currentSourceKey = null;
    private Map<String, List<DownLoadInfo>> movieDownUrlmap;
    private int num;
    private int position;
    public static String PAUSE_ACTION = "pause_alldownloadtask";
    public static String delete_SourceTag;

    public CustomDialog() {
    }

    public CustomDialog(Context context, View anchorView, List<DownTask> list, int x, int y, int position) {
        this.x = x;
        this.y = y;
        this.context = context;
        this.anchorView = anchorView;
        this.list = list;
        this.position = position;
        init();
        getLastDownloadInfo();
        initView();


    }

    public static CustomDialog getInstance() {
        if (instance == null) {
            instance = new CustomDialog();
        }
        return instance;
    }

    private void init() {
        inflater = LayoutInflater.from(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
        anchorView.getLocationOnScreen(location);
        childWidth = anchorView.getWidth();
        childHeight = anchorView.getHeight();


    }


    private void initView() {
        dialogview = inflater.inflate(R.layout.download_info, null);
        LinearLayout main_layout = (LinearLayout) dialogview.findViewById(R.id.layout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) main_layout.getLayoutParams();
        params.width = (int) (screenWidth * 0.9);
        params.height = (int) ((screenHeight - 60) * 0.45);
        main_layout.setLayoutParams(params);
        top_triangle = (ImageView) dialogview.findViewById(R.id.top_triangle);
        bottom_triangle = (ImageView) dialogview.findViewById(R.id.bottom_triangle);

        movie_name = (TextView) dialogview.findViewById(R.id.movie_name);
        movie_name.setText(movieName);
        allpause = (TextView) dialogview.findViewById(R.id.allpause);
        edit = (TextView) dialogview.findViewById(R.id.edit);
        add = (TextView) dialogview.findViewById(R.id.add);
        detailGrid = (GridView) dialogview.findViewById(R.id.movie_download_detail);
        adapter = new DownloadDetailAdapter(context, list, position);
        updateDetialGridAdapter();
        adapter.setOnEditListener(listener);
        detailGrid.setAdapter(adapter);
        detailGrid.setOnItemClickListener(this);
        allpause.setOnClickListener(this);
        edit.setOnClickListener(this);
        add.setOnClickListener(this);

        //弹出的详情框的界面布局
        download_view = (RelativeLayout) dialogview.findViewById(R.id.main_container);
        current_sourceIcon = (ImageView) download_view.findViewById(R.id.current_sourceIcon);
        current_sourceTag = (TextView) download_view.findViewById(R.id.current_sourceTag);
        current_sourceTag.setText(lastSourcTag);
        sourceIcon_arrowdown = (ImageView) download_view.findViewById(R.id.source_arrowdown);
        moviedetial_descbtn = (Button) download_view.findViewById(R.id.moviedetial_descbtn);
        upShadowDrawable = context.getResources().getDrawable(R.drawable.moviedetial_arraw_up);
        upShadowDrawable.setBounds(0, 0, upShadowDrawable.getMinimumWidth(), upShadowDrawable.getIntrinsicHeight());
        downShadowDrawable = context.getResources().getDrawable(R.drawable.moviedetial_arraw_down);
        downShadowDrawable.setBounds(0, 0, downShadowDrawable.getMinimumWidth(),
                downShadowDrawable.getIntrinsicHeight());
        current_sourceIcon.setOnClickListener(this);
        current_sourceTag.setOnClickListener(this);
        sourceIcon_arrowdown.setOnClickListener(this);
        moviedetial_descbtn.setOnClickListener(this);
        //初始化影片来源
        source_gridview = (MyGridView) download_view.findViewById(R.id.source_gridview);
        source_adapter = MovieDetialSourceAdapter.getInstance(context);
        source_gridview.setAdapter(source_adapter);
        source_gridview.setOnItemClickListener(this);
        //初始化影片剧集
        position_gridview = (MyGridView) download_view.findViewById(R.id.position_gridview);
        detial_adapter = MovieDetialGridAdapter.getInstance(context);
        position_gridview.setAdapter(detial_adapter);
        position_gridview.setOnItemClickListener(this);

        reLocation();


    }

    private void updateDetialGridAdapter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // 更新适配器 一秒更新一次
                    Log.d(AppConfig.Tag, "下载 界面更新适配器");
                    CommonUtil.sendMessage(UPDATE_DETIALGRIADAPTER, handler, null);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    private void getLastDownloadInfo() {
        String movieID = null;
        int len = list.size();
        DownLoadInfo downLoadInfo = list.get(len - 1).getDownLoadInfo();
        if (downLoadInfo != null) {
            movieName = downLoadInfo.getDownloadName();
            lastSourcTag = downLoadInfo.getDownloadSourceTag();
            delete_SourceTag = lastSourcTag;
            movieID = downLoadInfo.getDownloadID();
        }
        new GetMoviesInfo(handler).execute(movieID);
    }

    public void refreshUI() {
        detailGrid.invalidate();
    }


    private String requestUrl(String movieID) {
        StringBuffer stringBuffer = new StringBuffer();
        if (TextUtils.isEmpty(movieID) || !TextUtils.isDigitsOnly(movieID)) {
            return "";
        }
        int subId = Integer.parseInt(movieID);
        stringBuffer.append(AppConfig.GET_MOVIEDETIALINFO_URL);
        stringBuffer.append("" + (subId / 1000) + "/");
        stringBuffer.append("" + movieID + "_info.js");
        return stringBuffer.toString();
    }

    public void setList(List<DownTask> list) {
        this.list = list;
    }

    public List<DownTask> getList() {
        return list;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.allpause:
                if (isEidtable) {
                    if (num != 0) {
                        adapter.del();
                        num = 0;
                        updateEditText(0);

                    }
                } else {
                    sendPauseBroad();
                }
                break;
            case R.id.edit:
                isEidtable = !isEidtable;
                changeEditState(isEidtable);
                adapter.notifyEditable(isEidtable);
                break;
            case R.id.add:
                isAdd = !isAdd;
                showOrhideDownloadMenu(isAdd);
                break;
            case R.id.current_sourceIcon:
            case R.id.current_sourceTag:
            case R.id.source_arrowdown:
                isExpand = !isExpand;
                showOrhideMovieSources(isExpand);
                break;
            case R.id.moviedetial_descbtn:
                sortByDesc();
                break;

        }

    }

    private static final int ALL_PAUSE = 8;

    private void sendPauseBroad() {
        Intent pause_intent = new Intent();
        pause_intent.setAction(PAUSE_ACTION);
        context.sendBroadcast(pause_intent);


    }


    private void showOrhideDownloadMenu(boolean isAdd) {
        if (isAdd) {
            allpause.setText("");
            edit.setText("");
            add.setText("返回");
            download_view.setVisibility(View.VISIBLE);
        } else {
            allpause.setText("全部暂停");
            edit.setText("编辑");
            add.setText("+");
            download_view.setVisibility(View.GONE);
        }

    }

    private void changeEditState(boolean canEdit) {
        String edit_str = canEdit ? "取消" : "编辑";
        String pause_str = canEdit ? "删除(0)" : "全部暂停";
        edit.setText(edit_str);
        allpause.setText(pause_str);
        if (!canEdit) {
            adapter.resetEditState();
            num = 0;
        }

    }

    private void showOrhideMovieSources(boolean canExpand) {
        int expand = canExpand ? View.VISIBLE : View.GONE;
        source_gridview.setVisibility(expand);
        if (canExpand) {
            sourceIcon_arrowdown.setImageResource(R.drawable.moviedetial_sourceshadow_up);
        } else {
            sourceIcon_arrowdown.setImageResource(R.drawable.moviedetial_sourceshadow_down);
        }
    }

    private void sortByDesc() {
        if (!detial_adapter.isDesc()) {
            moviedetial_descbtn.setText("倒序");
            moviedetial_descbtn.setCompoundDrawables(downShadowDrawable, null, null, null);
        } else {
            moviedetial_descbtn.setText("正序");
            moviedetial_descbtn.setCompoundDrawables(upShadowDrawable, null, null, null);
        }
        detial_adapter.notifyDataSetChangeByDesc();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == source_gridview) {
            SourceBean sourceBean = source_adapter.getList().get(position);
            if (!currentSourceKey.equals(sourceBean.getKey())) {
                current_sourceIcon.setImageResource(sourceBean.getValues());
                currentSourceKey = sourceBean.getKey();
                current_sourceTag.setText(currentSourceKey);
                delete_SourceTag = currentSourceKey;
                detial_adapter.setCurrentSourceKey(currentSourceKey);
                List<DownLoadInfo> downloadInfo = movieDownUrlmap.get(currentSourceKey);
                int len = downloadInfo.size();
                for (int i = 0; i < len; i++) {
                    downloadInfo.get(i).setSourceIcon(sourceBean.getValues());
                }
                detial_adapter.setList(downloadInfo);
                detial_adapter.notifyDataSetChanged();
                isExpand = false;
                sourceIcon_arrowdown.setImageResource(R.drawable.moviedetial_sourceshadow_down);
                source_gridview.setVisibility(View.GONE);
            }

        } else if (parent == position_gridview) {


        }

    }

    DownloadDetailAdapter.DownloadEditInterface listener = new DownloadDetailAdapter.DownloadEditInterface() {
        @Override
        public void downloadStateAdd() {
            num++;
            updateEditText(num);
        }

        @Override
        public void downloadStateRemovie() {
            num--;
            updateEditText(num);
        }
    };

    private void updateEditText(int num) {
        allpause.setText("删除(" + num + ")");
    }


    private void reLocation() {
        dialog = new Dialog(context, R.style.dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                DownloadDetailAdapter.getInstance().notifyDownloadDetialAdapter();
            }
        });
        dialog.setContentView(dialogview);
        position_x = location[0] + childWidth / 2 - 20 - (int) (0.05 * screenWidth);
        Window window = dialog.getWindow();
        if (location[1] < screenHeight / 2) {
            top_triangle.setVisibility(View.VISIBLE);
            bottom_triangle.setVisibility(View.GONE);
            top_triangle.setX(position_x);
            position_y = location[1] + (int) (childHeight * 0.3f);
            WindowManager.LayoutParams lp = window.getAttributes();
            window.setGravity(Gravity.LEFT | Gravity.TOP);
            lp.x = (int) (0.05f * screenWidth);
            lp.y = position_y;
            window.setAttributes(lp);
        } else {
            bottom_triangle.setVisibility(View.VISIBLE);
            top_triangle.setVisibility(View.GONE);
            bottom_triangle.setX(position_x);
            position_y = (int) (childHeight * 0.3f);
            WindowManager.LayoutParams lp = window.getAttributes();
            window.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
            lp.x = (int) (0.05f * screenWidth);
            lp.y = position_y;
            window.setAttributes(lp);

        }


        dialog.show();

    }


    public void show() {
        dialog.show();
    }

    private void dismiss() {
        dialog.dismiss();
    }


    class GetMoviesInfo extends AsyncTask<String, Integer, Map<String, List<DownLoadInfo>>> {
        private Handler handler;

        public GetMoviesInfo(Handler handler) {
            this.handler = handler;
            movieInfo = new MovieInfo();
        }

        @Override
        protected Map<String, List<DownLoadInfo>> doInBackground(String... params) {
            String movidID = params[0];
            Map<String, List<DownLoadInfo>> datalist = JsonUtil.getMovieDownLoadList(movidID, movieInfo);
            return datalist;
        }

        @Override
        protected void onPostExecute(Map<String, List<DownLoadInfo>> stringListMap) {
            Message message = handler.obtainMessage();
            message.obj = stringListMap;
            message.what = GET_MOVIE_SOURCES;
            handler.sendMessage(message);

        }


    }


    public Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {

                case GET_MOVIE_SOURCES:// 影片剧集
                    movieDownUrlmap = (Map<String, List<DownLoadInfo>>) message.obj;
                    if (movieDownUrlmap == null || movieDownUrlmap.keySet() == null || movieDownUrlmap.keySet().size() == 0) {
                        return;
                    }
                    movieInfo.setMovieDownUrlmap(movieDownUrlmap);


                    if (movieInfo.getCurrentMovieDetialSources() != null
                            && movieInfo.getCurrentMovieDetialSources().size() != 0) {
//                        currentSourceKey = movieInfo.getCurrentMovieDetialSources().get(0).getKey();
                        currentSourceKey = lastSourcTag;
                        movieInfo.setCurrentSourceKey(currentSourceKey);
                        current_sourceIcon.setImageResource(movieInfo.getCurrentMovieDetialSources().get(0)
                                .getValues());
//                        if(!fromDownload)
//                            currentSourceTextView.setText(currentSourceKey);
                    }
                    // 来源

                    source_adapter.setList(movieInfo.getCurrentMovieDetialSources());
                    adapter.setSourceBeanList(movieInfo.getCurrentMovieDetialSources());
                    source_adapter.notifyDataSetChanged();
                    // 剧集

                    detial_adapter.setCurrentSourceKey(currentSourceKey);
                    List<SourceBean> sourceBeans = movieInfo.getCurrentMovieDetialSources();
                    int size = sourceBeans.size();
                    for (int i = 0; i < size; i++) {
                        if (sourceBeans.get(i).getKey().equals(lastSourcTag)) {
                            current_sourceIcon.setImageResource(sourceBeans.get(i).getValues());
                        }
                    }
                    List<DownLoadInfo> downloadInfo = movieDownUrlmap.get(currentSourceKey);
                    int len = downloadInfo.size();
                    for (int i = 0; i < len; i++) {
                        downloadInfo.get(i).setSourceIcon(movieInfo.getCurrentMovieDetialSources().get(0)
                                .getValues());
                    }
                    detial_adapter.setList(downloadInfo);
                    detial_adapter.setMovieInfo(movieInfo);
                    detial_adapter.notifyDataSetChangeByIsDownload(true);
                    detial_adapter.notifyDataSetChanged();
                    break;
                case UPDATE_DETIALGRIADAPTER:
                    adapter.notifyDataSetChanged();
                    detailGrid.invalidate();
                    break;

            }
        }
    };


}
