package cn.pipi.mobile.pipiplayer.local.vlc.gui.video;

import java.io.File;
import java.net.URLDecoder;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.local.libvlc.Media;
import cn.pipi.mobile.pipiplayer.local.vlc.BitmapCache;
import cn.pipi.mobile.pipiplayer.local.vlc.Util;
import cn.pipi.mobile.pipiplayer.util.SdcardUtil;

public class VideoListAdapter extends ArrayAdapter<Media>
                                 implements Comparator<Media> {

    public final static int SORT_BY_TITLE = 0;
    public final static int SORT_BY_LENGTH = 1;
    private int mSortDirection = 1;
    private int mSortBy = SORT_BY_TITLE;
    private boolean mListMode = false;
    private VideoGridFragment mFragment;
    Context context;
    private int itemWidth,itemHeight;
    
    public VideoListAdapter(Context context, VideoGridFragment fragment) {
        super(context, 0);
        mFragment = fragment;
        this.context=context;
        initData();
    }

    public final static String TAG = "PipiPlayer/MediaLibraryAdapter";

    public synchronized void update(Media item) {
        int position = getPosition(item);
        if (position != -1) {
            remove(item);
            insert(item, position);
        }
    }

    public void setTimes(HashMap<String, Long> times) {
        // update times
        for (int i = 0; i < getCount(); ++i) {
            Media media = getItem(i);
            Long time = times.get(media.getLocation());
            if (time != null)
                media.setTime(time);
        }
    }

    public void sortBy(int sortby) {
        switch (sortby) {
            case SORT_BY_TITLE:
                if (mSortBy == SORT_BY_TITLE)
                    mSortDirection *= -1;
                else {
                    mSortBy = SORT_BY_TITLE;
                    mSortDirection = 1;
                }
                break;
            case SORT_BY_LENGTH:
                if (mSortBy == SORT_BY_LENGTH)
                    mSortDirection *= -1;
                else {
                    mSortBy = SORT_BY_LENGTH;
                    mSortDirection *= 1;
                }
                break;
            default:
                mSortBy = SORT_BY_TITLE;
                mSortDirection = 1;
                break;
        }
        sort();
    }

    public void sort() {
        super.sort(this);
    }

    @Override
    public int compare(Media item1, Media item2) {
        int compare = 0;
        switch (mSortBy) {
            case SORT_BY_TITLE:
                compare = item1.getTitle().toUpperCase(Locale.ENGLISH).compareTo(
                        item2.getTitle().toUpperCase(Locale.ENGLISH));
                break;
            case SORT_BY_LENGTH:
                compare = ((Long) item1.getLength()).compareTo(item2.getLength());
                break;
        }
        return mSortDirection * compare;
    }

    /**
     * Display the view of a file browser item.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View v = convertView;

        if (v == null || (((ViewHolder)v.getTag()).listmode != mListMode)) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (!mListMode)
                v = inflater.inflate(R.layout.local_video_grid_item, parent, false);
            else
                v = inflater.inflate(R.layout.local_video_list_item, parent, false);

            holder = new ViewHolder();
            holder.layout = v.findViewById(R.id.layout_item);
            holder.thumbnail = (ImageView) v.findViewById(R.id.ml_item_thumbnail);
            holder.title = (TextView) v.findViewById(R.id.ml_item_title);
            holder.subsize = (TextView) v.findViewById(R.id.ml_item_subsize);
            holder.subtitle = (TextView) v.findViewById(R.id.ml_item_subtitle);
            holder.progress = (ProgressBar) v.findViewById(R.id.ml_item_progress);
            holder.listmode = mListMode;
            v.setTag(holder);

            /* Set the layoutParams based on the values set in the video_grid_item.xml root element */
            v.setLayoutParams(new GridView.LayoutParams(this.itemWidth, this.itemHeight));
        } else {
            holder = (ViewHolder) v.getTag();
        }
        //holder.thumbnail.setImageResource(R.drawable.img_default_small);
        Media media = getItem(position);
        /* Thumbnail */
        /*if(media.getLocation()!=null){
        	 BitmapManager.getInstance().getVideoThumbnail(media.getLocation().replace("file://", ""), holder.thumbnail);
        }*/
        Bitmap thumbnail = Util.getPictureFromCache(media);
        if (thumbnail == null) {
            // missing thumbnail
//            thumbnail = BitmapCache.GetFromResource(v, R.drawable.img_default_small);
        }
        else if (thumbnail.getWidth() == 1 && thumbnail.getHeight() == 1) {
            // dummy thumbnail
//            thumbnail = BitmapCache.GetFromResource(v, R.drawable.img_default_small);
        }
        //FIXME Warning: the thumbnails are upscaled in the grid view!
        holder.thumbnail.setImageBitmap(thumbnail);
        /* Color state */
        ColorStateList titleColor = v.getResources().getColorStateList(R.color.list_title);
        holder.title.setTextColor(titleColor);
        StringBuffer string=new StringBuffer("大小：");
        try {
        	if(media.getLocation()!=null){
        		@SuppressWarnings("deprecation")
				File file= new File(URLDecoder.decode(media.getLocation().replace("file://", "")));
        		if(file!=null&&file.exists()){
        			 string.append(SdcardUtil.formatSize(context,file.length()));
        		}
        			
        	}
		} catch (Exception e) {
			// TODO: handle exception
		}
        if (media.getWidth() > 0 && media.getHeight() > 0) {
        	string.append(String.format(" - %dx%d", media.getWidth(), media.getHeight()));
        }
        holder.subsize.setText(string.toString());
        /* Time / Duration */
        long lastTime = media.getTime();
        String text;
        if (lastTime > 0) {
            text ="时长："+ String.format("%s / %s",
                    Util.millisToText(lastTime),
                    Util.millisToText(media.getLength()));
            holder.progress.setVisibility(View.VISIBLE);
            holder.progress.setMax((int) (media.getLength() / 1000));
            holder.progress.setProgress((int) (lastTime / 1000));
        } else {
            text = "时长："+Util.millisToText(media.getLength());
            holder.progress.setVisibility(View.GONE);
        }
        /* Text */
        holder.subtitle.setText(text);
        holder.title.setText(media.getTitle());

        /* Popup menu */
        final ImageView more = (ImageView)v.findViewById(R.id.ml_item_more);
        if (more != null && mFragment != null) {
            more.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragment.onContextPopupMenu(more, position);
                }
            });
        }

        return v;
    }

    static class ViewHolder {
        boolean listmode;
        View layout;
        ImageView thumbnail;
        TextView title;
        TextView subsize;
        TextView subtitle;
        ImageView more;
        ProgressBar progress;
    }

    public void setListMode(boolean value) {
        mListMode = value;
    }

    public boolean isListMode() {
        return mListMode;
    }
	public void initData(){
		DisplayMetrics dm = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		
		float scaleWidth = 0.15f;
		float scaleImageHeight = 0.25f;
		
		itemWidth = (int)(scaleWidth*screenWidth);
		itemHeight = (int)(itemWidth*1.8);
	}
}
