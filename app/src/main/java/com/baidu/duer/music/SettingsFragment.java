package com.baidu.duer.music;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.support.v7.widget.SwitchCompat;
import android.widget.TextView;

import com.baidu.duer.music.task.HomeConfigInfo;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.squareup.leakcanary.RefWatcher;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    RelativeLayout densitycard, themeCard, albumArtCard, wifiCard;
    SwitchCompat albumArtToggle;
    SwitchCompat wifiToggle;
    ImageView themeColorImg;
    SeekBar densitySeekbar;
    TextView densityTextDialog, densityText;

    HomeActivity homeActivity;
    HomeConfigInfo homeConfigInfo;
    View bottomMarginLayout;

    onAlbumArtBackgroundToggled mCallback;
    onColorChangedListener mCallback2;
    onAboutClickedListener mCallback3;

    ImageView backBtn;
    TextView fragTitle;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public interface onColorChangedListener {
        public void onColorChanged();
    }

    public interface onAlbumArtBackgroundToggled {
        public void onAlbumArtBackgroundChangedVisibility(int visibility);
    }

    public interface onAboutClickedListener {
        public void onAboutClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        homeActivity = (HomeActivity) context;
        homeConfigInfo= homeActivity.getHomeConfigInfo();
        try {
            mCallback = (onAlbumArtBackgroundToggled) getContext();
            mCallback2 = (onColorChangedListener) getContext();
            mCallback3 = (onAboutClickedListener) getContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backBtn = (ImageView) view.findViewById(R.id.settings_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        fragTitle = (TextView) view.findViewById(R.id.settings_fragment_title);

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);
        if (HomeActivity.isReloaded)
            bottomMarginLayout.getLayoutParams().height = 0;
        else
            bottomMarginLayout.getLayoutParams().height = ((HomeActivity) getContext()).dpTopx(65);

        densitycard = (RelativeLayout) view.findViewById(R.id.density_card);
        densityText = (TextView) view.findViewById(R.id.density_value);
        densityText.setText(String.valueOf(100 - (int) (homeActivity.minAudioStrength * 100)));

        densitycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.density_dialog);
                densitySeekbar = (SeekBar) dialog.findViewById(R.id.density_dialog_seekbar);
                densitySeekbar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(homeActivity.themeColor, PorterDuff.Mode.SRC_IN));
                densityTextDialog = (TextView) dialog.findViewById(R.id.density_dialog_value);
                densitySeekbar.setMax(100);
                densitySeekbar.setProgress(Integer.parseInt(densityText.getText().toString()));
                densityTextDialog.setText(String.valueOf(densitySeekbar.getProgress()));
                densitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        homeActivity.minAudioStrength = 1.0f - ((float) progress / (float) 100);
                        homeConfigInfo.getSettings().setMinAudioStrength(homeActivity.minAudioStrength);
                        densityTextDialog.setText(String.valueOf(progress));
                        densityText.setText(String.valueOf(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                dialog.show();

            }
        });

        themeCard = (RelativeLayout) view.findViewById(R.id.theme_card);
        themeColorImg = (ImageView) view.findViewById(R.id.theme_color_img);
        themeColorImg.setBackgroundColor(homeActivity.themeColor);
        themeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(getContext())
                        .setTitle("Choose color")
                        .initialColor(((ColorDrawable) themeColorImg.getBackground()).getColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(9)
                        .showColorPreview(true)
                        .lightnessSliderOnly()
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int color) {

                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int color, Integer[] allColors) {
                                homeConfigInfo.getSettings().setThemeColor(color);
                                homeActivity.themeColor = color;
                                homeActivity.collapsingToolbar.setContentScrimColor(color);
                                homeActivity.customLinearGradient.setStartColor(color);
                                homeActivity.customLinearGradient.invalidate();
                                themeColorImg.setBackgroundColor(color);
                                mCallback2.onColorChanged();
                                if (Build.VERSION.SDK_INT >= 21) {
                                    Window window = ((Activity) (getContext())).getWindow();
                                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                    window.setStatusBarColor(getDarkColor(color));
                                }
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .build()
                        .show();
            }
        });

        albumArtCard = (RelativeLayout) view.findViewById(R.id.album_art_card);
        albumArtToggle = (SwitchCompat) view.findViewById(R.id.album_art_toggle);
        albumArtToggle.setChecked(homeConfigInfo.getSettings().isAlbumArtBackgroundEnabled());
        albumArtCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumArtToggle.toggle();
            }
        });
        albumArtToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                homeConfigInfo.getSettings().setAlbumArtBackgroundEnabled(isChecked);
                mCallback.onAlbumArtBackgroundChangedVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        wifiCard = (RelativeLayout) view.findViewById(R.id.wifi_card);
        wifiToggle = (SwitchCompat) view.findViewById(R.id.wifi_stream_toggle);
        wifiToggle.setChecked(homeConfigInfo.getSettings().isStreamOnlyOnWifiEnabled());
        wifiCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiToggle.toggle();
            }
        });
        wifiToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                homeConfigInfo.getSettings().setStreamOnlyOnWifiEnabled(isChecked);
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

    public int getDarkColor(int color) {
        int darkColor = 0;

        int r = Math.max(Color.red(color) - 25, 0);
        int g = Math.max(Color.green(color) - 25, 0);
        int b = Math.max(Color.blue(color) - 25, 0);

        darkColor = Color.rgb(r, g, b);

        return darkColor;
    }
}
