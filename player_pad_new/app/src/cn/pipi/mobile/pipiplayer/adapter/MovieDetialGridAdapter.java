package cn.pipi.mobile.pipiplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.List;

import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.DownTask;
import cn.pipi.mobile.pipiplayer.HtmlPlayer;
import cn.pipi.mobile.pipiplayer.MovieDetialActivity;
import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.bean.HistoryBean;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.fragment.PlayerFragment;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.ToastUtil;
import cn.pipi.mobile.pipiplayer.view.CustomDialog;

/**
 * 影片详情 剧集下载适配器
 *
 * @author qiny
 */
public class MovieDetialGridAdapter extends BaseAdapter {

    private Context context;

    private LayoutInflater layoutInflater;

    private final int textViewColor[] = {R.color.black, R.color.red};

    private final int convertViewBg[] = {
            R.drawable.moviedetial_gridview_itembg,
            R.drawable.moviedetial_gridview_item_prebg,
            R.drawable.moviedetial_gridview_item_downbg};

    private List<DownLoadInfo> list;

    boolean isDownload; // 播放or下载

    boolean isDesc; // 升序或降序

    public String currentSourceKey;// 当前来源 皮皮 爱奇艺...

    public String movieName;

    private String movieID;

    private String movieImgUrl;

    private ColorStateList redColorStateList;

    private ColorStateList blackColorStateList;

    private ColorStateList whiteColorStateList;

    private DBHelperDao dbHelperDao;


    private MovieInfo movieInfo;

    private ImageView playimageView; // 详情界面 播放按钮隐藏用

    private int currentPlayPosition; // 当前播放下标

    private boolean fromDownload = false; //判读是否是来自下载页面

    private int currentPosition = 0; //设置显示下载的是第几集

    public int getCurrentPlayPosition() {
        return currentPlayPosition;
    }

    public void setCurrentPlayPosition(int currentPlayPosition) {
        this.currentPlayPosition = currentPlayPosition;
    }

    public ImageView getPlayimageView() {
        return playimageView;
    }

    public void setPlayimageView(ImageView playimageView) {
        this.playimageView = playimageView;
    }

    public MovieInfo getMovieInfo() {
        return movieInfo;
    }

    public void setMovieInfo(MovieInfo movieInfo) {
        this.movieInfo = movieInfo;
    }

    public String getMovieImgUrl() {
        return movieImgUrl;
    }

