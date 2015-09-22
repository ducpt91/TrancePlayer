package trancemusiccollections.tranceplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import utils.PausableAnimation;

public class MainActivity extends AppCompatActivity {

    private TextView txtSongTitle;
    private TextView txtCurrentPlayingTrack;
    private ImageView ivPlayerDisc;
    private ImageView ivShuffle;
    private ImageView ivMusicLibrary;
    private ImageView ivRepeat;
    private TextView txtSongDuration;
    private SeekBar sbPlayback;
    private ImageView ivSkipPrevious;
    private ImageView ivFastRewind;
    private ImageView ivPlay;
    private ImageView ivPause;
    private ImageView ivFastForward;
    private ImageView ivSkipNext;

    private PausableAnimation mAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getViews();
        initialAnimation();
        setEvents();
    }

    /**
     * Get all views.
     */
    private void getViews() {
        txtSongTitle = (TextView) findViewById(R.id.txtSongTitle);
        txtCurrentPlayingTrack = (TextView) findViewById(R.id.txtCurrentPlayingTrack);
        ivPlayerDisc = (ImageView) findViewById(R.id.ivPlayerDisc);
        ivShuffle = (ImageView) findViewById(R.id.ivShuffle);
        ivMusicLibrary = (ImageView) findViewById(R.id.ivMusicLibrary);
        ivRepeat = (ImageView) findViewById(R.id.ivRepeat);
        txtSongDuration = (TextView) findViewById(R.id.txtSongDuration);
        sbPlayback = (SeekBar) findViewById(R.id.sbPlayback);
        ivSkipPrevious = (ImageView) findViewById(R.id.ivSkipPrevious);
        ivFastRewind = (ImageView) findViewById(R.id.ivFastRewind);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        ivPause = (ImageView) findViewById(R.id.ivPause);
        ivFastForward = (ImageView) findViewById(R.id.ivFastForward);
        ivSkipNext = (ImageView) findViewById(R.id.ivSkipNext);
    }

    /**
     * Initial animation for player disc.
     */
    private void initialAnimation() {
        mAnimation = new PausableAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setDuration(2500);
        mAnimation.setRepeatMode(Animation.RESTART);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.pause();
        ivPlayerDisc.setAnimation(mAnimation);
    }

    /**
     * Set events for views.
     */
    private void setEvents() {
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnimation.resume();
                ivPlay.setVisibility(View.GONE);
                ivPause.setVisibility(View.VISIBLE);
            }
        });
        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnimation.pause();
                ivPlay.setVisibility(View.VISIBLE);
                ivPause.setVisibility(View.GONE);
            }
        });
    }
}
