package com.baidu.duer.music;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.duer.music.model.LocalTrack;

import java.util.List;

/**
 * Created by Harjot on 14-May-16.
 */
public class LocalTracksHorizontalAdapter extends RecyclerView.Adapter<LocalTracksHorizontalAdapter.MyViewHolder> {

    private List<LocalTrack> localList;
    private Context ctx;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView art;
        TextView title, artist;
        RelativeLayout bottomHolder;

        public MyViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.backImage);
            title = (TextView) view.findViewById(R.id.card_title);
            artist = (TextView) view.findViewById(R.id.card_artist);
            bottomHolder = (RelativeLayout) view.findViewById(R.id.bottomHolder);
        }
    }

    public LocalTracksHorizontalAdapter(List<LocalTrack> localList, Context ctx) {
        this.localList = localList;
        this.ctx = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card_layout2, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        LocalTrack localTrack = localList.get(position);
        MusicApplication.getImageLoader().DisplayImage(localTrack.getPath(), holder.art);
        holder.title.setText(localTrack.getTitle());
        holder.artist.setText(localTrack.getArtist());
    }

    @Override
    public int getItemCount() {
        return localList.size();
    }

}
