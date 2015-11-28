package cn.pipi.mobile.pipiplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.pipi.mobile.pipiplayer.AdDetailActivity;
import cn.pipi.mobile.pipiplayer.bean.AdInfo;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.view.MyGridView;

public class HomePagerListAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;

    private Map<String, List<MovieInfo>> map;

    private Context context;

    private String titels[];

    private selectMoreInterFace selectMoreInterFace;

    private List<ViewHolder> viewHolders;

    private int itemWidth;

    private int itemHeight;

    private int itemImageHeight;

    public Map<String, List<MovieInfo>> getMap() {
        return map;
    }

    public void setMap(Map<String, List<MovieInfo>> map) {
        this.map = map;
    }

    public HomePagerListAdapter(Context context) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        titels = context.getResources().getStringArray(R.array.homepager_titelgroup);
        viewHolders = new ArrayList<HomePagerListAdapter.ViewHolder>();
    }

    public HomePagerListAdapter(Fragment fragment) {
        this.context = fragment.getActivity();
        selectMoreInterFace = (cn.pipi.mobile.pipiplayer.adapter.HomePagerListAdapter.selectMoreInterFace) fragment;
        mLayoutInflater = LayoutInflater.from(context);
        titels = context.getResources().getStringArray(R.array.homepager_titelgroup);
        viewHolders = new ArrayList<HomePagerListAdapter.ViewHolder>();
    }

    @Override
    public int getCount() {

        int count = 0;

        count = map != null && map.keySet().size() != 0 ? map.keySet().size() - 1 : 0;

        return count;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolders.add(viewHolder);
        } else {
            viewHolder = new ViewHolder();
            viewHolder.homePagerAdapter = new HomePagerAdapter(context);
            convertView = mLayoutInflater.inflate(R.layout.homepager_item, null);
            viewHolder.titelView = (TextView) convertView.findViewById(R.id.homepager_titel);
            viewHolder.moreImgBtn = (ImageButton) convertView.findViewById(R.id.homepager_more);
            viewHolder.gridView = (MyGridView) convertView.findViewById(R.id.homepager_gridview);
            viewHolder.gridView.setAdapter(viewHolder.homePagerAdapter);
            convertView.setTag(viewHolder);
        }
        viewHolder.moreImgBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                if (selectMoreInterFace != null) {
                    selectMoreInterFace.selectMore(position);
                }
            }
        });
        viewHolder.gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int gridPosition, long arg3) {
                MovieInfo movieInfo = map.get(AppConfig.homePagerKeys[position]).get(gridPosition);
                if (movieInfo.isAd()) {
                    AdInfo info = (AdInfo) movieInfo;
                    Intent intent = new Intent(context, AdDetailActivity.class);
                    intent.putExtra("name", info.getAppName());
                    intent.putExtra("url", info.getHomePageUrl());
                    context.startActivity(intent);
                } else {
                    MobclickAgent.onEvent(context, "Click_Shouye", titels[position]);
                    CommonUtil.toMovieDetialActivity(context, movieInfo.getMovieID(), movieInfo.getMovieName());
                }
            }

        });
        if (map.keySet().size() != 0) {
            if (map.containsKey(AppConfig.homePagerKeys[position])
                    && map.get(AppConfig.homePagerKeys[position]) != null
                    && map.get(AppConfig.homePagerKeys[position]).size() != 0) {
                viewHolder.titelView.setText(titels[position]);
                viewHolder.homePagerAdapter.setList(map.get(AppConfig.homePagerKeys[position]));
                viewHolder.homePagerAdapter.notifyDataSetChanged();
            }
        }
        return convertView;
    }

    public class ViewHolder {

        TextView titelView;

        ImageButton moreImgBtn;

        MyGridView gridView;

        HomePagerAdapter homePagerAdapter;

        public HomePagerAdapter getHomePagerAdapter() {
            return this.homePagerAdapter;
        }
    }

    /**
     * 点击更多 接口
     *
     * @author qiny
     */
    public interface selectMoreInterFace {
        public void selectMore(int position);
    }

    public List<ViewHolder> getViewHolders() {
        return this.viewHolders;
    }

    public void setInitViewData(int itemWidth, int itemHeight, int itemImageHeight) {
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
        this.itemImageHeight = itemImageHeight;
    }
}
