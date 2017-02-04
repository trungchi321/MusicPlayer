package com.baidu.duer.music;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.duer.music.helper.ItemTouchHelperAdapter;
import com.baidu.duer.music.helper.ItemTouchHelperViewHolder;
import com.baidu.duer.music.model.LocalTrack;
import com.baidu.duer.music.model.Queue;
import com.baidu.duer.music.model.Track;
import com.baidu.duer.music.model.UnifiedTrack;
import com.baidu.duer.music.task.HomeConfigInfo;
import com.baidu.duer.music.task.SaveQueue;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Harjot on 16-May-16.
 */


public class QueueRecyclerAdapter extends RecyclerView.Adapter<QueueRecyclerAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {

    private List<UnifiedTrack> queue;
    Context ctx;
    HomeConfigInfo homeConfigInfo;
    public interface OnDragStartListener {
        void onDragStarted(RecyclerView.ViewHolder viewHolder);
    }

    private final OnDragStartListener mDragStartListener;

    public class MyViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder {

        ImageView art;
        TextView title, artist;
        CustomPlayingIndicator indicator;
        ImageView holderImg;

        public MyViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.img);
            title = (TextView) view.findViewById(R.id.title);
            artist = (TextView) view.findViewById(R.id.url);
            indicator = (CustomPlayingIndicator) view.findViewById(R.id.currently_playing_indicator);
            holderImg = (ImageView) view.findViewById(R.id.holderImage);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.parseColor("#333333"));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.BLACK);
        }
    }

    public QueueRecyclerAdapter(List<UnifiedTrack> queue, Context ctx, OnDragStartListener listener) {
        this.queue = queue;
        this.ctx = ctx;
        mDragStartListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_3, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        if (homeConfigInfo.getQueueCurrentIndex() == position && !HomeActivity.isReloaded) {
            holder.title.setTextColor(HomeActivity.themeColor);
            holder.title.setTypeface(null, Typeface.BOLD);
            holder.indicator.setVisibility(View.VISIBLE);
            if (PlayerFragment.mMediaPlayer != null) {
                if (PlayerFragment.mMediaPlayer.isPlaying()) {
                    holder.indicator.play();
                } else {
                    holder.indicator.pause();
                }
            }
        } else {
            holder.title.setTextColor(Color.WHITE);
            holder.title.setTypeface(null, Typeface.NORMAL);
            holder.indicator.setVisibility(View.INVISIBLE);
        }

        holder.holderImg.setColorFilter(HomeActivity.themeColor);
        holder.indicator.setDrawColor(HomeActivity.themeColor);

        UnifiedTrack ut = queue.get(position);
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

        holder.holderImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onDragStarted(holder);
                }
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return queue.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        UnifiedTrack prev = queue.remove(fromPosition);
        queue.add(toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
        if (fromPosition == homeConfigInfo.getQueueCurrentIndex()) {
            homeConfigInfo.setQueueCurrentIndex(toPosition);
        } else if (fromPosition > homeConfigInfo.getQueueCurrentIndex()&& toPosition == homeConfigInfo.getQueueCurrentIndex()) {
            homeConfigInfo.setQueueCurrentIndex(homeConfigInfo.getQueueCurrentIndex()+1);

        } else if (fromPosition < homeConfigInfo.getQueueCurrentIndex() && toPosition ==homeConfigInfo.getQueueCurrentIndex()) {
            homeConfigInfo.setQueueCurrentIndex(homeConfigInfo.getQueueCurrentIndex()-1);
        }
        ((HomeActivity) ctx).updateVisualizerRecycler();
        new SaveQueue((HomeActivity) ctx,homeConfigInfo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onItemDismiss(int position) {
        UnifiedTrack ut = queue.get(position);
        Queue originalQueue = homeConfigInfo.getOriginalQueue();
        if (originalQueue != null)
            originalQueue.removeItem(ut);
        queue.remove(position);
        if (position < homeConfigInfo.getQueueCurrentIndex()) {
            homeConfigInfo.setQueueCurrentIndex(homeConfigInfo.getQueueCurrentIndex()-1);
        }
        notifyItemRemoved(position);
        ((HomeActivity) ctx).updateVisualizerRecycler();
        new SaveQueue((HomeActivity) ctx,homeConfigInfo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
