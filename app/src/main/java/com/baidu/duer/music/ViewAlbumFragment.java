package com.baidu.duer.music;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.duer.music.dialog.CustomLocalBottomSheetDialog;
import com.baidu.duer.music.model.LocalTrack;
import com.baidu.duer.music.model.Queue;
import com.baidu.duer.music.model.UnifiedTrack;
import com.baidu.duer.music.task.HomeConfigInfo;
import com.squareup.leakcanary.RefWatcher;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewAlbumFragment extends Fragment {

    LocalTrackListAdapter aslAdapter;

    RecyclerView rv;

    ImageView backDrop, backBtn, addToQueueIcon;
    FloatingActionButton fab;
    Context ctx;

    TextView fragTitle, albumTitle, albumTracksText;

    TextView albumDetails;

    HomeActivity activity;
    HomeConfigInfo homeConfigInfo;
    View bottomMarginLayout;

    albumCallbackListener mCallback;

    public ViewAlbumFragment() {
        // Required empty public constructor
    }

    public interface albumCallbackListener {
        void onAlbumSongClickListener();

        void addToPlaylist(UnifiedTrack ut);

        void onAlbumPlayAll();

        void addAlbumToQueue();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = context;
        activity = (HomeActivity) context;
        try {
            mCallback = (albumCallbackListener) context;
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
        return inflater.inflate(R.layout.fragment_view_album, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backBtn = (ImageView) view.findViewById(R.id.view_album_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        backDrop = (ImageView) view.findViewById(R.id.album_backdrop);
        MusicApplication.getImageLoader().DisplayImage(HomeActivity.tempAlbum.getAlbumSongs().get(0).getPath(), backDrop);

        fragTitle = (TextView) view.findViewById(R.id.album_fragment_title);

        albumTitle = (TextView) view.findViewById(R.id.album_title);
        albumTitle.setText(HomeActivity.tempAlbum.getName());

        albumTracksText = (TextView) view.findViewById(R.id.album_tracks_text);

        addToQueueIcon = (ImageView) view.findViewById(R.id.add_album_to_queue_icon);
        addToQueueIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.addAlbumToQueue();
            }
        });

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = ((HomeActivity) getContext()).dpTopx(65);


        int tmp = HomeActivity.tempAlbum.getAlbumSongs().size();
        String details1;
        if (tmp == 1) {
            details1 = "1 Song ";
        } else {
            details1 = tmp + " Songs ";
        }

        albumTracksText.setText(details1);

        fab = (FloatingActionButton) view.findViewById(R.id.play_all_from_album);
        fab.setBackgroundTintList(ColorStateList.valueOf(HomeActivity.themeColor));

        rv = (RecyclerView) view.findViewById(R.id.album_songs_recycler);
        aslAdapter = new LocalTrackListAdapter(HomeActivity.tempAlbum.getAlbumSongs(), getContext());
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(mLayoutManager2);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(aslAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Queue queue = homeConfigInfo.getQueue();

                queue.getQueue().clear();
                for (int i = 0; i < HomeActivity.tempAlbum.getAlbumSongs().size(); i++) {
                    UnifiedTrack ut = new UnifiedTrack(true, HomeActivity.tempAlbum.getAlbumSongs().get(i), null);
                    queue.getQueue().add(ut);
                }
                mCallback.onAlbumPlayAll();
            }
        });

        rv.addOnItemTouchListener(new ClickItemTouchListener(rv) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                LocalTrack track = HomeActivity.tempAlbum.getAlbumSongs().get(position);
                ((HomeActivity)getActivity()).updateQueueIndex(track,null,true);
                ((HomeActivity)getActivity()).resetLocalTrackState(track);

                mCallback.onAlbumSongClickListener();
                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                CustomLocalBottomSheetDialog localBottomSheetDialog = new CustomLocalBottomSheetDialog();
                localBottomSheetDialog.setPosition(position);
                localBottomSheetDialog.setLocalTrack(activity.tempAlbum.getAlbumSongs().get(position));
                localBottomSheetDialog.setFragment("Album");
                localBottomSheetDialog.show(activity.getSupportFragmentManager(), "local_song_bottom_sheet");
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

    }

    public Bitmap getBitmap(String url) {
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(url);
        Bitmap bitmap = null;

        byte[] data = mmr.getEmbeddedPicture();

        if (data != null) {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            return bitmap;
        } else {
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new OvershootInterpolator());
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

    public void updateList() {
        if (aslAdapter != null) {
            aslAdapter.notifyDataSetChanged();
        }
    }
}
