package adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import object.Song;
import trancemusiccollections.tranceplayer.R;

/**
 * Created by duc.t.pham on 9/23/15.
 */
public class SongAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Song> listSongs;
    private int position;

    public SongAdapter(Context context, ArrayList<Song> listSongs) {
        this.context = context;
        this.listSongs = listSongs;
    }

    @Override
    public int getCount() {
        if (listSongs != null) {
            return listSongs.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.listview_song_item, null);
            holder.ivStatus = (ImageView) view.findViewById(R.id.ivStatus);
            holder.txtTitle = (TextView) view.findViewById(R.id.txtTitle);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        this.position = position;
        Song item = listSongs.get(position);
        if (item.isPlaying()) {
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.player_disc_rotation);
            holder.ivStatus.startAnimation(anim);
        } else {
            holder.ivStatus.clearAnimation();
        }
        holder.txtTitle.setText(item.getTitle());
        return view;
    }

    public Song getSong(int position) {
        return listSongs.get(position);
    }

    private class ViewHolder {
        private ImageView ivStatus;
        private TextView txtTitle;
    }
}
