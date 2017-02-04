package com.baidu.duer.music;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.duer.music.model.AllSavedDNA;
import com.baidu.duer.music.task.HomeConfigInfo;
import com.baidu.duer.music.task.SaveTheDNAs;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.baidu.duer.music.model.SavedDNA;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewSavedDNA extends Fragment {

    RecyclerView viewDnaRecycler;
    ViewSavedDnaRecyclerAdapter vdAdapter;
    LinearLayoutManager mLayoutManager2;

    VisualizerView2 mVisualizerView2;

    ImageView shareIcon, saveToStorageIcon;

    LinearLayout noSavedContent;

    onShareListener mCallback;

    ShowcaseView showCase;

    boolean addTextToImage = false;

    int selectedDNA = 0;

    View bottomMarginLayout;
    HomeConfigInfo homeConfigInfo;
    ImageView backBtn;
    TextView fragmentTitle;

    public ViewSavedDNA() {
        // Required empty public constructor
    }

    public interface onShareListener {
        void onShare(Bitmap bmp, String fileName);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (onShareListener) context;
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
        return inflater.inflate(R.layout.fragment_view_saved_dn, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backBtn = (ImageView) view.findViewById(R.id.view_saved_dna_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        fragmentTitle = (TextView) view.findViewById(R.id.view_saved_dna_fragment_title);

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = ((HomeActivity) getContext()).dpTopx(65);

        mVisualizerView2 = (VisualizerView2) view.findViewById(R.id.saved_dna_visualizer);
        viewDnaRecycler = (RecyclerView) view.findViewById(R.id.saved_dna_recycler);

        noSavedContent = (LinearLayout) view.findViewById(R.id.no_saved_dnas);

        AllSavedDNA savedDNAs = homeConfigInfo.getSavedDNAs();
        if (savedDNAs == null || savedDNAs.getSavedDNAs().size() == 0) {
            noSavedContent.setVisibility(View.VISIBLE);
            mVisualizerView2.setVisibility(View.INVISIBLE);
            viewDnaRecycler.setVisibility(View.INVISIBLE);
        } else {
            noSavedContent.setVisibility(View.GONE);
            mVisualizerView2.setVisibility(View.VISIBLE);
            viewDnaRecycler.setVisibility(View.VISIBLE);
        }

        if (savedDNAs != null)
            vdAdapter = new ViewSavedDnaRecyclerAdapter(savedDNAs.getSavedDNAs(), getContext(), this);
        else
            vdAdapter = new ViewSavedDnaRecyclerAdapter(new ArrayList<SavedDNA>(), getContext(), this);

        mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        viewDnaRecycler.setLayoutManager(mLayoutManager2);
        viewDnaRecycler.setItemAnimator(new DefaultItemAnimator());
        viewDnaRecycler.setAdapter(vdAdapter);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AllSavedDNA savedDNAs = homeConfigInfo.getSavedDNAs();

                if (savedDNAs.getSavedDNAs().size() > 0) {
                    SavedDNA dna = savedDNAs.getSavedDNAs().get(selectedDNA);
                    selectedDNA = 0;
                    homeConfigInfo.setTempSavedDNA(dna);
                    Bitmap bmp = bitmapFromBase64String(dna.getBase64encodedBitmap());
                    mVisualizerView2.setBmp(bmp);
                    mVisualizerView2.update();
                }
            }
        }, 350);

        viewDnaRecycler.addOnItemTouchListener(new ClickItemTouchListener(viewDnaRecycler) {
            @Override
            boolean onClick(RecyclerView parent, View view, int position, long id) {
                AllSavedDNA savedDNAs = homeConfigInfo.getSavedDNAs();

                vdAdapter.notifyItemChanged(selectedDNA);
                selectedDNA = position;
                vdAdapter.notifyItemChanged(selectedDNA);
                SavedDNA dna = savedDNAs.getSavedDNAs().get(position);
                selectedDNA = position;
                homeConfigInfo.setTempSavedDNA(dna);
                Bitmap bmp = bitmapFromBase64String(dna.getBase64encodedBitmap());
                mVisualizerView2.setBmp(bmp);
                mVisualizerView2.update();
                return true;
            }

            @Override
            boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                PopupMenu popup = new PopupMenu(getContext(), view);
                popup.getMenuInflater().inflate(R.menu.save_dna_popup, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        AllSavedDNA savedDNAs = homeConfigInfo.getSavedDNAs();

                        if (item.getTitle().equals("View")) {
                            vdAdapter.notifyItemChanged(selectedDNA);
                            selectedDNA = position;
                            vdAdapter.notifyItemChanged(selectedDNA);
                            SavedDNA dna = savedDNAs.getSavedDNAs().get(position);
                            selectedDNA = position;
                            homeConfigInfo.setTempSavedDNA(dna);
                            Bitmap bmp = bitmapFromBase64String(dna.getBase64encodedBitmap());
                            mVisualizerView2.setBmp(bmp);
                            mVisualizerView2.update();
                        } else if (item.getTitle().equals("Delete")) {
                            savedDNAs.getSavedDNAs().remove(position);
                            vdAdapter.notifyItemRemoved(position);

                            if (position == selectedDNA) {
                                if (position > 0) {
                                    selectedDNA = position - 1;
                                } else if (position == 0) {
                                    if (savedDNAs.getSavedDNAs().size() == 0) {
                                        noSavedContent.setVisibility(View.VISIBLE);
                                        new SaveTheDNAs(getActivity(),homeConfigInfo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                        return true;
                                    } else {
                                        selectedDNA = 0;
                                    }
                                }

                                SavedDNA dna = savedDNAs.getSavedDNAs().get(selectedDNA);
                                homeConfigInfo.setTempSavedDNA(dna);

                                Bitmap bmp = bitmapFromBase64String(dna.getBase64encodedBitmap());
                                mVisualizerView2.setBmp(bmp);
                                mVisualizerView2.update();
                            }

                            new SaveTheDNAs(getActivity(),homeConfigInfo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

        shareIcon = (ImageView) view.findViewById(R.id.share_icon);
        saveToStorageIcon = (ImageView) view.findViewById(R.id.save_to_storage_icon);

        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeConfigInfo.getTempSavedDNA()!= null) {
                    showDialog(1);
                }
            }
        });

        saveToStorageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeConfigInfo.getTempSavedDNA()!= null) {
                    showDialog(0);
                }
            }
        });

        Button mEndButton = new Button(getContext());
        mEndButton.setBackgroundColor(HomeActivity.themeColor);
        mEndButton.setTextColor(Color.WHITE);

        if (savedDNAs.getSavedDNAs().size() > 0) {
            showCase = new ShowcaseView.Builder(getActivity())
                    .blockAllTouches()
                    .singleShot(5)
                    .setStyle(R.style.CustomShowcaseTheme)
                    .useDecorViewAsParent()
                    .replaceEndButton(mEndButton)
                    .setContentTitlePaint(HomeActivity.tp)
                    .setTarget(new ViewTarget(R.id.visualizer_alt_showcase, getActivity()))
                    .setContentTitle("Saved DNAs")
                    .setContentText("View all your saved DNAs here")
                    .build();
            showCase.setButtonText("Next");
            showCase.setButtonPosition(HomeActivity.lps);
            showCase.overrideButtonClick(new View.OnClickListener() {
                int count1 = 0;

                @Override
                public void onClick(View v) {
                    count1++;
                    switch (count1) {
                        case 1:
                            showCase.setTarget(new ViewTarget(shareIcon.getId(), getActivity()));
                            showCase.setContentTitle("Share DNA");
                            showCase.setContentText("Share the DNA as an image with your friends");
                            showCase.setButtonPosition(HomeActivity.lps);
                            showCase.setButtonText("Next");
                            break;
                        case 2:
                            showCase.setTarget(new ViewTarget(saveToStorageIcon.getId(), getActivity()));
                            showCase.setContentTitle("Save DNA");
                            showCase.setContentText("Save the DNA as an image to your internal storage");
                            showCase.setButtonPosition(HomeActivity.lps);
                            showCase.setButtonText("Done");
                            break;
                        case 3:
                            showCase.hide();
                            break;
                    }
                }

            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mLayoutManager2.scrollToPositionWithOffset(0, 0);
    }

    public void showDialog(int type) {
        if (type == 0) {
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.save_image_dialog);
            dialog.setTitle("Save as Image");

            // set the custom dialog components - text, image and button
            final EditText text = (EditText) dialog.findViewById(R.id.save_image_filename_text);
            text.setText(homeConfigInfo.getTempSavedDNA().getName());
            Button btn = (Button) dialog.findViewById(R.id.save_image_btn);
            btn.setBackgroundColor(HomeActivity.themeColor);

            CheckBox cb = (CheckBox) dialog.findViewById(R.id.text_checkbox);
            cb.setChecked(addTextToImage);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    addTextToImage = isChecked;
                }
            });

            // if button is clicked, close the custom dialog
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (text.getText().toString().trim().equals("")) {
                        text.setError("Enter Filename");
                    } else {
                        mVisualizerView2.drawText(text.getText().toString(), addTextToImage);
                        mVisualizerView2.setDrawingCacheEnabled(true);
                        HomeActivity.saveBitmapAsImage(mVisualizerView2.getDrawingCache(), text.getText().toString());
                        mVisualizerView2.dropText();
                        mVisualizerView2.setDrawingCacheEnabled(false);
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        } else if (type == 1) {
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.save_image_dialog);
            dialog.setTitle("Share as Image");

            // set the custom dialog components - text, image and button
            final EditText text = (EditText) dialog.findViewById(R.id.save_image_filename_text);
            text.setText(homeConfigInfo.getTempSavedDNA().getName());
            Button btn = (Button) dialog.findViewById(R.id.save_image_btn);
            btn.setBackgroundColor(HomeActivity.themeColor);
            btn.setText("SHARE");

            CheckBox cb = (CheckBox) dialog.findViewById(R.id.text_checkbox);
            cb.setChecked(addTextToImage);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    addTextToImage = isChecked;
                }
            });

            // if button is clicked, close the custom dialog
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (text.getText().toString().trim().equals("")) {
                        text.setError("Enter Text");
                    } else {
                        mVisualizerView2.drawText(text.getText().toString(), addTextToImage);
                        mVisualizerView2.setDrawingCacheEnabled(true);
                        mCallback.onShare(mVisualizerView2.getDrawingCache(), text.getText().toString());
                        mVisualizerView2.dropText();
                        mVisualizerView2.setDrawingCacheEnabled(false);
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        }
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

    public boolean isShowcaseVisible() {
        return (showCase != null && showCase.isShowing());
    }

    public void hideShowcase() {
        showCase.hide();
    }

    public void setVisualizerVisibility(int visibility) {
        mVisualizerView2.setVisibility(visibility);
    }

    public int getSelectedDNAnumber() {
        return selectedDNA;
    }

    public Bitmap bitmapFromBase64String(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
