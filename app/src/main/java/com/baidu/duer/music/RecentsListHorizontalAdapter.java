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
import com.baidu.duer.music.model.Track;
import com.baidu.duer.music.model.UnifiedTrack;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Harjot on 15-May-16.
 */
public class RecentsListHorizontalAdapter extends RecyclerView.Adapter<RecentsListHorizontalAdapter.MyViewHolder> {

    private List<UnifiedTrack> recentslyPlayed;
    Context ctx;
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

    public RecentsListHorizontalAdapter(List<UnifiedTrack> recentslyPlayed, Context ctx) {
        this.recentslyPlayed = recentslyPlayed;
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

        UnifiedTrack ut = recentslyPlayed.get(position);
        if (ut.getType()) {
            LocalTrack lt = ut.getLocalTrack();
            MusicApplication.getImageLoader().DisplayImage(lt.getPath(), holder.art);
            holder.title.setText(lt.getTitle());
            holder.artist.setText(lt.getArtist());
        } else {
            Track t = ut.getStreamTrack();
            Picasso.with(ctx)
                    .load(t.getArtworkURL())
                    .resize(100, 100)
                    .error(R.drawable.ic_default)
                    .placeholder(R.drawable.ic_default)
                    .into(holder.art);
            holder.title.setText(t.getTitle());
            holder.artist.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return recentslyPlayed.size();
    }


}
