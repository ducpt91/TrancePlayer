package trancemusiccollections.tranceplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.logging.LogRecord;

import utils.PausableAnimation;
import utils.Utils;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private TextView txtSongTitle;
    private TextView txtCurrentPlayingTrack;
    private ImageView ivPlayerDisc;
    private ImageView ivShuffle;
    private ImageView ivMusicLibrary;
    private ImageView ivRepeat;
    private TextView txtCurrentDuration;
    private TextView txtSongDuration;
    private SeekBar sbPlayback;
    private ImageView ivSkipPrevious;
    private ImageView ivFastRewind;
    private ImageView ivPlay;
    private ImageView ivPause;
    private ImageView ivFastForward;
    private ImageView ivSkipNext;

    private PausableAnimation mAnimation;
    private static final String URL = "http://dl7.mp3.zdn.vn/fsdd1131lwwjA/5da789e37afff8cf96725891b3666dcd/56010a80/2015/04/25/1/6/167fc5d627166e755e745e9f5941f40e.mp3?filename=Say%20You%20Do%20-%20Tien%20Tien.mp3";
    private MediaPlayer mMediaPlayer;
    private boolean mHasMediaPrepared = false;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getViews();
        initialViewAttributes();
        initialAnimation();
        initialMediaPlayer();
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
        txtCurrentDuration = (TextView) findViewById(R.id.txtCurrentDuration);
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
     * Initial views' attributes.
     */
    private void initialViewAttributes() {
        txtSongTitle.setSelected(true);
        txtCurrentPlayingTrack.setSelected(true);
        sbPlayback.setOnSeekBarChangeListener(this);
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
     * Initial media player.
     */
    private void initialMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }


    /**
     * Set events for views.
     */
    private void setEvents() {
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivPlay.setVisibility(View.GONE);
                ivPause.setVisibility(View.VISIBLE);
                if (mHasMediaPrepared) {
                    mMediaPlayer.start();
                    mAnimation.resume();
                } else {
                    new LoadMedia().execute(URL);
                }
            }
        });
        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnimation.pause();
                ivPlay.setVisibility(View.VISIBLE);
                ivPause.setVisibility(View.GONE);
                mMediaPlayer.pause();
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class LoadMedia extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                mMediaPlayer.setDataSource(params[0]);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            mMediaPlayer.start();
            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mHasMediaPrepared = result;
            if (result) {
                txtSongTitle.setText("Say You Do - Tien Tien");
                txtCurrentPlayingTrack.setText("Say You Do - Tien Tien");
                mAnimation.resume();
                mHandler.postDelayed(mUpdateTimeTask, 100);
            } else {
                mAnimation.pause();
                ivPlay.setVisibility(View.VISIBLE);
                ivPause.setVisibility(View.GONE);
            }
        }
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mMediaPlayer.getDuration();
            long currentDuration = mMediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            txtSongDuration.setText(Utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            txtCurrentDuration.setText(Utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (Utils.getProgressPercentage(currentDuration, totalDuration));
            sbPlayback.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };
}
