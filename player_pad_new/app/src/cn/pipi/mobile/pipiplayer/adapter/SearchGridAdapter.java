package cn.pipi.mobile.pipiplayer.adapter;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import cn.pipi.mobile.pipiplayer.bean.AdInfo;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.BitmapManager;

public class SearchGridAdapter extends BaseAdapter {

	private LayoutInflater mLayoutInflater;

	private List<MovieInfo> list;

	private Bitmap defaultBitmap;
	private Context mContext;

	public List<MovieInfo> getList() {
		return list;
	}

	public void setList(List<MovieInfo> list) {
		this.list = list;
	}

	public SearchGridAdapter(Context context) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		defaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_default);
		initData();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list != null && list.size() != 0 ? list.size() : 0;
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
			convertView = mLayoutInflater.inflate(R.layout.search_gridview_item, null);
			viewHolder.movieImageView = (ImageView) convertView.findViewById(R.id.search_movieimgview);
			viewHolder.gradeView = (TextView) convertView.findViewById(R.id.search_moviegrade);
			viewHolder.titelView = (TextView) convertView.findViewById(R.id.search_movietitelview);
			viewHolder.adHint = (TextView) convertView.findViewById(R.id.ad_hint);
			convertView.setTag(viewHolder);
			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(itemWidth, itemHeight);
			convertView.setLayoutParams(layoutParams);
		}
		if (getList() != null && getList().size() != 0) {
			MovieInfo movieInfo = getList().get(position);
			if (movieInfo.isAd()) {
				AdInfo info = (AdInfo) movieInfo;
				viewHolder.gradeView.setTextColor(mContext.getResources().getColor(R.color.red));
				viewHolder.titelView.setTextColor(mContext.getResources().getColor(R.color.red));
				viewHolder.adHint.setVisibility(View.VISIBLE);
				BitmapManager.getInstance().loadBitmap(info.getImageUrl(), viewHolder.movieImageView);
				viewHolder.titelView.setText(info.getAppName());
			} else {
				viewHolder.gradeView.setTextColor(mContext.getResources().getColor(R.color.black));
				viewHolder.titelView.setTextColor(mContext.getResources().getColor(R.color.gray));
				viewHolder.adHint.setVisibility(View.GONE);
				Log.d(AppConfig.Tag, "movieInfo.getGrade():" + movieInfo.getGrade());
				if (!TextUtils.isEmpty(movieInfo.getGrade())) {
					viewHolder.gradeView.setText(movieInfo.getGrade());
				} else {
					viewHolder.gradeView.setText("5.0");
				}
				viewHolder.titelView.setText(movieInfo.getMovieName());
				BitmapManager.getInstance().loadBitmap(movieInfo.getMovieImgPath(), viewHolder.movieImageView);
			}
		}
		return convertView;
	}

	private class ViewHolder {
		ImageView movieImageView;
		TextView gradeView;
		TextView titelView;
		TextView adHint;
	}

	public void addAd(List<AdInfo> ads) {
		Iterator<AdInfo> iterator = ads.iterator();
		while (iterator.hasNext()) {
			AdInfo info = iterator.next();
			if (list.size() <= info.getPosition())
				list.add(info);
			else
				list.add(info.getPosition(), info);
		}
	}
	
	private int itemWidth, itemHeight;
	public void initData(){
		DisplayMetrics dm = new DisplayMetrics();
		((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		
		float scaleWidth = 0.15f;
		float scaleImageHeight = 0.25f;
		
		itemWidth = (int)(scaleWidth*screenWidth);
		itemHeight = (int)(itemWidth*1.8);
	}
}
