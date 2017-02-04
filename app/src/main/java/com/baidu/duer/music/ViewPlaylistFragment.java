package com.baidu.duer.music;


import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.duer.music.helper.SimpleItemTouchHelperCallback;
import com.baidu.duer.music.model.LocalTrack;
import com.baidu.duer.music.model.Track;
import com.baidu.duer.music.model.UnifiedTrack;
import com.baidu.duer.music.task.HomeConfigInfo;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPlaylistFragment extends Fragment implements
        PlaylistTrackAdapter.OnDragStartListener,
        PlaylistTrackAdapter.OnMoveRemoveListener {

    RecyclerView playlistRecyler;
    PlaylistTrackAdapter plAdapter;
    FloatingActionButton playAll;

    ImageView backdrop, backBtn, renameIcon, addToQueueIcon;
    TextView title, songsText, fragmentTitle;

    ItemTouchHelper mItemTouchHelper;

    LinearLayoutManager mLayoutManager2;

    View bottomMarginLayout;
    HomeConfigInfo homeConfigInfo;
    playlistCallbackListener mCallback;

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void updateViewPlaylistFragment() {

        title.setText(HomeActivity.tempPlaylist.getPlaylistName());

        String s = "";
        if (HomeActivity.tempPlaylist.getSongList().size() > 1)
            s = "Songs";
        else
            s = "Song";
        songsText.setText(HomeActivity.tempPlaylist.getSongList().size() + " " + s);

        UnifiedTrack ut = HomeActivity.tempPlaylist.getSongList().get(0);
        if (ut.getType()) {
            LocalTrack lt = ut.getLocalTrack();
            MusicApplication.getImageLoader().DisplayImage(lt.getPath(), backdrop);
        } else {
            Track t = ut.getStreamTrack();
            Picasso.with(getContext())
                    .load(t.getArtworkURL())
                    .resize(100, 100)
                    .error(R.drawable.ic_default)
                    .placeholder(R.drawable.ic_default)
                    .into(backdrop);
        }

    }

    public interface playlistCallbackListener {
        void onPlaylistPLayAll();

        void onPLaylistItemClicked(int position);

        void playlistRename();

        void playlistAddToQueue();
    }

    public ViewPlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (playlistCallbackListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

        homeConfigInfo =((HomeActivity) context).getHomeConfigInfo();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_playlist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = ((HomeActivity) getContext()).dpTopx(65);

        fragmentTitle = (TextView) view.findViewById(R.id.playlist_fragment_title);

        title = (TextView) view.findViewById(R.id.playlist_title);
        title.setText(HomeActivity.tempPlaylist.getPlaylistName());

        songsText = (TextView) view.findViewById(R.id.playlist_tracks_text);
        String s = "";
        if (HomeActivity.tempPlaylist.getSongList().size() > 1)
            s = "Songs";
        else
            s = "Song";
        songsText.setText(HomeActivity.tempPlaylist.getSongList().size() + " " + s);

        backBtn = (ImageView) view.findViewById(R.id.view_playlist_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        renameIcon = (ImageView) view.findViewById(R.id.rename_playlist_icon);
        renameIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.playlistRename();
            }
        });

        addToQueueIcon = (ImageView) view.findViewById(R.id.add_playlist_to_queue_icon);
        addToQueueIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.playlistAddToQueue();
            }
        });

        backdrop = (ImageView) view.findViewById(R.id.playlist_backdrop);
        UnifiedTrack ut = HomeActivity.tempPlaylist.getSongList().get(0);
        if (ut.getType()) {
            LocalTrack lt = ut.getLocalTrack();
            MusicApplication.getImageLoader().DisplayImage(lt.getPath(), backdrop);
        } else {
            Track t = ut.getStreamTrack();
            Picasso.with(getContext())
                    .load(t.getArtworkURL())
                    .resize(100, 100)
                    .error(R.drawable.ic_default)
                    .placeholder(R.drawable.ic_default)
                    .into(backdrop);
        }

        playlistRecyler = (RecyclerView) view.findViewById(R.id.view_playlist_recycler);
        plAdapter = new PlaylistTrackAdapter(HomeActivity.tempPlaylist.getSongList(), this, getContext());
        mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        playlistRecyler.setLayoutManager(mLayoutManager2);
        playlistRecyler.setItemAnimator(new DefaultItemAnimator());
        playlistRecyler.setAdapter(plAdapter);

        playlistRecyler.addOnItemTouchListener(new ClickItemTouchListener(playlistRecyler) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                mCallback.onPLaylistItemClicked(position);
                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        playAll = (FloatingActionButton) view.findViewById(R.id.play_all_fab);
        if (HomeActivity.tempPlaylist.getSongList().size() == 0) {
            playAll.setVisibility(View.GONE);
        }
        playAll.setBackgroundTintList(ColorStateList.valueOf(HomeActivity.themeColor));
        playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = HomeActivity.tempPlaylist.getSongList().size();
                homeConfigInfo.getQueue().getQueue().clear();
                for (int i = 0; i < size; i++) {
                    homeConfigInfo.getQueue().addToQueue(HomeActivity.tempPlaylist.getSongList().get(i));
                }
                homeConfigInfo.setQueueCurrentIndex(0);
                mCallback.onPlaylistPLayAll();
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(plAdapter,homeConfigInfo);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(playlistRecyler);

    }

    @Override
    public void onResume() {
        super.onResume();
        mLayoutManager2.scrollToPositionWithOffset(0, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                playAll.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new OvershootInterpolator());
            }
        }, 500);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RefWatcher refWatcher = MusicApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MusicApplication.getRefWatcher(getContext());
        refWatcher.watch(this);
    }

}
