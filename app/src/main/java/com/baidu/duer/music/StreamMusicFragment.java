package com.baidu.duer.music;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.duer.music.dialog.CustomGeneralBottomSheetDialog;
import com.baidu.duer.music.model.Track;
import com.baidu.duer.music.model.UnifiedTrack;
import com.baidu.duer.music.task.HomeConfigInfo;
import com.squareup.leakcanary.RefWatcher;


/**
 * A simple {@link Fragment} subclass.
 */
public class StreamMusicFragment extends Fragment {

    public static StreamTrackListAdapter adapter;
    OnTrackSelectedListener mCallback;
    Context ctx;
    HomeConfigInfo homeConfigInfo;
    RecyclerView lv;

    View bottomMarginLayout;

    public StreamMusicFragment() {
        // Required empty public constructor
    }

    public interface OnTrackSelectedListener {
        public void onTrackSelected(int position);

        public void addToPlaylist(UnifiedTrack ut);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnTrackSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
        ctx = context;

        homeConfigInfo =((HomeActivity) context).getHomeConfigInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stream_music, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = ((HomeActivity) getContext()).dpTopx(65);

        lv = (RecyclerView) view.findViewById(R.id.trackList);

        adapter = new StreamTrackListAdapter(getContext(), HomeActivity.streamingTrackList);
        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        lv.setLayoutManager(mLayoutManager2);
        lv.setItemAnimator(new DefaultItemAnimator());
        lv.setAdapter(adapter);

        lv.addOnItemTouchListener(new ClickItemTouchListener(lv) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                Track track = HomeActivity.streamingTrackList.get(position);
                ((HomeActivity)getActivity()).updateQueueIndex(null,track,false);
                ((HomeActivity)getActivity()).resetTrackState(track);

                mCallback.onTrackSelected(position);
                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                CustomGeneralBottomSheetDialog generalBottomSheetDialog = new CustomGeneralBottomSheetDialog();
                generalBottomSheetDialog.setPosition(position);
                generalBottomSheetDialog.setTrack(new UnifiedTrack(false, null, HomeActivity.streamingTrackList.get(position)));
                generalBottomSheetDialog.setFragment("Stream");
                generalBottomSheetDialog.show(getActivity().getSupportFragmentManager(), "general_bottom_sheet_dialog");
                return true;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

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

    public void dataChanged() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

}
