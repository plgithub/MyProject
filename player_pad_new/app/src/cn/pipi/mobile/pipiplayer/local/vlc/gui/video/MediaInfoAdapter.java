package cn.pipi.mobile.pipiplayer.local.vlc.gui.video;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.local.libvlc.TrackInfo;

public class MediaInfoAdapter extends ArrayAdapter<TrackInfo> {

    public MediaInfoAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.local_audio_browser_item, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) v.findViewById(R.id.title);
            holder.text = (TextView) v.findViewById(R.id.artist);
            v.setTag(holder);
        } else
            holder = (ViewHolder) v.getTag();

        TrackInfo track = getItem(position);
        String title;
        StringBuilder textBuilder = new StringBuilder(1024);
        Resources res = getContext().getResources();
        switch (track.Type)
        {
            case TrackInfo.TYPE_AUDIO:
                title = res.getString(R.string.track_audio);
                appendCommon(textBuilder, res, track);
                appendAudio(textBuilder, res, track);
                break;
            case TrackInfo.TYPE_VIDEO:
                title = res.getString(R.string.track_video);
                appendCommon(textBuilder, res, track);
                appendVideo(textBuilder, res, track);
                break;
            case TrackInfo.TYPE_TEXT:
                title = res.getString(R.string.track_text);
                appendCommon(textBuilder, res, track);
                break;
            default:
                title = res.getString(R.string.track_unknown);
        }

        holder.title.setText(title);
        holder.text.setText(textBuilder.toString());

        return v;
    }

    private void appendCommon(StringBuilder textBuilder, Resources res, TrackInfo track) {
        textBuilder.append(res.getString(R.string.track_codec_info, track.Codec));
        if (track.Language != null && !track.Language.equalsIgnoreCase("und"))
            textBuilder.append(res.getString(R.string.track_language_info, track.Language));
    }

    private void appendAudio(StringBuilder textBuilder, Resources res, TrackInfo track) {
//        textBuilder.append(res.getQuantityString(R.plurals.track_channels_info_quantity, track.Channels, track.Channels));
//        textBuilder.append(res.getString(R.string.track_samplerate_info, track.Samplerate));
    }

    private void appendVideo(StringBuilder textBuilder, Resources res, TrackInfo track) {
        if( track.Width != 0 && track.Height != 0 )
            textBuilder.append(res.getString(R.string.track_resolution_info, track.Width, track.Height));
        if( !Float.isNaN(track.Framerate) )
            textBuilder.append(res.getString(R.string.track_framerate_info, track.Framerate));
    }

    static class ViewHolder {
        TextView title;
        TextView text;
    }

}
