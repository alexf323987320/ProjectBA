package com.example.alex.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.bakingapp.json.StepJson;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;

import java.util.List;

public class StepFragment extends Fragment{

    public static final String STEP_KEY = "step";
    private static final String PLAYER_CURRENT_POSITION_KEY = "player_current_position";
    public static final String PLAYER_PLAY_WHEN_READY_KEY = "player_play_when_ready";
    public static final String ARG_PLAY_WHEN_READY_KEY = "arg_play_when_ready";

    private StepJson mStepJsonArg;
    private boolean mPlayWhenReadyArg;
    private boolean mIsLand;
    private boolean mIsTablet;
    private SimpleExoPlayer mPlayer;
    private PlayerView mPlayerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mStepJsonArg = (StepJson) arguments.getSerializable(STEP_KEY);
            mPlayWhenReadyArg = arguments.getBoolean(ARG_PLAY_WHEN_READY_KEY, false);
        }
        if (mStepJsonArg == null) {
            throw new IllegalArgumentException("Argument for step fragment is not set");
        }

        mIsLand = getResources().getBoolean(R.bool.isLand);
        mIsTablet = getResources().getBoolean(R.bool.isTablet);

        View rootView = null;
        if (mIsLand && !mIsTablet) {
            rootView = inflater.inflate(R.layout.fragment_step_land, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_step, container, false);
        }

        mPlayerView = rootView.findViewById(R.id.player_pv);

        if (mIsLand && !mIsTablet) {
            InitializeFullScreenModeEvents(rootView);
        } else {
            //fill the text data
            TextView descriptionTv = rootView.findViewById(R.id.description_tv);
            descriptionTv.setText(mStepJsonArg.description);
        }

        return rootView;
    }

    private void InitializeFullScreenModeEvents(View rootView) {
        //hide system bars on click
//        mPlayerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideSystemBars();
//            }
//        });
        //show action bar according full screen mode
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                } else {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                }
            }
        });
    }

    public void hideSystemBars() {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                //View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //start playing on creation and argument mPlayWhenReadyArg = true or on rotation when player was active
        if (savedInstanceState == null && mPlayWhenReadyArg ||
                savedInstanceState != null && savedInstanceState.containsKey(PLAYER_CURRENT_POSITION_KEY)) {
            epStart(savedInstanceState);
        }
    }

    @Override
    public void onDestroyView() {
        epDestroy();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPlayer != null) {
            outState.putLong(PLAYER_CURRENT_POSITION_KEY, mPlayer.getCurrentPosition());
            outState.putBoolean(PLAYER_PLAY_WHEN_READY_KEY, mPlayer.getPlayWhenReady());
        }
    }

    //Called from viewPager onPageSelected. Stops video on all fragments and starts on current.
    public static void onPageSelected(List<Fragment> fragments, StepJson stepJson) {
        StepFragment targetFragment = null;
        for (Fragment fragment: fragments) {
            if (fragment instanceof StepFragment) {
                if (((StepFragment) fragment).mStepJsonArg == stepJson) {
                    targetFragment = (StepFragment) fragment;
                }
                ((StepFragment) fragment).epDestroy();
            }
        }
        if (targetFragment != null) {
            targetFragment.epStart(null);
        }
    }


//***************ExoPlayer***************

    private void epStart(@Nullable Bundle savedInstanceState) {

        //if no video file - do not initialize at all
        if (mStepJsonArg.videoURL.isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.video_not_available), Toast.LENGTH_LONG).show();
            return;
        }

        //enter full screen mode
        if (mIsLand && !mIsTablet) {
            hideSystemBars();
        }

        // Measures bandwidth during playback. Can be null if not required.
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        // Create a default TrackSelector
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

        mPlayerView.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.black));
        mPlayerView.setPlayer(mPlayer);

        Uri uri = Uri.parse(mStepJsonArg.videoURL);
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(getActivity(), getString(R.string.app_name)));
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        // Prepare the player with the source.
        mPlayer.prepare(videoSource);

        mPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                super.onPlayerError(error);
                Toast.makeText(getActivity(), error.getCause().getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(PLAYER_CURRENT_POSITION_KEY)) {
            mPlayer.seekTo(savedInstanceState.getLong(PLAYER_CURRENT_POSITION_KEY));
        }
        mPlayer.setPlayWhenReady(savedInstanceState == null || savedInstanceState.getBoolean(PLAYER_PLAY_WHEN_READY_KEY, false));
    }

    private void epDestroy() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    //***************End ExoPlayer***************

}
