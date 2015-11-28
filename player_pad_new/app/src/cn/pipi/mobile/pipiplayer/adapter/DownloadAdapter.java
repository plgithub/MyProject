package cn.pipi.mobile.pipiplayer.adapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.DownTask;
import cn.pipi.mobile.pipiplayer.PlayerActivity;
import cn.pipi.mobile.pipiplayer.bean.DeleteTaskBean;
import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.BitmapManager;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.view.CustomDialog;

public class DownloadAdapter extends BaseAdapter implements View.OnTouchListener {

    private Context context;

    private LayoutInflater mLayoutInflater;

    private List<DownTask> list;

    private boolean isEditBtnShow = false;
    private static DownloadAdapter instance;
    private DownloadEditInterface downloadEditInterface;
    public static List<List<DownTask>> sorttask;
    public static String action_updateadapter_del = "action.intent.update_downloadadapter_delete";
    public static String action_updateadapter_add = "action.intent.update_downloadadapter_add";
    public static String action_unregister = "action.intent.unregister_receiver";
    private UpdateAdapterReceiver receiver;

    public List<DownTask> getList() {
        return list;
    }

    public void setList(List<DownTask> list) {
        this.list = list;
    }

    public void setSortedMovie(List<List<DownTask>> sorttask) {
        this.sorttask = sorttask;
    }

    public List<List<DownTask>> getSortedMovie() {
        return sorttask;
    }

    public boolean isEditBtnShow() {
        return isEditBtnShow;
    }

    public void setEditBtnShow(boolean isEditBtnShow) {
        this.isEditBtnShow = isEditBtnShow;
    }

    public DownloadAdapter() {

    }

    public static DownloadAdapter getInstance() {
        if (instance == null) {
            instance = new DownloadAdapter();
        }
        return instance;
    }


    public DownloadAdapter(Context context) {
        this.context = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        setEditBtnShow(false);
        downloadEditInterface = (DownloadEditInterface) context;
        initData();

    }

    public void register() {
        receiver = new UpdateAdapterReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(action_updateadapter_del);
        context.registerReceiver(receiver, filter);
    }

