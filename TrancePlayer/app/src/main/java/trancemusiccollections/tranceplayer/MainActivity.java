package trancemusiccollections.tranceplayer;

import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import adapter.SongAdapter;
import object.Song;
import object.Track;
import utils.Constants;
import utils.PausableAnimation;
import utils.Utils;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private final int SEEK_TIME = 10000;
    private ListView lvSongs;
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
    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> listSongs;
    private Song currentSong;
    private Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long currentDuration = mMediaPlayer.getCurrentPosition();

//            txtCurrentPlayingTrack.setText(currentSong.getListTracks().get(0).getName());
            // Displaying current duration
            txtCurrentDuration.setText(Utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (Utils.getProgressPercentage(currentDuration, currentSong.getDuration()));
            sbPlayback.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };
    private boolean isSuffle;
    private boolean isRepeat;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getViews();
        initialViewAttributes();
        initialAnimation();
        initialMediaPlayer();
        setEvents();
        ivMusicLibrary.performClick();
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
        ivShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSuffle) {
                    ivShuffle.setImageResource(R.drawable.ic_shuffle_white_36dp);
                    isSuffle = false;
                } else {
                    ivShuffle.setImageResource(R.drawable.ic_shuffle_green_36dp);
                    isSuffle = true;
                }
            }
        });
        ivMusicLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View contentView = View.inflate(MainActivity.this, R.layout.list_songs_selection, null);
                final Dialog songDialog = new Dialog(MainActivity.this);
                songDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                songDialog.setCancelable(false);
                TextView txtClose = (TextView) contentView.findViewById(R.id.txtClose);
                txtClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        songDialog.dismiss();
                    }
                });
                ProgressBar pbLoading = (ProgressBar) contentView.findViewById(R.id.pbLoading);
                lvSongs = (ListView) contentView.findViewById(R.id.lvSongs);
                lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                        position = pos;
                        SongAdapter adapter = (SongAdapter) lvSongs.getAdapter();
                        if (currentSong != null) {
                            // Reset playing status of current song
                            currentSong.setPlaying(false);
                        }
                        currentSong = adapter.getSong(position);
                        currentSong.setPlaying(true);
                        songDialog.dismiss();
                        playMedia();
                    }
                });
                getSongs(pbLoading);
                songDialog.setContentView(contentView);
                songDialog.show();
            }
        });
        ivRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRepeat) {
                    ivRepeat.setImageResource(R.drawable.ic_repeat_white_36dp);
                    isRepeat = false;
                } else {
                    ivRepeat.setImageResource(R.drawable.ic_repeat_green_36dp);
                    isRepeat = true;
                }
            }
        });
        ivSkipPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SongAdapter adapter = (SongAdapter) lvSongs.getAdapter();
                int size = adapter.getCount();
                if (currentSong != null) {
                    // Reset playing status of current song
                    currentSong.setPlaying(false);
                }
                if (position > 0) {
                    position--;
                } else {
                    position = size - 1;
                }
                currentSong = adapter.getSong(position);
                currentSong.setPlaying(true);
                playMedia();
            }
        });
        ivFastRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = mMediaPlayer.getCurrentPosition();
                if (currentPosition - SEEK_TIME > 0) {
                    mMediaPlayer.seekTo(currentPosition - SEEK_TIME);
                } else {
                    mMediaPlayer.seekTo(0);
                }
            }
        });
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivPlay.setVisibility(View.GONE);
                ivPause.setVisibility(View.VISIBLE);
                mMediaPlayer.start();
                mAnimation.resume();
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
        ivFastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = mMediaPlayer.getCurrentPosition();
                if (currentPosition + SEEK_TIME < currentSong.getDuration()) {
                    mMediaPlayer.seekTo(currentPosition + SEEK_TIME);
                } else {
                    mMediaPlayer.seekTo(currentSong.getDuration());
                }
            }
        });
        ivSkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SongAdapter adapter = (SongAdapter) lvSongs.getAdapter();
                int size = adapter.getCount();
                if (currentSong != null) {
                    // Reset playing status of current song
                    currentSong.setPlaying(false);
                }
                if (position < size - 1) {
                    position++;
                } else {
                    position = 0;
                }
                currentSong = adapter.getSong(position);
                currentSong.setPlaying(true);
                playMedia();
            }
        });
    }

    private void getSongs(final ProgressBar pbLoading) {
        pbLoading.setVisibility(View.VISIBLE);
        if (listSongs == null) {
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, Constants.SONGS_URL, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        listSongs = new ArrayList<>();
                        JSONObject songObject;
                        Song song;
                        JSONArray trackArray;
                        JSONObject trackObject;
                        ArrayList<Track> listTrack;
                        Track track;
                        for (int i = 0; i < response.length(); i++) {
                            songObject = response.getJSONObject(i);
                            song = new Song();
                            song.setId(songObject.getInt(Constants.KEY_ID));
                            song.setUrl(songObject.getString(Constants.KEY_URL));
                            song.setTitle(songObject.getString(Constants.KEY_TITLE));
                            song.setDuration(songObject.getInt(Constants.KEY_DURATION));
                            trackArray = songObject.getJSONArray(Constants.KEY_TRACK);
                            listTrack = new ArrayList<>();
                            for (int j = 0; j < trackArray.length(); j++) {
                                trackObject = trackArray.getJSONObject(j);
                                track = new Track();
                                track.setTime(trackObject.getString(Constants.KEY_TIME));
                                track.setName(trackObject.getString(Constants.KEY_NAME));
                                listTrack.add(track);
                            }
                            song.setListTracks(listTrack);
                            listSongs.add(song);
                        }
                        lvSongs.setAdapter(new SongAdapter(MainActivity.this, listSongs));
                        pbLoading.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        pbLoading.setVisibility(View.GONE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pbLoading.setVisibility(View.GONE);
                }
            });
            MySingleton.getInstance(this).addToRequestQueue(request);
        } else {
            lvSongs.setAdapter(new SongAdapter(MainActivity.this, listSongs));
            pbLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        SongAdapter adapter = (SongAdapter) lvSongs.getAdapter();
        int size = adapter.getCount();
        if (currentSong != null) {
            currentSong.setPlaying(false);
        }
        if (!isRepeat && !isSuffle) {
            if (position < (size - 1)) {
                position++;
            } else {
                position = 0;
            }
        } else if (isSuffle) {
            Random rand = new Random();
            position = rand.nextInt((size - 1) + 1);
        }
        currentSong = adapter.getSong(position);
        currentSong.setPlaying(true);
        adapter.notifyDataSetChanged();
        playMedia();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = currentSong.getDuration();
        int currentPosition = Utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mMediaPlayer.seekTo(currentPosition);

        // update timer progress again
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private void playMedia() {
        boolean result = false;
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(currentSong.getUrl());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result) {
            txtSongTitle.setText(currentSong.getTitle());
            txtSongDuration.setText(Utils.milliSecondsToTimer(currentSong.getDuration()));
            mAnimation.resume();
            ivPlay.setVisibility(View.GONE);
            ivPause.setVisibility(View.VISIBLE);
            mHandler.postDelayed(mUpdateTimeTask, 100);
        } else {
            mAnimation.pause();
            ivPlay.setVisibility(View.VISIBLE);
            ivPause.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
    }
}
