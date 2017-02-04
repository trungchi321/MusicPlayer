package com.baidu.duer.music;


import android.content.Context;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.duer.music.model.AllPlaylists;
import com.baidu.duer.music.model.Playlist;
import com.baidu.duer.music.model.UnifiedTrack;
import com.baidu.duer.music.imageLoader.ImageLoader;
import com.baidu.duer.music.task.HomeConfigInfo;
import com.baidu.duer.music.task.SavePlaylists;
import com.squareup.leakcanary.RefWatcher;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayListFragment extends Fragment {

    RecyclerView allPlaylistRecycler;
    ViewAllPlaylistsRecyclerAdapter vpAdapter;

    LinearLayout noPlaylistContent;

    onPlaylistTouchedListener mCallback;
    onPlaylistMenuPlayAllListener mCallback2;
    onPlaylistRenameListener mCallback3;
    newPlaylistListerner mCallback4;

    LinearLayoutManager mLayoutManager2;

    FloatingActionButton addPlaylistFAB;

    HomeActivity homeActivity;

    View bottomMarginLayout;

    ImageView backBtn;
    TextView allPlaylistFragmentTitle;
    ImageView[] imgView = new ImageView[10];

    ImageView playlistFragIcon;
    HomeConfigInfo homeConfigInfo;
    ImageLoader imgLoader;

    public PlayListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            imgLoader = new ImageLoader(context);
            imgLoader.type = "all_playlist";
            homeActivity = (HomeActivity) context;
            mCallback = (onPlaylistTouchedListener) context;
            mCallback2 = (onPlaylistMenuPlayAllListener) context;
            mCallback3 = (onPlaylistRenameListener) context;
            mCallback4 = (newPlaylistListerner) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

        homeConfigInfo =((HomeActivity) context).getHomeConfigInfo();

    }

    public interface onPlaylistTouchedListener {
        public void onPlaylistTouched(int pos);
    }

    public interface onPlaylistMenuPlayAllListener {
        public void onPlaylistMenuPLayAll();
    }

    public interface onPlaylistRenameListener {
        public void onPlaylsitRename();
    }


    public interface newPlaylistListerner {
        public void newPlaylistListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backBtn = (ImageView) view.findViewById(R.id.all_playlist_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        allPlaylistFragmentTitle = (TextView) view.findViewById(R.id.all_playlist_fragment_title);

        playlistFragIcon = (ImageView) view.findViewById(R.id.all_playlist_frag_icon);
        playlistFragIcon.setImageTintList(ColorStateList.valueOf(HomeActivity.themeColor));

        initializeHeaderImages(view);

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = ((HomeActivity) getContext()).dpTopx(65);

        noPlaylistContent = (LinearLayout) view.findViewById(R.id.noPlaylistContent);

        allPlaylistRecycler = (RecyclerView) view.findViewById(R.id.all_playlists_recycler);

        addPlaylistFAB = (FloatingActionButton) view.findViewById(R.id.new_playlist_fab);
        addPlaylistFAB.setBackgroundTintList(ColorStateList.valueOf(HomeActivity.themeColor));
        addPlaylistFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback4.newPlaylistListener();
            }
        });

        if (homeConfigInfo.getAllPlaylists().getPlaylists().size() == 0) {
            allPlaylistRecycler.setVisibility(View.INVISIBLE);
            noPlaylistContent.setVisibility(View.VISIBLE);
        } else {
            allPlaylistRecycler.setVisibility(View.VISIBLE);
            noPlaylistContent.setVisibility(View.INVISIBLE);
        }
        vpAdapter = new ViewAllPlaylistsRecyclerAdapter(homeConfigInfo.getAllPlaylists().getPlaylists(), getContext());
        mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        allPlaylistRecycler.setLayoutManager(mLayoutManager2);
        allPlaylistRecycler.setItemAnimator(new DefaultItemAnimator());
        allPlaylistRecycler.setAdapter(vpAdapter);

        allPlaylistRecycler.addOnItemTouchListener(new ClickItemTouchListener(allPlaylistRecycler) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                mCallback.onPlaylistTouched(position);
                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                PopupMenu popup = new PopupMenu(getContext(), view);
                popup.getMenuInflater().inflate(R.menu.playlist_popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Play")) {
                            HomeActivity.tempPlaylist = homeConfigInfo.getAllPlaylists().getPlaylists().get(position);

                            int size = HomeActivity.tempPlaylist.getSongList().size();
                            homeConfigInfo.getQueue().getQueue().clear();
                            for (int i = 0; i < size; i++) {
                                homeConfigInfo.getQueue().addToQueue(HomeActivity.tempPlaylist.getSongList().get(i));
                            }
                            homeConfigInfo.setQueueCurrentIndex(0);
                            mCallback2.onPlaylistMenuPLayAll();
                        } else if (item.getTitle().equals("Add to Queue")) {
                            Playlist pl = homeConfigInfo.getAllPlaylists().getPlaylists().get(position);
                            for (UnifiedTrack ut : pl.getSongList()) {
                                homeConfigInfo.getQueue().addToQueue(ut);
                            }
                        } else if (item.getTitle().equals("Delete")) {
                            homeConfigInfo.getAllPlaylists().getPlaylists().remove(position);
                            if (vpAdapter != null) {
                                vpAdapter.notifyItemRemoved(position);
                            }
                            if (homeConfigInfo.getAllPlaylists().getPlaylists().size() == 0) {
                                noPlaylistContent.setVisibility(View.VISIBLE);
                            }
                            new SavePlaylists((HomeActivity)getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            homeActivity.pAdapter.notifyItemRemoved(position);
                        } else if (item.getTitle().equals("Rename")) {
                            HomeActivity.renamePlaylistNumber = position;
                            mCallback3.onPlaylsitRename();
                        }
                        return true;
                    }
                });
                popup.show();
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mLayoutManager2.scrollToPositionWithOffset(0, 0);
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

    public void itemChanged(int position) {
        if (vpAdapter != null) {
            vpAdapter.notifyItemChanged(position);
        }
    }

    public void dataChanged() {
        if (vpAdapter != null) {
            vpAdapter.notifyDataSetChanged();
            if (homeConfigInfo.getAllPlaylists().getPlaylists().size() > 0) {
                allPlaylistRecycler.setVisibility(View.VISIBLE);
                noPlaylistContent.setVisibility(View.INVISIBLE);

            }
        }
    }

    public void itemRemoved(int position) {
        if (vpAdapter != null) {
            vpAdapter.notifyItemRemoved(position);
        }
    }

    public void initializeHeaderImages(View v) {

        imgView[0] = (ImageView) v.findViewById(R.id.all_playlist_img_1);
        imgView[1] = (ImageView) v.findViewById(R.id.all_playlist_img_2);
        imgView[2] = (ImageView) v.findViewById(R.id.all_playlist_img_3);
        imgView[3] = (ImageView) v.findViewById(R.id.all_playlist_img_4);
        imgView[4] = (ImageView) v.findViewById(R.id.all_playlist_img_5);
        imgView[5] = (ImageView) v.findViewById(R.id.all_playlist_img_6);
        imgView[6] = (ImageView) v.findViewById(R.id.all_playlist_img_7);
        imgView[7] = (ImageView) v.findViewById(R.id.all_playlist_img_8);
        imgView[8] = (ImageView) v.findViewById(R.id.all_playlist_img_9);
        imgView[9] = (ImageView) v.findViewById(R.id.all_playlist_img_10);
        AllPlaylists allPlaylists=homeConfigInfo.getAllPlaylists();
        int numPlaylists = allPlaylists.getPlaylists().size();
        Playlist pl1, pl2;
        if (numPlaylists == 0) {
            for (int i = 0; i < 10; i++) {
                imgLoader.DisplayImage("all_playlist" + i, imgView[i]);
            }
        } else if (numPlaylists == 1) {
            pl1 = allPlaylists.getPlaylists().get(0);
            for (int i = 0; i < Math.min(10, pl1.getSongList().size()); i++) {
                UnifiedTrack ut = pl1.getSongList().get(i);
                if (ut.getType())
                    imgLoader.DisplayImage(ut.getLocalTrack().getPath(), imgView[i]);
                else
                    imgLoader.DisplayImage(ut.getStreamTrack().getArtworkURL(), imgView[i]);
            }
            if (pl1.getSongList().size() < 10) {
                for (int i = pl1.getSongList().size(); i < 10; i++) {
                    imgLoader.DisplayImage("all_playlist" + i, imgView[i]);
                }
            }
        } else {
            pl1 = allPlaylists.getPlaylists().get(0);
            pl2 = allPlaylists.getPlaylists().get(1);
            for (int i = 0; i < Math.min(10, pl1.getSongList().size()); i++) {
                UnifiedTrack ut = pl1.getSongList().get(i);
                if (ut.getType())
                    imgLoader.DisplayImage(pl1.getSongList().get(i).getLocalTrack().getPath(), imgView[i]);
                else
                    imgLoader.DisplayImage(pl1.getSongList().get(i).getStreamTrack().getArtworkURL(), imgView[i]);
            }
            if (pl1.getSongList().size() < 10) {
                if (pl2.getSongList().size() >= (10 - pl1.getSongList().size())) {
                    for (int i = pl1.getSongList().size(); i < 10; i++) {
                        UnifiedTrack ut = pl2.getSongList().get(i - pl1.getSongList().size());
                        if (ut.getType())
                            imgLoader.DisplayImage(ut.getLocalTrack().getPath(), imgView[i]);
                        else
                            imgLoader.DisplayImage(ut.getStreamTrack().getArtworkURL(), imgView[i]);
                    }
                } else {
                    for (int i = pl1.getSongList().size(); i < pl1.getSongList().size() + pl2.getSongList().size(); i++) {
                        UnifiedTrack ut = pl2.getSongList().get(i - pl1.getSongList().size());
                        if (ut.getType())
                            imgLoader.DisplayImage(ut.getLocalTrack().getPath(), imgView[i]);
                        else
                            imgLoader.DisplayImage(ut.getStreamTrack().getArtworkURL(), imgView[i]);
                    }
                    for (int i = pl1.getSongList().size() + pl2.getSongList().size(); i < 10; i++) {
                        imgLoader.DisplayImage("all_playlist" + i, imgView[i]);
                    }
                }
            }
        }
    }

}