    public void unregister(Context context) {
        context.unregisterReceiver(receiver);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        int size = 0;
        if (getSortedMovie() != null)
            size = getSortedMovie().size();
        return size;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mLayoutInflater.inflate(R.layout.download_gridview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.download_framelayout = (FrameLayout) convertView.findViewById(R.id.download_framelayout);
            viewHolder.imgView = (ImageView) convertView.findViewById(R.id.download_downimg);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.download_downname);
            viewHolder.downinfoView = (TextView) convertView.findViewById(R.id.download_downinfo);
            viewHolder.checkMovieInfoBtn = (Button) convertView.findViewById(R.id.download_checkdetialbtn);
            viewHolder.downPostionView = (TextView) convertView.findViewById(R.id.download_downposition);
            viewHolder.downSpeedView = (TextView) convertView.findViewById(R.id.download_downspeed);
            viewHolder.downStateView = (TextView) convertView.findViewById(R.id.download_downstate);
            viewHolder.editView = (ImageView) convertView.findViewById(R.id.download_edit);
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(this.itemWidth, this.itemHeight);
            convertView.setLayoutParams(layoutParams);
            ViewGroup.LayoutParams params1 = viewHolder.imgView.getLayoutParams();
            params1.height = (int) (itemHeight * 0.65f);
            viewHolder.imgView.setLayoutParams(params1);
            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) viewHolder.download_framelayout.getLayoutParams();
            params2.height = (int) (itemHeight * 0.65f);
            viewHolder.download_framelayout.setLayoutParams(params2);
            convertView.setTag(viewHolder);
            setImgViewWidthAndHeight(viewHolder.imgView);
        }


        viewHolder.checkMovieInfoBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                DownLoadInfo downLoadInfo = getList().get(position).getDownLoadInfo();
                if (downLoadInfo != null) {
                    String index = String.valueOf(downLoadInfo.getDownloadPosition());
                    String sourceTag = downLoadInfo.getDownloadSourceTag();
                    CommonUtil.toMovieDetialActivity(context, downLoadInfo.getDownloadID(),
                            downLoadInfo.getDownloadName(), "download", index, sourceTag);

                }
            }
        });
        if (isEditBtnShow()) {
            viewHolder.editView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.editView.setVisibility(View.GONE);
        }
        if (getList() != null && getList().size() != 0) {
            List<DownTask> downtask = getSortedMovie().get(position);
            List<DownTask> list = sorttask.get(position);
            int total = list.size();
            if (total != 0) {
                DownLoadInfo downLoadInfo = downtask.get(0).getDownLoadInfo();
                if (downLoadInfo != null) {
                    BitmapManager.getInstance().loadBitmap(downLoadInfo.getDownloadImgPath(), viewHolder.imgView, null, 190,
                            155);
                    if (isEditBtnShow()) {
                        if (downLoadInfo.isEditState()) {
                            viewHolder.editView.setImageResource(R.drawable.download_downlist_select_press);
                        } else {
                            viewHolder.editView.setImageResource(R.drawable.download_downlist_select_normol);
                        }
                    }
                    viewHolder.editView.setOnClickListener(new EditImgViewOnclick(position, viewHolder));
//                    if (downLoadInfo.getDownloadCount() != 0) {
                    int count = 0;
                    for (int i = 0; i < total; i++) {
                        DownLoadInfo downLoadInfo1 = list.get(i).getDownLoadInfo();
                        if (downLoadInfo1 != null && downLoadInfo1.getDownloadState() == DownTask.TASK_FINISHED) {
                            count++;
                        }
                    }
                    viewHolder.downinfoView.setText("共" + total + "集,完成" + count + "集");
//                    }
                    viewHolder.nameTextView.setText(downLoadInfo.getDownloadName());
                    int speed = downLoadInfo.getDownloadSpeed();
                    if (speed != 0) {
                        viewHolder.downSpeedView.setText(speed / 1000 + "KB/s");
                    } else {
                        viewHolder.downSpeedView.setText("");
                    }
                    int state = downLoadInfo.getDownloadState();
                    switch (state) {
                        case DownTask.TASK_WAITING_DOWNLOAD:
                            viewHolder.downStateView.setText("等待中");
                            break;
                        case DownTask.TASK_DOWNLOADING:
                            viewHolder.downStateView.setText("" + downLoadInfo.getDownloadProgress() + "%");
                            break;
                        case DownTask.TASK_WIFI_ERROR:
                            System.out.println("getview 网络异常");
                            viewHolder.downSpeedView.setText("");
                            viewHolder.downStateView.setText("网络异常");
                            break;
                        case DownTask.TASK_PAUSE_DOWNLOAD:
                            viewHolder.downSpeedView.setText("");
                            viewHolder.downStateView.setText("暂停");
                            break;
                        case DownTask.TASK_RESUME_DOWNLOAD:
                            viewHolder.downSpeedView.setText("");
                            viewHolder.downStateView.setText("等待中");
                            break;
                        case DownTask.TASK_FileMerge:
                            viewHolder.downSpeedView.setText("");
                            viewHolder.downStateView.setText("合并文件");
                            break;
                        case DownTask.TASK_FINISHED:
                            viewHolder.downSpeedView.setText("");
                            viewHolder.downStateView.setText("完成");
                            break;
                    }
                }
            }
            convertView.setOnTouchListener(this);
            convertView.setId(position);
        }
        return convertView;
    }

    public List<List<DownTask>> sortMovie(List<DownTask> downTasks) {
        List<List<DownTask>> list = new ArrayList<List<DownTask>>();
        List<DownTask> alltask = downTasks;
        Set set = new HashSet();
        int len = alltask.size();
        if (downTasks != null && downTasks.size() != 0) {
            for (int i = 0; i < len; i++) {
                DownLoadInfo downLoadInfo = alltask.get(i).getDownLoadInfo();
                if (downLoadInfo != null)
                    set.add(downLoadInfo.getDownloadID());
            }

            Map<String, List<DownTask>> map = new HashMap<String, List<DownTask>>();
            for (Iterator it = set.iterator(); it.hasNext(); ) {
                if (it != null) {
                    String key = it.next().toString();
                    List<DownTask> task = new ArrayList<DownTask>();
                    for (int j = 0; j < len; j++) {
                        DownLoadInfo downLoadInfo = alltask.get(j).getDownLoadInfo();
                        if (downLoadInfo != null) {
                            String download_id = downLoadInfo.getDownloadID().toString();
                            if (download_id.equals(key))
                                task.add(alltask.get(j));
                        }
                    }
                    map.put(key, task);
                }
            }
            for (Object key : map.keySet()) {
                list.add(map.get(key));
            }
        }
        return list;

    }

    public void notifyDownloadDetialdel(int position) {
        sorttask.remove(position);
        notifyDataSetChanged();
    }

    public void notifyDownloadTask() {
        DownCenter mDownCenter = DownCenter.getExistingInstance();
        sortMovie(mDownCenter.getDownTaskList());
        notifyDataSetChanged();
    }

    private void setImgViewWidthAndHeight(ImageView imgview) {
        FrameLayout.LayoutParams layoutParams = (LayoutParams) imgview.getLayoutParams();
        layoutParams.width = CommonUtil.scaleWidgetWidth(190);
        layoutParams.height = CommonUtil.scaleWidgetHeight(175);
        imgview.setLayoutParams(layoutParams);
    }

    public void changeDownloadState(int position) {
        if (getList() == null || getList().size() == 0)
            return;
        final DownTask downTask = getList().get(position);
        if (downTask == null)
            return;
        final DownLoadInfo downLoadInfo = downTask.getDownLoadInfo();
        if (downLoadInfo == null)
            return;
        new Thread(new Runnable() {

            @Override
            public void run() {
                PlayerActivity.startPlayerActivity(context, downLoadInfo);
                /*int downloadState = downLoadInfo.getDownloadState();
                if (downloadState == DownTask.TASK_FINISHED) {
					PlayerActivity.startPlayerActivity(context, downLoadInfo);
					return;
				}
				if (downloadState == DownTask.TASK_WAITING_DOWNLOAD || downloadState == DownTask.TASK_DOWNLOADING
						|| downloadState == DownTask.TASK_RESUME_DOWNLOAD)// 暂停掉即将或者正在下载的任务
					downTask.pause();
				else if (downloadState == DownTask.TASK_PAUSE_DOWNLOAD)// 加入队列,而不是让他立即下载
				{
					downLoadInfo.setDownloadState(DownTask.TASK_RESUME_DOWNLOAD);
					DownCenter.getExistingInstance().autoDown();//
				}
				Log.i("DownCenter", "用户点击状态   name=" + downLoadInfo.getDownloadName() + ",state=" + downloadState);*/
            }
        }).start();
    }

    public void notifiyByEdit(boolean isEdit) {
        setEditBtnShow(isEdit);
        if (getSortedMovie() != null && getSortedMovie().size() != 0) {
            for (List<DownTask> list : getSortedMovie()) {
                for (DownTask downTask : list) {
                    DownLoadInfo downLoadInfo = downTask.getDownLoadInfo();
                    if (downLoadInfo != null)
                        downLoadInfo.setEditState(false);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                int y = (int) event.getY();
                int position = v.getId();
                List<DownTask> list = getSortedMovie().get(position);
                CustomDialog dialog = new CustomDialog(context, v, list, x, y, position);
                break;
        }
        return true;
    }

    class EditImgViewOnclick implements OnClickListener {

        private int position;

        ViewHolder viewHolder;

        public EditImgViewOnclick(int position, ViewHolder viewHolder) {
            this.position = position;
            this.viewHolder = viewHolder;

        }

        @Override
        public void onClick(View view) {
            if (getList() == null || getList().size() == 0)
                return;
            DeleteTaskBean delete_task = delete_list.get(position);
            if (!delete_task.getCanDelete()) {
                delete_task.setCanDelete(true);
                viewHolder.editView.setImageResource(R.drawable.download_downlist_select_press);
                if (downloadEditInterface != null) {
                    downloadEditInterface.downloadStateAdd();
                }
            } else {
                delete_task.setCanDelete(false);
                viewHolder.editView.setImageResource(R.drawable.download_downlist_select_normol);
                if (downloadEditInterface != null) {
                    downloadEditInterface.downloadStateRemovie();
                }
            }

        }

    }


    public void resetEditState() {
        int size = delete_list.size();
        for (int i = 0; i < size; i++) {
            delete_list.get(i).setCanDelete(false);
        }
    }

    public static List<DeleteTaskBean> delete_list = new ArrayList<DeleteTaskBean>();

    public void initDeleteTaskBean() {
        int len = getSortedMovie().size();
        for (int i = 0; i < len; i++) {
            DeleteTaskBean delete_task = new DeleteTaskBean();
            delete_task.setTaskId(i);
            delete_task.setDelList(getSortedMovie().get(i));
            delete_list.add(delete_task);
        }
    }

    //点击DownloadActivity页面的“删除”按钮删除下载的影片
    public void del() {
        List<List<DownTask>> tmpDownTasks = new ArrayList<List<DownTask>>();
        List<Integer> taskIds = new ArrayList<Integer>();
        int len = delete_list.size();
        for (int i = 0; i < len; i++) {
            DeleteTaskBean task = delete_list.get(i);
            if (task.getCanDelete()) {
                taskIds.add(task.getTaskId());
            }
        }
        for (int j = 0; j < taskIds.size(); j++) {
            delete_list.remove(j);
            tmpDownTasks.add(getSortedMovie().get(j));
            for (DownTask downTask : getSortedMovie().get(j)) {
                DownLoadInfo downLoadInfo = downTask.getDownLoadInfo();
                DBHelperDao.getDBHelperDaoInstace(context)
                        .deleteDownloadTaskFromMovieURL(
                                downLoadInfo.getDownAddress());
                DownCenter.getExistingInstance().delDownTask(
                        downLoadInfo.getDownAddress());
            }

        }

        getSortedMovie().removeAll(tmpDownTasks);
        notifyDataSetChanged();


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
        void downloadStateAdd();

        void downloadStateRemovie();
    }

    class UpdateAdapterReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(action_updateadapter_del)) {
                int position = intent.getIntExtra("position", 0);
                notifyDownloadDetialdel(position);
            } else if (intent.getAction().equals(action_updateadapter_add)) {
                notifyDataSetChanged();
            }
        }
    }


    class ViewHolder {
        FrameLayout download_framelayout;
        ImageView imgView;
        TextView nameTextView;
        Button checkMovieInfoBtn; // 查看详情
        TextView downinfoView; // 已完成第一集
        TextView downPostionView;// 当前下载的是第几集
        TextView downSpeedView; // 下载速度
        TextView downStateView;// 下载状态
        ImageView editView; // 编辑按钮

    }

    private int itemWidth, itemHeight;

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


}
