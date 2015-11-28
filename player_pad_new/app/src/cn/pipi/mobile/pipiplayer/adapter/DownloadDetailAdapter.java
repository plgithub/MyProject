package cn.pipi.mobile.pipiplayer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.pipi.mobile.pipiplayer.DownTask;
import cn.pipi.mobile.pipiplayer.DownloadActivity;
import cn.pipi.mobile.pipiplayer.PlayerActivity;
import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.bean.SourceBean;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.util.BitmapManager;
import cn.pipi.mobile.pipiplayer.view.CustomDialog;

/**
 * Created by admin on 2015/11/5.
 */
public class DownloadDetailAdapter extends BaseAdapter {
    private static List<DownTask> list;
    private Context context;
    private LayoutInflater inflater;
    private DownLoadInfo downLoadInfo;
    private ViewHolder holder;
    private boolean isEditable = false;
    private static DownloadDetailAdapter instance;
    private static int position;
    private List<SourceBean> sourceBeanList;
    private int itemWidth;
    private int itemHeight;

    public List<SourceBean> getSourceBeanList() {
        return sourceBeanList;
    }

    public void setSourceBeanList(List<SourceBean> sourceBeanList) {
        this.sourceBeanList = sourceBeanList;
    }

    public DownloadDetailAdapter() {

    }

    public DownloadDetailAdapter(Context context, List<DownTask> list, int position) {
        this.context = context;
        this.list = list;
        this.position = position;
        inflater = LayoutInflater.from(context);
        initData();
    }

    public void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        float scaleWidth = 0.15f;
        float scaleImageHeight = 0.25f;

