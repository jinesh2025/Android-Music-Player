/*
 * This is the source code of Music Player Lite for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @CodeCrunch Team, 2016-2017.
 */
package co.codecrunch.musicplayerlite.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.codecrunch.musicplayerlite.R;
import co.codecrunch.musicplayerlite.adapter.DrawerAdapter;
import co.codecrunch.musicplayerlite.fragments.FragmentAllSongs;
import co.codecrunch.musicplayerlite.fragments.FragmentEqualizer;
import co.codecrunch.musicplayerlite.fragments.FragmentFeedBack;
import co.codecrunch.musicplayerlite.fragments.FragmentFevorite;
import co.codecrunch.musicplayerlite.fragments.FragmentLibrary;
import co.codecrunch.musicplayerlite.fragments.FragmentSettings;
import co.codecrunch.musicplayerlite.manager.MediaController;
import co.codecrunch.musicplayerlite.manager.MusicPreferance;
import co.codecrunch.musicplayerlite.manager.NotificationManager;
import co.codecrunch.musicplayerlite.models.DrawerItem;
import co.codecrunch.musicplayerlite.models.SongDetail;
import co.codecrunch.musicplayerlite.phonemidea.MusicPlayerUtility;
import co.codecrunch.musicplayerlite.recyclerviewutils.ItemClickSupport;
import co.codecrunch.musicplayerlite.slidinguppanelhelper.SlidingUpPanelLayout;
import co.codecrunch.musicplayerlite.uicomponent.PlayPauseView;
import co.codecrunch.musicplayerlite.uicomponent.Slider;
import co.codecrunch.musicplayerlite.utility.LogWriter;
import co.codecrunch.musicplayerlite.utility.PermissionHelper;
import co.codecrunch.musicplayerlite.utility.SystemBarTintManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class MusicPlayerBaseActivity extends ActionBarActivity implements View.OnClickListener, Slider.OnValueChangedListener,
        NotificationManager.NotificationCenterDelegate {

    private static final String TAG = "ActivityDMPlayerBase";
    private Context context;
    private SharedPreferences sharedPreferences;
    private ActionBarDrawerToggle mDrawerToggle;
    private int theme;

    private FrameLayout statusBar;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;

    private RecyclerView recyclerViewDrawer;
    private RecyclerView.Adapter adapterDrawer;

    private SlidingUpPanelLayout mLayout;
    private RelativeLayout slidepanelchildtwo_topviewone;
    private RelativeLayout slidepanelchildtwo_topviewtwo;
    private boolean isExpand = false;

    private DisplayImageOptions options;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageView songAlbumbg;
    private ImageView img_bottom_slideone;
    private ImageView img_bottom_slidetwo;
    private TextView txt_playesongname;
    private TextView txt_songartistname;
    private TextView txt_playesongname_slidetoptwo;
    private TextView txt_songartistname_slidetoptwo;
    private TextView txt_timeprogress;
    private TextView txt_timetotal;
    private ImageView imgbtn_backward;
    private ImageView imgbtn_forward;
    private ImageView imgbtn_toggle;
    private ImageView imgbtn_suffel;
    private ImageView img_Favorite;
    private PlayPauseView btn_playpause;
    private PlayPauseView btn_playpausePanel;
    private Slider audio_progress;
    private boolean isDragingStart = false;
    private int TAG_Observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set your theme first
        context = MusicPlayerBaseActivity.this;
        theme();

        //Set your Layout view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmplayerbase);

        //System bar color set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            systembartiteniam();
        }

        toolbarStatusBar();
        //navigationDrawer();
        initiSlidingUpPanel();
        setFragment(1);

        getIntentData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addObserver();
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		    if(checkSelfPermission( Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
	            loadAlreadyPlayng();
		    else
			    (new PermissionHelper(MusicPlayerBaseActivity.this)).checkPermission();
	    }
	    else
		    loadAlreadyPlayng();
    }

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case 124: {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					if(checkSelfPermission( Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
						loadAlreadyPlayng();
				}
			}
			break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
    @Override
    protected void onPause() {
        super.onPause();
        removeObserver();
    }

    @Override
    protected void onDestroy() {
        removeObserver();
        if (MediaController.getInstance().isAudioPaused()) {
            MediaController.getInstance().cleanupPlayer(context, true, true);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isExpand) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
            overridePendingTransition(0, 0);
            finish();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bottombar_play:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    PlayPauseEvent(v);
                break;

            case R.id.btn_play:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    PlayPauseEvent(v);
                break;

            case R.id.btn_forward:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    MediaController.getInstance().playNextSong();
                break;

            case R.id.btn_backward:
                if (MediaController.getInstance().getPlayingSongDetail() != null)
                    MediaController.getInstance().playPreviousSong();
                break;

            case R.id.btn_suffel:
                v.setSelected(v.isSelected() ? false : true);
                MediaController.getInstance().shuffleMusic = v.isSelected() ? true : false;
                MusicPreferance.setShuffel(context, (v.isSelected() ? true : false));
                MediaController.getInstance().shuffleList(MusicPreferance.playlist);
                MusicPlayerUtility.changeColorSet(context, (ImageView) v, v.isSelected());
                break;

            case R.id.btn_toggle:
                v.setSelected(v.isSelected() ? false : true);
                MediaController.getInstance().repeatMode = v.isSelected() ? 1 : 0;
                MusicPreferance.setRepeat(context, (v.isSelected() ? 1 : 0));
                MusicPlayerUtility.changeColorSet(context, (ImageView) v, v.isSelected());
                break;

            case R.id.bottombar_img_Favorite:
                if (MediaController.getInstance().getPlayingSongDetail() != null) {
                    MediaController.getInstance().storeFavoritePlay(context, MediaController.getInstance().getPlayingSongDetail(), v.isSelected() ? 0 : 1);
                    v.setSelected(v.isSelected() ? false : true);
                    MusicPlayerUtility.animateHeartButton(v);
                    findViewById(R.id.ivLike).setSelected(v.isSelected() ? true : false);
                    MusicPlayerUtility.animatePhotoLike(findViewById(R.id.vBgLike), findViewById(R.id.ivLike));
                }
                break;

            default:
                break;
        }
    }

    /**
     * Get intent data from music choose option
     */
    private void getIntentData() {
        try {
            Uri data = getIntent().getData();
            if (data != null) {
                if (data.getScheme().equalsIgnoreCase("file")) {
                    String path = data.getPath().toString();
                    if (!TextUtils.isEmpty(path)) {
                        MediaController.getInstance().cleanupPlayer(context, true, true);
                        MusicPreferance.getPlaylist(context, path);
                        updateTitle(false);
                        MediaController.getInstance().playAudio(MusicPreferance.playingSongDetail);
                        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    }
                }
                if (data.getScheme().equalsIgnoreCase("http"))
                    LogWriter.info(TAG, data.getPath().toString());
                if (data.getScheme().equalsIgnoreCase("content"))
                    LogWriter.info(TAG, data.getPath().toString());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toolbarStatusBar() {
        statusBar = (FrameLayout) findViewById(R.id.statusBar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
	    toolbar.setTitle(getResources().getString(R.string.app_name));
    }


    private void initiSlidingUpPanel() {
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        songAlbumbg = (ImageView) findViewById(R.id.image_songAlbumbg_mid);
        img_bottom_slideone = (ImageView) findViewById(R.id.img_bottom_slideone);
        img_bottom_slidetwo = (ImageView) findViewById(R.id.img_bottom_slidetwo);

        txt_timeprogress = (TextView) findViewById(R.id.slidepanel_time_progress);
        txt_timetotal = (TextView) findViewById(R.id.slidepanel_time_total);
        imgbtn_backward = (ImageView) findViewById(R.id.btn_backward);
        imgbtn_forward = (ImageView) findViewById(R.id.btn_forward);
        imgbtn_toggle = (ImageView) findViewById(R.id.btn_toggle);
        imgbtn_suffel = (ImageView) findViewById(R.id.btn_suffel);
        btn_playpause = (PlayPauseView) findViewById(R.id.btn_play);
        audio_progress = (Slider) findViewById(R.id.audio_progress_control);
        btn_playpausePanel = (PlayPauseView) findViewById(R.id.bottombar_play);
        img_Favorite = (ImageView) findViewById(R.id.bottombar_img_Favorite);

        TypedValue typedvaluecoloraccent = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedvaluecoloraccent, true);
        final int coloraccent = typedvaluecoloraccent.data;
        audio_progress.setBackgroundColor(coloraccent);
        audio_progress.setValue(0);

        audio_progress.setOnValueChangedListener(this);
        imgbtn_backward.setOnClickListener(this);
        imgbtn_forward.setOnClickListener(this);
        imgbtn_toggle.setOnClickListener(this);
        imgbtn_suffel.setOnClickListener(this);
        img_Favorite.setOnClickListener(this);

        btn_playpausePanel.Pause();
        btn_playpause.Pause();

        txt_playesongname = (TextView) findViewById(R.id.txt_playesongname);
        txt_songartistname = (TextView) findViewById(R.id.txt_songartistname);
        txt_playesongname_slidetoptwo = (TextView) findViewById(R.id.txt_playesongname_slidetoptwo);
        txt_songartistname_slidetoptwo = (TextView) findViewById(R.id.txt_songartistname_slidetoptwo);

        slidepanelchildtwo_topviewone = (RelativeLayout) findViewById(R.id.slidepanelchildtwo_topviewone);
        slidepanelchildtwo_topviewtwo = (RelativeLayout) findViewById(R.id.slidepanelchildtwo_topviewtwo);

        slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
        slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);

        slidepanelchildtwo_topviewone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

            }
        });

        slidepanelchildtwo_topviewtwo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

            }
        });

        ((PlayPauseView) findViewById(R.id.bottombar_play)).setOnClickListener(this);
        ((PlayPauseView) findViewById(R.id.btn_play)).setOnClickListener(this);

        imgbtn_toggle.setSelected((MusicPreferance.getRepeat(context) == 1) ? true : false);
        MediaController.getInstance().shuffleMusic = imgbtn_toggle.isSelected() ? true : false;
        MusicPlayerUtility.changeColorSet(context, (ImageView) imgbtn_toggle, imgbtn_toggle.isSelected());

        imgbtn_suffel.setSelected(MusicPreferance.getShuffel(context) ? true : false);
        MediaController.getInstance().repeatMode = imgbtn_suffel.isSelected() ? 1 : 0;
        MusicPlayerUtility.changeColorSet(context, (ImageView) imgbtn_suffel, imgbtn_suffel.isSelected());

        MediaController.getInstance().shuffleList(MusicPreferance.playlist);

        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);

                if (slideOffset == 0.0f) {
                    isExpand = false;
                    slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
                    slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);
                } else if (slideOffset > 0.0f && slideOffset < 1.0f) {
                    // if (isExpand) {
                    // slidepanelchildtwo_topviewone.setAlpha(1.0f);
                    // slidepanelchildtwo_topviewtwo.setAlpha(1.0f -
                    // slideOffset);
                    // } else {
                    // slidepanelchildtwo_topviewone.setAlpha(1.0f -
                    // slideOffset);
                    // slidepanelchildtwo_topviewtwo.setAlpha(1.0f);
                    // }

                } else {
                    isExpand = true;
                    slidepanelchildtwo_topviewone.setVisibility(View.INVISIBLE);
                    slidepanelchildtwo_topviewtwo.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
                isExpand = true;
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
                isExpand = false;
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });

    }

    public void setFragment(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (position) {
            case 0:
                sharedPreferences.edit().putInt("FRAGMENT", position).apply();
                FragmentAllSongs fragmentallsongs = new FragmentAllSongs();
                fragmentTransaction.replace(R.id.fragment, fragmentallsongs);
                fragmentTransaction.commit();
                toolbar.setTitle("All Songs");
                break;
            case 1:
                sharedPreferences.edit().putInt("FRAGMENT", position).apply();
                FragmentLibrary fragmentlibrary = new FragmentLibrary();
                fragmentTransaction.replace(R.id.fragment, fragmentlibrary);
                fragmentTransaction.commit();
                toolbar.setTitle(getResources().getString(R.string.app_name));
                break;
            case 2:
                sharedPreferences.edit().putInt("FRAGMENT", position).apply();
                FragmentFevorite fragmentfevorite = new FragmentFevorite();
                fragmentTransaction.replace(R.id.fragment, fragmentfevorite);
                fragmentTransaction.commit();
                toolbar.setTitle("Favorite");
                break;

            case 3:
                sharedPreferences.edit().putInt("FRAGMENT", position).apply();
                FragmentSettings fragmentsettings = new FragmentSettings();
                fragmentTransaction.replace(R.id.fragment, fragmentsettings);
                fragmentTransaction.commit();
                toolbar.setTitle("Settings");
                break;

            case 4:
                sharedPreferences.edit().putInt("FRAGMENT", position).apply();
                FragmentEqualizer fragmentequalizer = new FragmentEqualizer();
                fragmentTransaction.replace(R.id.fragment, fragmentequalizer);
                fragmentTransaction.commit();
                toolbar.setTitle("Equilizer");
                break;

            case 5:
                sharedPreferences.edit().putInt("FRAGMENT", position).apply();
                FragmentFeedBack fragmentfeedback = new FragmentFeedBack();
                fragmentTransaction.replace(R.id.fragment, fragmentfeedback);
                fragmentTransaction.commit();
                toolbar.setTitle("Send feedback");
                break;
        }
    }

    //Catch  theme changed from settings
    public void theme() {
        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        theme = 0;
        //theme = sharedPreferences.getInt("THEME", 0);
        MusicPlayerUtility.settingTheme(context, theme);
    }

    private void loadImageLoaderOption() {
        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }

    }

    private void loadAlreadyPlayng() {
        SongDetail mSongDetail = MusicPreferance.getLastSong(context);
        ArrayList<SongDetail> playlist = MusicPreferance.getPlaylist(context);
        if (mSongDetail != null) {
            updateTitle(false);
        }
        MediaController.getInstance().checkIsFavorite(context, mSongDetail, img_Favorite);
    }

    public void addObserver() {
        TAG_Observer = MediaController.getInstance().generateObserverTag();
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioDidReset);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioPlayStateChanged);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioDidStarted);
        NotificationManager.getInstance().addObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().addObserver(this, NotificationManager.newaudioloaded);
    }

    public void removeObserver() {
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioDidReset);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioPlayStateChanged);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioDidStarted);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.audioProgressDidChanged);
        NotificationManager.getInstance().removeObserver(this, NotificationManager.newaudioloaded);
    }

    public void loadSongsDetails(SongDetail mDetail) {
        String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
        imageLoader.displayImage(contentURI, songAlbumbg, options, animateFirstListener);
        imageLoader.displayImage(contentURI, img_bottom_slideone, options, animateFirstListener);
        imageLoader.displayImage(contentURI, img_bottom_slidetwo, options, animateFirstListener);

        txt_playesongname.setText(mDetail.getTitle());
        txt_songartistname.setText(mDetail.getArtist());
        txt_playesongname_slidetoptwo.setText(mDetail.getTitle());
        txt_songartistname_slidetoptwo.setText(mDetail.getArtist());

        if (txt_timetotal != null) {
            long duration = Long.valueOf(mDetail.getDuration());
            txt_timetotal.setText(duration != 0 ? String.format("%d:%02d", duration / 60, duration % 60) : "-:--");
        }
        updateProgress(mDetail);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationManager.audioDidStarted || id == NotificationManager.audioPlayStateChanged || id == NotificationManager.audioDidReset) {
            updateTitle(id == NotificationManager.audioDidReset && (Boolean) args[1]);
        } else if (id == NotificationManager.audioProgressDidChanged) {
            SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail();
            updateProgress(mSongDetail);
        }
    }

    @Override
    public void newSongLoaded(Object... args) {
        MediaController.getInstance().checkIsFavorite(context, (SongDetail) args[0], img_Favorite);
    }


    private void updateTitle(boolean shutdown) {
        SongDetail mSongDetail = MediaController.getInstance().getPlayingSongDetail();
        if (mSongDetail == null && shutdown) {
            return;
        } else {
            updateProgress(mSongDetail);
            if (MediaController.getInstance().isAudioPaused()) {
                btn_playpausePanel.Pause();
                btn_playpause.Pause();
            } else {
                btn_playpausePanel.Play();
                btn_playpause.Play();
            }
            SongDetail audioInfo = MediaController.getInstance().getPlayingSongDetail();
            loadSongsDetails(audioInfo);

            if (txt_timetotal != null) {
                long duration = Long.valueOf(audioInfo.getDuration());
                txt_timetotal.setText(duration != 0 ? String.format("%d:%02d", duration / 60, duration % 60) : "-:--");
            }
        }
    }

    private void updateProgress(SongDetail mSongDetail) {
        if (audio_progress != null) {
            // When SeekBar Draging Don't Show Progress
            if (!isDragingStart) {
                // Progress Value comming in point it range 0 to 1
                audio_progress.setValue((int) (mSongDetail.audioProgress * 100));
            }
            String timeString = String.format("%d:%02d", mSongDetail.audioProgressSec / 60, mSongDetail.audioProgressSec % 60);
            txt_timeprogress.setText(timeString);
        }
    }

    private void PlayPauseEvent(View v) {
        if (MediaController.getInstance().isAudioPaused()) {
            MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingSongDetail());
            ((PlayPauseView) v).Play();
        } else {
            MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingSongDetail());
            ((PlayPauseView) v).Pause();
        }
    }

    @Override
    public void onValueChanged(int value) {
        MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingSongDetail(), (float) value / 100);
    }


    private void systembartiteniam() {
        try {
            setTranslucentStatus(true);
            TypedValue typedValueStatusBarColor = new TypedValue();
            getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValueStatusBarColor, true);
            final int colorStatusBar = typedValueStatusBarColor.data;

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(colorStatusBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
