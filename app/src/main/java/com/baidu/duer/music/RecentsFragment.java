package com.baidu.duer.music;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.duer.music.dialog.CustomGeneralBottomSheetDialog;
import com.baidu.duer.music.helper.SimpleItemTouchHelperCallback;
import com.baidu.duer.music.model.LocalTrack;
import com.baidu.duer.music.model.Queue;
import com.baidu.duer.music.model.RecentlyPlayed;
import com.baidu.duer.music.model.Track;
import com.baidu.duer.music.model.UnifiedTrack;
import com.baidu.duer.music.task.HomeConfigInfo;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecentsFragment extends Fragment implements
        RecentsTrackAdapter.OnDragStartListener,
        RecentsTrackAdapter.onRemoveListener {

    RecyclerView recentRecycler;
    RecentsTrackAdapter rtAdpater;
    LinearLayoutManager mLayoutManager2;

    LinearLayout noContent;

    ItemTouchHelper mItemTouchHelper;

    recentsCallbackListener mCallback;

    FloatingActionButton shuffleFab;

    View bottomMarginLayout;

    ImageView backdrop;
    TextView fragTitle;
    ImageView backBtn, addToQueueIcon, fragIcon;
    HomeConfigInfo homeConfigInfo;

    public RecentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void updateRecentsFragment() {
        RecentlyPlayed recentlyPlayed =homeConfigInfo.getRecentlyPlayed();
        if (recentlyPlayed.getRecentlyPlayed().size() > 0) {
            UnifiedTrack ut = recentlyPlayed.getRecentlyPlayed().get(0);
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
        } else {
            backdrop.setBackground(new ColorDrawable(Color.parseColor("#111111")));
        }
    }

    public interface recentsCallbackListener {
        void onRecentItemClicked(boolean isLocal);

        void addToPlaylist(UnifiedTrack ut);

        void onRecent(int pos);

        void addRecentsToQueue();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (recentsCallbackListener) context;
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
        return inflater.inflate(R.layout.fragment_recents, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backBtn = (ImageView) view.findViewById(R.id.recents_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        fragIcon = (ImageView) view.findViewById(R.id.recents_frag_icon);
        fragIcon.setImageTintList(ColorStateList.valueOf(HomeActivity.themeColor));

        fragTitle = (TextView) view.findViewById(R.id.recents_fragment_title);

        addToQueueIcon = (ImageView) view.findViewById(R.id.add_recents_to_queue_icon);
        addToQueueIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.addRecentsToQueue();
            }
        });

        backdrop = (ImageView) view.findViewById(R.id.recents_backdrop);
        RecentlyPlayed recentlyPlayed =homeConfigInfo.getRecentlyPlayed();

        if (recentlyPlayed.getRecentlyPlayed().size() > 0) {
            UnifiedTrack ut = recentlyPlayed.getRecentlyPlayed().get(0);
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

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = ((HomeActivity) getContext()).dpTopx(65);

        noContent = (LinearLayout) view.findViewById(R.id.no_recents_content);

        recentRecycler = (RecyclerView) view.findViewById(R.id.view_recent_recycler);
        rtAdpater = new RecentsTrackAdapter(recentlyPlayed.getRecentlyPlayed(), this, getContext());
        mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recentRecycler.setLayoutManager(mLayoutManager2);
        recentRecycler.setItemAnimator(new DefaultItemAnimator());
        recentRecycler.setAdapter(rtAdpater);

        recentRecycler.addOnItemTouchListener(new ClickItemTouchListener(recentRecycler) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                RecentlyPlayed recentlyPlayed =homeConfigInfo.getRecentlyPlayed();
                Queue queue =homeConfigInfo.getQueue();
                UnifiedTrack ut = recentlyPlayed.getRecentlyPlayed().get(position);
                boolean isRepeat = false;
                int pos = 0;
                for (int i = 0; i < queue.getQueue().size(); i++) {
                    UnifiedTrack ut1 = queue.getQueue().get(i);
                    if (ut1.getType() && ut.getType() && ut1.getLocalTrack().getTitle().equals(ut.getLocalTrack().getTitle())) {
                        isRepeat = true;
                        pos = i;
                        break;
                    }
                    if (!ut1.getType() && !ut.getType() && ut1.getStreamTrack().getTitle().equals(ut.getStreamTrack().getTitle())) {
                        isRepeat = true;
                        pos = i;
                        break;
                    }
                }
                if (!isRepeat) {
                    if (ut.getType()) {
                        LocalTrack track = ut.getLocalTrack();

                        ((HomeActivity)getActivity()).updateQueueIndex(track,null,true);
                        ((HomeActivity)getActivity()).resetLocalTrackState(track);

                        mCallback.onRecentItemClicked(true);
                    } else {
                        Track track = ut.getStreamTrack();

                        ((HomeActivity)getActivity()).updateQueueIndex(null,track,false);
                        ((HomeActivity)getActivity()).resetTrackState(track);

                        mCallback.onRecentItemClicked(false);
                    }
                } else {
                    mCallback.onRecent(pos);
                }

                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                if (position != -1) {
                    RecentlyPlayed recentlyPlayed = homeConfigInfo.getRecentlyPlayed();
                    final UnifiedTrack ut = recentlyPlayed.getRecentlyPlayed().get(position);
                    CustomGeneralBottomSheetDialog generalBottomSheetDialog = new CustomGeneralBottomSheetDialog();
                    generalBottomSheetDialog.setPosition(position);
                    generalBottomSheetDialog.setTrack(ut);
                    generalBottomSheetDialog.setFragment("Recents");
                    generalBottomSheetDialog.show(getActivity().getSupportFragmentManager(), "general_bottom_sheet_dialog");
                }
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        shuffleFab = (FloatingActionButton) view.findViewById(R.id.play_all_fab_recent);

        if (recentlyPlayed != null && recentlyPlayed.getRecentlyPlayed().size() > 0) {
            noContent.setVisibility(View.INVISIBLE);
            shuffleFab.setVisibility(View.VISIBLE);
        } else {
            noContent.setVisibility(View.VISIBLE);
            shuffleFab.setVisibility(View.INVISIBLE);
        }

        shuffleFab.setBackgroundTintList(ColorStateList.valueOf(HomeActivity.themeColor));
        shuffleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentlyPlayed recentlyPlayed = homeConfigInfo.getRecentlyPlayed();
                Queue queue = homeConfigInfo.getQueue();
                if (recentlyPlayed.getRecentlyPlayed().size() > 0) {
                    queue.getQueue().clear();
                    for (int i = 0; i < recentlyPlayed.getRecentlyPlayed().size(); i++) {
                        queue.getQueue().add(recentlyPlayed.getRecentlyPlayed().get(i));
                    }
                    Random r = new Random();
                    mCallback.onRecent(r.nextInt(queue.getQueue().size()));
                }
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(rtAdpater,homeConfigInfo);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recentRecycler);

    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mLayoutManager2.scrollToPositionWithOffset(0, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shuffleFab.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new OvershootInterpolator());
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
