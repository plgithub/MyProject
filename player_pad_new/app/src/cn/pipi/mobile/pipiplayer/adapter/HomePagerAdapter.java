package cn.pipi.mobile.pipiplayer.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.Iterator;
import java.util.List;

import cn.pipi.mobile.pipiplayer.bean.AdInfo;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.hd.R;

public class HomePagerAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;

    private List<MovieInfo> list;

    private int convertViewWidth = 220;

    private int convertViewHeight = 362;

    private Context mContext;
    private DisplayImageOptions options;
    private ImageLoader mImageLoader;

    public List<MovieInfo> getList() {
        return list;
    }

    public void setList(List<MovieInfo> list) {
        this.list = list;
    }

    public HomePagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.item_moive_list)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.item_moive_list) // 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565).build();// 构建完成
        mImageLoader = ImageLoader.getInstance();
        initData();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        int count = list != null && list.size() != 0 ? list.size() : 0;
        count = count - count % 5;
        return count;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.homepager_gridview_item, null);
            viewHolder.movieImageView = (ImageView) convertView.findViewById(R.id.homepager_movieimgview);
            viewHolder.gradeView = (TextView) convertView.findViewById(R.id.homepager_moviegrade);
            viewHolder.updaTextView = (TextView) convertView.findViewById(R.id.homepager_movieupdataview);
            viewHolder.titelView = (TextView) convertView.findViewById(R.id.homepager_movietitelview);
            viewHolder.subTitelView = (TextView) convertView.findViewById(R.id.homepager_moviesubtitelview);
            viewHolder.adHint = (TextView) convertView.findViewById(R.id.ad_hint);
            convertView.setTag(viewHolder);
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(this.itemWidth, this.itemHeight);
            convertView.setLayoutParams(layoutParams);
        }
        if (getList() != null && getList().size() != 0) {
            MovieInfo movieInfo = getList().get(position);
            if (movieInfo.isAd()) {
                AdInfo info = (AdInfo) movieInfo;
                viewHolder.adHint.setVisibility(View.VISIBLE);
                viewHolder.gradeView.setVisibility(View.GONE);
                viewHolder.titelView.setTextColor(mContext.getResources().getColor(R.color.red));
                viewHolder.subTitelView.setTextColor(mContext.getResources().getColor(R.color.red));
                viewHolder.titelView.setText(info.getAppName());
                viewHolder.subTitelView.setText(info.getAppDescription());
                mImageLoader.displayImage(info.getImageUrl(), viewHolder.movieImageView, options);
            } else {
                viewHolder.adHint.setVisibility(View.GONE);
                viewHolder.gradeView.setVisibility(View.VISIBLE);
                viewHolder.titelView.setTextColor(mContext.getResources().getColor(R.color.black));
                viewHolder.subTitelView.setTextColor(mContext.getResources().getColor(R.color.gray));
                if (!TextUtils.isEmpty(movieInfo.getGrade())) {
                    viewHolder.gradeView.setText(movieInfo.getGrade());
                }
                viewHolder.titelView.setText(movieInfo.getMovieName());
                viewHolder.subTitelView.setText(movieInfo.getMovieSubTitel());
                mImageLoader.displayImage(movieInfo.getMovieImgPath(), viewHolder.movieImageView, options);
            }
        }
        return convertView;
    }

    class ViewHolder {
        ImageView movieImageView;
        TextView gradeView;
        TextView updaTextView;
        TextView titelView;
        TextView subTitelView;
        TextView adHint;
    }

    public void addAd(List<AdInfo> ads) {
        Iterator<AdInfo> iterator = ads.iterator();
        while (iterator.hasNext()) {
            AdInfo info = iterator.next();
            if (list.size() <= info.getPosition()) list.add(info);
            else list.add(info.getPosition(), info);
        }
    }

    private int itemWidth, itemHeight;

    public void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        float scaleWidth = 0.15f;
        float scaleImageHeight = 0.25f;

        itemWidth = (int) (scaleWidth * screenWidth);
        itemHeight = (int) (itemWidth * 1.8);
    }
}
