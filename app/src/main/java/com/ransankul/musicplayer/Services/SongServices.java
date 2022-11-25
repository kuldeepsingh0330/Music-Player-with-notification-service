package com.ransankul.musicplayer.Services;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_MUTABLE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import static com.ransankul.musicplayer.Activity.PlayerActivity.binding;
import static com.ransankul.musicplayer.Activity.PlayerActivity.songServices;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.ransankul.musicplayer.Activity.MainActivity;
import com.ransankul.musicplayer.Activity.PlayerActivity;
import com.ransankul.musicplayer.Frgement.MyMusic;
import com.ransankul.musicplayer.Model.Global;
import com.ransankul.musicplayer.R;

import java.io.IOException;

public class SongServices extends Service implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnCompletionListener {

    public MediaPlayer mediaPlayer = new MediaPlayer();
    public static Runnable runnable;
    private final IBinder iBinder = new Mybinder();
    private MediaSessionCompat mediaSessionCompat;
    public static AudioManager audioManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onAudioFocusChange(int i) {
        if (i <= 0) {
            binding.btnPlayPause.setIconResource(R.drawable.pause);
            MyMusic.fragmentMyMusicBinding.nowPlayPause.setIconResource(R.drawable.pause);
            showNotification(R.drawable.pause);
            PlayerActivity.isPlaying = false;
            mediaPlayer.pause();
        } else {
            binding.btnPlayPause.setIconResource(R.drawable.playy);
            MyMusic.fragmentMyMusicBinding.nowPlayPause.setIconResource(R.drawable.playy);
            showNotification(R.drawable.playy);
            PlayerActivity.isPlaying = true;
            mediaPlayer.start();
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mediaSessionCompat = new MediaSessionCompat(this, "MY MUSIC");
        return iBinder;
    }

    public class Mybinder extends Binder {
        public SongServices getServices() {
            return SongServices.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showNotification(int playPauseBtn) {


        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent playerPendingIntent = PendingIntent.getActivity(this, 0, intent,
                FLAG_MUTABLE);

        Intent prevIntent = new Intent(getBaseContext(), NotificationReceiver.class).setAction(ApplicationClass.PREVIOUS);
        PendingIntent prevPendindIntent = PendingIntent
                .getBroadcast(getBaseContext(), 0, prevIntent, FLAG_IMMUTABLE);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(ApplicationClass.NEXT);
        PendingIntent nextPendindIntent = PendingIntent
                .getBroadcast(this, 0, nextIntent, FLAG_IMMUTABLE);

        Intent playIntent = new Intent(this, NotificationReceiver.class).setAction(ApplicationClass.PLAY);
        PendingIntent playPendindIntent = PendingIntent
                .getBroadcast(this, 0, playIntent, FLAG_IMMUTABLE);

        Intent exitIntent = new Intent(this, NotificationReceiver.class).setAction(ApplicationClass.EXIT);
        PendingIntent exitPendindIntent = PendingIntent
                .getBroadcast(this, 0, exitIntent, FLAG_IMMUTABLE);


        Notification notification = new NotificationCompat.Builder(this, ApplicationClass.CHANNEL_ID)
                .setContentIntent(playerPendingIntent)
                .setContentTitle(PlayerActivity.playersongArrayList.get(PlayerActivity.poisition).getTitle())
                .setContentText(PlayerActivity.playersongArrayList.get(PlayerActivity.poisition).getArtist())
                .setSmallIcon(R.drawable.music_icon)
                .setLargeIcon(setImageNotification())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true).setNotificationSilent()
                .addAction(R.drawable.previous, "previous", prevPendindIntent)
                .addAction(playPauseBtn, "play", playPendindIntent)
                .addAction(R.drawable.next, "next", nextPendindIntent)
                .addAction(R.drawable.exit, "exit", exitPendindIntent)
                .build();

        startForeground(82, notification);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void playMusic() {
        if (songServices.mediaPlayer == null)
            songServices.mediaPlayer = new MediaPlayer();

        songServices.mediaPlayer.reset();
        try {
            songServices.mediaPlayer.setDataSource(PlayerActivity.playersongArrayList.get(PlayerActivity.poisition).getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            songServices.mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        songServices.showNotification(R.drawable.playy);
        binding.tvCurrTime.setText(Global.formatDuration((long) songServices.mediaPlayer.getCurrentPosition()));
        binding.tvTotalTime.setText(Global.formatDuration((long) songServices.mediaPlayer.getDuration()));
        binding.seekBarPlayer.setProgress(0);
        binding.seekBarPlayer.setMax(songServices.mediaPlayer.getDuration());
        PlayerActivity.nowPlayingSongId = PlayerActivity.playersongArrayList.get(PlayerActivity.poisition).getId();
    }

    public void setUpSeekBar() {
        runnable = new Runnable() {
            @Override
            public void run() {
                binding.tvCurrTime.setText(Global.formatDuration((long) songServices.mediaPlayer.getCurrentPosition()));
                binding.seekBarPlayer.setProgress(mediaPlayer.getCurrentPosition());
                new Handler(Looper.getMainLooper()).postDelayed(runnable, 200);
            }
        };
        new Handler(Looper.getMainLooper()).postDelayed(runnable, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        NotificationReceiver.preNextSong(songServices.getBaseContext(), true);

    }

    private Bitmap setImageNotification() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(PlayerActivity.playersongArrayList.get(PlayerActivity.poisition).getPath());
        byte[] image = retriever.getEmbeddedPicture();
        Bitmap img = null;
        if (image != null) {
            img = BitmapFactory.decodeByteArray(image, 0, image.length);
        } else img = BitmapFactory.decodeResource(getResources(), R.drawable.music_icon);

        return img;
    }


}