        itemWidth = (int) (scaleWidth * screenWidth);
        itemHeight = (int) ((screenHeight - 60) * 0.4f);
    }


    public static DownloadDetailAdapter getInstance() {
        if (instance == null) {
            instance = new DownloadDetailAdapter();
        }
        return instance;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.download_detail_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        downLoadInfo = list.get(position).getDownLoadInfo();
        if (downLoadInfo != null) {
            BitmapManager.getInstance().loadBitmap(downLoadInfo.getDownloadImgPath(), holder.download_downimg, null, 190, 150);
            ClickPlayListener listener = new ClickPlayListener(position);
            holder.play_btn.setOnClickListener(listener);
            holder.bottom_layout.setOnClickListener(listener);
            holder.sourceIcon1.setImageResource(downLoadInfo.getSourceIcon());
            holder.sourceTag1.setText(downLoadInfo.getDownloadSourceTag());
            holder.download_position.setText("第" + (downLoadInfo.getDownloadPosition() + 1) + "集");
            setDownloadState(holder, downLoadInfo.getDownloadState());
            holder.download_progress.setText(downLoadInfo.getDownloadProgress() + "%");
            if (!list.get(position).getDownLoadInfo().isEditState())
                holder.download_edit.setImageResource(R.drawable.download_downlist_select_normol);
            else
                holder.download_edit.setImageResource(R.drawable.download_downlist_select_press);
            if (getEditable()) {
                holder.play_btn.setVisibility(View.GONE);
                holder.download_edit.setVisibility(View.VISIBLE);
                holder.download_edit.setOnClickListener(new EditImgViewOnclick(position, holder));

            } else {
                holder.play_btn.setVisibility(View.VISIBLE);
                holder.download_edit.setVisibility(View.GONE);


            }
        }
        ViewGroup.LayoutParams convertveiw_param=convertView.getLayoutParams();
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(itemWidth, (int) (itemHeight * 0.9));
        convertView.setLayoutParams(layoutParams);
        ViewGroup.LayoutParams img_layout = holder.download_downimg.getLayoutParams();
        img_layout.height = (int) (layoutParams.height * 0.7);
        holder.download_downimg.setLayoutParams(img_layout);
        RelativeLayout.LayoutParams frame_param = (RelativeLayout.LayoutParams) holder.download_framelayout.getLayoutParams();
        frame_param.height = (int) (layoutParams.height * 0.7);
        holder.download_framelayout.setLayoutParams(frame_param);
        return convertView;
    }

    public void resetEditState() {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            DownLoadInfo downLoadInfo = list.get(i).getDownLoadInfo();
            if (downLoadInfo != null)
                downLoadInfo.setEditState(false);
        }
        notifyDataSetChanged();
    }

    public void notifyEditable(boolean isEditable) {
        setEditable(isEditable);
        notifyDataSetChanged();


    }

    private void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    private boolean getEditable() {
        return isEditable;
    }

    private void setDownloadState(ViewHolder holder, int state) {
        switch (state) {
            case DownTask.TASK_WAITING_DOWNLOAD:
                holder.download_state.setText("等待中");
                break;
            case DownTask.TASK_DOWNLOADING:
                int speed = downLoadInfo.getDownloadSpeed();
                String downloadspeed = speed == 0 ? "" : "" + (speed / 1000) + "KB/s";
                holder.sourceTag2.setText(downloadspeed);
                holder.download_state.setText("" + downLoadInfo.getDownloadProgress() + "%");
                break;
            case DownTask.TASK_WIFI_ERROR:
                System.out.println("getview 网络异常");
                holder.download_progress.setText("");
                holder.download_state.setText("网络异常");
                break;
            case DownTask.TASK_PAUSE_DOWNLOAD:
                holder.download_progress.setText("");
                holder.speed.setText("");
//                holder.download_state.setText("暂停");
                holder.sourceTag2.setText("暂停");
                break;
            case DownTask.TASK_RESUME_DOWNLOAD:
                holder.download_progress.setText("");
                holder.download_state.setText("等待中");
                break;
            case DownTask.TASK_FileMerge:
                holder.download_progress.setText("");
                holder.download_state.setText("合并文件");
                break;
            case DownTask.TASK_FINISHED:
                holder.download_progress.setText("");
                holder.download_state.setText("完成");
                holder.sourceIcon2.setImageResource(downLoadInfo.getSourceIcon());
                holder.sourceTag2.setText(downLoadInfo.getDownloadSourceTag());
                holder.sourceIcon2.setVisibility(View.VISIBLE);
                holder.sourceTag2.setVisibility(View.VISIBLE);
                holder.sourceIcon1.setVisibility(View.GONE);
                holder.sourceTag1.setVisibility(View.GONE);
                holder.speed.setText("");
                break;
        }
    }


    class ClickPlayListener implements View.OnClickListener {
        private int index;

        public ClickPlayListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play_btn:
                    playMovie();
                    break;
                case R.id.bottom_layout:
                    int download_state = list.get(index).getDownLoadInfo().getDownloadState();
                    if (download_state != DownTask.TASK_FINISHED) {
                        holder.sourceTag2.setText("暂停");
                        sendPauseBroad(index);
                    } else if (download_state == DownTask.TASK_FINISHED) {
                        playMovie();
                    }
                    break;

            }
        }

        private void sendPauseBroad(int position) {
            Context mContext = VLCApplication.getAppContext();
            Intent pause_intent = new Intent();
            pause_intent.putExtra("download_id", list.get(position).getDownLoadInfo().getDownloadID());
            pause_intent.putExtra("position", position);
            pause_intent.setAction(DownloadActivity.SINGLE_PAUSE_ACTION);
            mContext.sendBroadcast(pause_intent);


        }


        private void playMovie() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DownLoadInfo downLoad_Info = list.get(index).getDownLoadInfo();
                    PlayerActivity.startPlayerActivity(context, downLoad_Info);
                }
            }).start();
        }
    }


    class ViewHolder {
        RelativeLayout itemview_layout;
        ImageView download_downimg;
        ImageView sourceIcon1;
        TextView sourceTag1;
        TextView download_state;
        TextView download_position;
        ImageView sourceIcon2;
        TextView sourceTag2;
        TextView speed;
        TextView download_progress;
        ImageView download_edit;
        ImageView play_btn;
        RelativeLayout bottom_layout;
        FrameLayout download_framelayout;

        public ViewHolder(View itemView) {
            itemview_layout = (RelativeLayout) itemView.findViewById(R.id.itemview_layout);
            download_downimg = (ImageView) itemView.findViewById(R.id.download_downimg);
            sourceIcon1 = (ImageView) itemView.findViewById(R.id.sourceIcon1);
            sourceTag1 = (TextView) itemView.findViewById(R.id.sourceTag1);
            download_state = (TextView) itemView.findViewById(R.id.download_state);
            download_position = (TextView) itemView.findViewById(R.id.download_downposition);
            sourceIcon2 = (ImageView) itemView.findViewById(R.id.sourceIcon2);
            sourceTag2 = (TextView) itemView.findViewById(R.id.sourceTag2);
            speed = (TextView) itemView.findViewById(R.id.speed);
            download_progress = (TextView) itemView.findViewById(R.id.download_downspeed);
            download_edit = (ImageView) itemView.findViewById(R.id.download_edit);
            play_btn = (ImageView) itemView.findViewById(R.id.play_btn);
            bottom_layout = (RelativeLayout) itemView.findViewById(R.id.bottom_layout);
            download_framelayout = (FrameLayout) itemView.findViewById(R.id.download_framelayout);
        }


    }

    class EditImgViewOnclick implements View.OnClickListener {

        private int position;

        ViewHolder viewHolder;

        public EditImgViewOnclick(int position, ViewHolder viewHolder) {
            this.position = position;
            this.viewHolder = viewHolder;

        }

        @Override
        public void onClick(View view) {
            if (list == null || list.size() == 0)
                return;

            if (!list.get(position).getDownLoadInfo().isEditState()) {
                list.get(position).getDownLoadInfo().setEditState(true);
                viewHolder.download_edit.setImageResource(R.drawable.download_downlist_select_press);
                if (listener != null) {
                    listener.downloadStateAdd();
                }
            } else {
                list.get(position).getDownLoadInfo().setEditState(false);
                viewHolder.download_edit.setImageResource(R.drawable.download_downlist_select_normol);
                if (listener != null) {
                    listener.downloadStateRemovie();
                }
            }

        }

    }

    public void add(DownTask task, boolean canAdd) {
        if (canAdd) {
            list.add(task);
            notifyDataSetChanged();
        }

    }

    //点击弹出框的影片来源的集数删除下载影片
    public void remove(int delete_position, boolean canRemove) {
        DownTask delete_task = null;
        if (canRemove) {
            for (DownTask downTask : list) {
                DownLoadInfo downLoadInfo = downTask.getDownLoadInfo();
                if (downLoadInfo != null) {
                    String download_sourceTag = downLoadInfo.getDownloadSourceTag();
                    int download_position = downLoadInfo.getDownloadPosition();
                    if (CustomDialog.delete_SourceTag.equals(download_sourceTag) && delete_position == download_position) {
                        delete_task = downTask;
                    }
                }
            }
            list.remove(delete_task);
            notifyDataSetChanged();


        }
    }


    //点击弹出框的删除按钮删除下载详情框的影片
    public void del() {
        List<DownTask> tmpDownTasks = new ArrayList<DownTask>();
        if (list == null || list.size() == 0)
            return;
        for (DownTask downTask : list) {
            DownLoadInfo downLoadInfo = downTask.getDownLoadInfo();
            if (downLoadInfo != null && downLoadInfo.isEditState()) {
                tmpDownTasks.add(downTask);
            }
        }
        list.removeAll(tmpDownTasks);
        notifyDataSetChanged();
        for (DownTask downTask : tmpDownTasks) {
            deleteTask(downTask);

        }
    }

    //当详情框中的影片全部删除了之后发送一个广播更新DownloadActivity界面，将对应的影片删除
    public void notifyDownloadDetialAdapter() {
        Context mContext = VLCApplication.getAppContext();
        if (list != null && list.size() == 0) {
            Intent intent = new Intent();
            intent.putExtra("position", position);
            intent.setAction(DownloadAdapter.action_updateadapter_del);
            mContext.sendBroadcast(intent);
        }
    }

    private void deleteTask(final DownTask downTask) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                downTask.delete();
            }
        }).start();
    }

    public interface DownloadEditInterface {
        public void downloadStateAdd();

        public void downloadStateRemovie();
    }

    DownloadEditInterface listener;

    public void setOnEditListener(DownloadEditInterface listener) {
        this.listener = listener;
    }

}