    public void setMovieImgUrl(String movieImgUrl) {
        this.movieImgUrl = movieImgUrl;
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getCurrentSourceKey() {
        return currentSourceKey;
    }

    public void setCurrentSourceKey(String currentSourceKey) {
        this.currentSourceKey = currentSourceKey;
    }

    public boolean isDesc() {
        return isDesc;
    }

    public void setDesc(boolean isDesc) {
        this.isDesc = isDesc;
    }

    public List<DownLoadInfo> getList() {
        return list;
    }

    public void setList(List<DownLoadInfo> list) {
        this.list = list;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }

    public void isFromDownload(boolean fromDownload) {
        this.fromDownload = fromDownload;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
    private static MovieDetialGridAdapter instance;
    public MovieDetialGridAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        dbHelperDao = DBHelperDao.getDBHelperDaoInstace(context);
        setDownload(false);
        setDesc(false);// 默认为降序
        Resources resource = (Resources) context.getResources();
        blackColorStateList = (ColorStateList) resource
                .getColorStateList(R.color.black);
        redColorStateList = (ColorStateList) resource
                .getColorStateList(R.color.red);
        whiteColorStateList = (ColorStateList) resource
                .getColorStateList(R.color.white);
    }
    public static MovieDetialGridAdapter getInstance(Context context){
        if(instance==null){
            instance=new MovieDetialGridAdapter(context);
        }
        return instance;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list != null && list.size() != 0 ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /**
     * 升序or降序
     */
    public void notifyDataSetChangeByDesc() {
        if (getList() == null || getList().size() == 0)
            return;
        if (isDesc()) {
            setDesc(false);
        } else {
            setDesc(true);
        }
        notifyDataSetChanged();
    }

    /**
     * 切换播放状态和下载状态
     *
     * @param isDownload
     */
    public void notifyDataSetChangeByIsDownload(boolean isDownload) {
        if (getList() == null || getList().size() == 0)
            return;
        setDownload(isDownload);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(
                    R.layout.moviedetial_gridview_item, null);
            viewHolder.textview = (TextView) convertView
                    .findViewById(R.id.moviedetial_gridviewitem_textview);
            convertView.setTag(viewHolder);
        }
        if (getList() != null && getList().size() != 0) {
            int tmpPosition;
            if (isDesc) {
                tmpPosition = list.size() - position;
                viewHolder.textview.setText("" + (list.size() - position));
            } else {
                tmpPosition = position + 1;
                viewHolder.textview.setText("" + (position + 1));
            }
            DownLoadInfo downLoadInfo = getList().get(tmpPosition - 1);
            downLoadInfo.setDownloadPosition(tmpPosition - 1);
                 if (!isDownload()) { // 播放状态下 默认背景颜色
                    if (getCurrentPlayPosition() != (tmpPosition - 1)) {
                        viewHolder.textview.setTextColor(blackColorStateList);
                        convertView.setBackgroundResource(convertViewBg[0]);
                    } else {
                        viewHolder.textview.setTextColor(redColorStateList);
                        convertView.setBackgroundResource(convertViewBg[1]);
                    }
                } else {// 下载状态下
                    if (DBHelperDao.getDBHelperDaoInstace(context)
                            .isFirstInsertToDownloadTable(
                                    downLoadInfo.getDownAddress())) {
                        viewHolder.textview.setTextColor(blackColorStateList);
                        convertView.setBackgroundResource(convertViewBg[0]);
                    } else {
                        viewHolder.textview.setTextColor(whiteColorStateList);
                        convertView.setBackgroundResource(convertViewBg[2]);
                    }

                }
            // convertViewOntouch(convertView, viewHolder);
            convertViewOnclick(convertView, viewHolder, downLoadInfo);

        }
        return convertView;
    }

    private void convertViewOntouch(final View convertView,
                                    final ViewHolder viewHolder) {
        // System.out.println("**********ontouch*******");
        convertView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (isDownload()) { // 下载状态下返回
                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    viewHolder.textview.setTextColor(redColorStateList);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    viewHolder.textview.setTextColor(blackColorStateList);
                }
                return false;
            }
        });
    }

    ;

    /**
     * convertview onclick
     *
     * @author qiny
     */

    private void convertViewOnclick(final View convertView,
                                    final ViewHolder viewHolder, final DownLoadInfo downLoadInfo) {
        if (downLoadInfo == null
                || TextUtils.isEmpty(downLoadInfo.getDownAddress()))
            return;
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isDownload()) { // 下载状态
                    if (DBHelperDao.getDBHelperDaoInstace(context)
                            .isFirstInsertToDownloadTable(
                                    downLoadInfo.getDownAddress())) {
                        ToastUtil.ToastShort(context, "添加到下载");
                        viewHolder.textview.setTextColor(whiteColorStateList);
                        convertView.setBackgroundResource(convertViewBg[2]);
                        startDownload(downLoadInfo);

                    } else {

                        //先删除list中的任务，再删除数据库中的任务
                        DownloadDetailAdapter detailAdapter=DownloadDetailAdapter.getInstance();
                        detailAdapter.remove(downLoadInfo.getDownloadPosition(), CustomDialog.isAdd);
                        ToastUtil.ToastShort(context, "移除下载任务");
                        DBHelperDao.getDBHelperDaoInstace(context)
                                .deleteDownloadTaskFromMovieURL(
                                        downLoadInfo.getDownAddress());
                        DownCenter.getExistingInstance().delDownTask(
                                downLoadInfo.getDownAddress());
                        viewHolder.textview.setTextColor(blackColorStateList);
                        convertView.setBackgroundResource(convertViewBg[0]);


                    }
                } else {// 播放状态
                    playVideo(downLoadInfo);
                }

            }
        });
    }

    public void playVideo(DownLoadInfo downLoadInfo) {
        ((MovieDetialActivity) context).goneAd();
        if (downLoadInfo == null
                || TextUtils.isEmpty(downLoadInfo.getDownAddress()))
            return;
        this.setCurrentPlayPosition(downLoadInfo.getDownloadPosition());
        this.notifyDataSetChanged();
        HistoryBean historyBean = new HistoryBean();
        historyBean.setMovieId(movieID);
        historyBean.setMovieName(movieName);
        historyBean.setPlayPosition(downLoadInfo.getDownloadPosition());
        historyBean.setSourceTag(currentSourceKey);
        historyBean.setWatchedDate(CommonUtil.getCurrentDate());
        historyBean.setMovieUrl(downLoadInfo.getDownAddress());
        if (dbHelperDao.isFirstToPlayHistoryTable(movieID)) {
            dbHelperDao.insertToPlayHistoryTable(historyBean);
        } else {
            dbHelperDao.updatePlayHistoryName(movieID, historyBean);
        }
        downLoadInfo.setDownloadID(movieID);
        downLoadInfo.setDownloadName(movieName);
        downLoadInfo.setDownloadSourceTag(currentSourceKey);
        getMovieInfo().setCurrentSourceKey(currentSourceKey);
        if (CommonUtil.isPolySource(downLoadInfo.getDownAddress())) {
            MobclickAgent.onEvent(context, "Click_Movie_Play", movieID);
            Intent intent = new Intent();
            intent.setClass(context, HtmlPlayer.class);
            intent.putExtra("playurl", downLoadInfo.getDownAddress());
            context.startActivity(intent);
            return;
        }
        if (getPlayimageView() != null
                && getPlayimageView().getVisibility() == View.VISIBLE) {
            getPlayimageView().setVisibility(View.GONE);
        }
        MobclickAgent.onEvent(context, "Click_Movie_Play", movieID);
        FragmentManager fragmentManager = ((FragmentActivity) context)
                .getSupportFragmentManager();
        FragmentTransaction ftTransaction = fragmentManager.beginTransaction();
        PlayerFragment playerFragment = new PlayerFragment();
        Bundle bundle = new Bundle();
        if (getMovieInfo() != null)
            bundle.putSerializable("movieinfo", getMovieInfo());
        bundle.putSerializable("downloadinfo", downLoadInfo);
        bundle.putBoolean("isFullScreen", false);
        playerFragment.setArguments(bundle);
        ftTransaction.replace(R.id.moviedetial_frame_player, playerFragment);
        ftTransaction.commit();
    }

    private void startDownload(final DownLoadInfo downLoadInfo) {
        if (!TextUtils.isEmpty(movieID))
            MobclickAgent.onEvent(context, "Click_Movie_Down", movieID);
        new Thread(new Runnable() {

            @Override
            public void run() {

                DownLoadInfo tmpDownLoadInfo = new DownLoadInfo();
                tmpDownLoadInfo.setDownloadID(movieID);
                tmpDownLoadInfo.setDownloadSourceTag(currentSourceKey);
                tmpDownLoadInfo.setDownloadName(movieName);
                tmpDownLoadInfo.setDownloadPosition(downLoadInfo
                        .getDownloadPosition());
                tmpDownLoadInfo.setDownAddress(downLoadInfo.getDownAddress());
                tmpDownLoadInfo.setDownloadImgPath(movieImgUrl);
                tmpDownLoadInfo.setSourceIcon(downLoadInfo.getSourceIcon());
                DBHelperDao.getDBHelperDaoInstace(context)
                        .insertToDownloadTable(tmpDownLoadInfo);
                DownCenter.getExistingInstance().addJob(tmpDownLoadInfo);
                DownCenter.getExistingInstance().autoDown();

                DownloadDetailAdapter detailAdapter=DownloadDetailAdapter.getInstance();
                detailAdapter.add(new DownTask(tmpDownLoadInfo), CustomDialog.isAdd);


            }
        }).start();
    }

    class ViewHolder {
        TextView textview;
    }

}
