package com.ransankul.musicplayer.Activity;

import static com.ransankul.musicplayer.Frgement.Favourite.songArrayList;
import static com.ransankul.musicplayer.R.drawable.music_icon;
import static com.ransankul.musicplayer.Services.SongServices.runnable;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ransankul.musicplayer.Frgement.Favourite;
import com.ransankul.musicplayer.Frgement.MyMusic;
import com.ransankul.musicplayer.Frgement.Playlist;
import com.ransankul.musicplayer.Model.Global;
import com.ransankul.musicplayer.Model.Song;
import com.ransankul.musicplayer.R;
import com.ransankul.musicplayer.Services.SongServices;
import com.ransankul.musicplayer.databinding.ActivityPlayerBinding;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Collections;

public class PlayerActivity extends AppCompatActivity implements ServiceConnection , MediaPlayer.OnCompletionListener {

    @SuppressLint("StaticFieldLeak")
    public static ActivityPlayerBinding binding;
    public static boolean repeat = false;
    public static SongServices songServices;
    public static ArrayList<Song> playersongArrayList;
    public static int poisition = 0;
    public static boolean isPlaying = false;
    public static String className = "";
    public static String nowPlayingSongId = "";
    public static boolean isFavourite  = false;
    public static int favouriteIndex = -1;

    public static boolean min15 = false;
    public static boolean min30 = false;
    public static boolean min45 = false;
    public static boolean min60 = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setTheme(R.style.Theme_MusicPlayer);
        setContentView(binding.getRoot());

       initializationMediaPlayer();

        binding.btnPlayPause.setOnClickListener(view -> {
            if(isPlaying) {
                binding.btnPlayPause.setIconResource(R.drawable.pause);
                MyMusic.fragmentMyMusicBinding.nowPlayPause.setIconResource(R.drawable.pause);
                songServices.showNotification(R.drawable.pause);
                songServices.mediaPlayer.pause();
                isPlaying = false;
            }else{
                binding.btnPlayPause.setIconResource(R.drawable.playy);
                MyMusic.fragmentMyMusicBinding.nowPlayPause.setIconResource(R.drawable.playy);
                songServices.showNotification(R.drawable.playy);
                songServices.mediaPlayer.start();
                isPlaying = true;
            }
        });

        binding.previous.setOnClickListener(view -> preNextSong(false));

        binding.next.setOnClickListener(view -> preNextSong(true));

        binding.shuffle.setOnClickListener(view -> {
            songServices.mediaPlayer.release();
            songServices.mediaPlayer = null;
            poisition = 0;
            playersongArrayList.clear();
            playersongArrayList.addAll(MyMusic.songArrayListAll);
            Collections.shuffle(playersongArrayList);
            setLayout();
            playMusic();
        });

        binding.seekBarPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    songServices.mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.repeatButtonPlayer.setOnClickListener(view -> {
            if(!repeat){
                repeat = true;
                binding.repeatButtonPlayer.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.purple_500));
            }else{
                repeat = false;
                binding.repeatButtonPlayer.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.skyBlue));
            }
        });

        binding.backBtnPlayerActivity.setOnClickListener(view -> finish());

        binding.equilzerBtn.setOnClickListener(view -> {
            try {
                Intent eqIntent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, songServices.mediaPlayer.getAudioSessionId());
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getBaseContext().getPackageName());
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                startActivityForResult(eqIntent, 3);
            }catch (Exception e){
            }
        });

        binding.timerPlayerActivity.setOnClickListener(view -> {
            if(min15 || min30 || min45 || min60){
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(PlayerActivity.this);
                dialogBuilder.setTitle("Stop Timer")
                        .setMessage("Do you want to stop the timer")
                        .setPositiveButton("YES", (dialogInterface, i) -> {
                            min15 = min30 = min45 = min60 = false;
                            binding.timerPlayerActivity.setColorFilter(ContextCompat.getColor(PlayerActivity.this,R.color.skyBlue));
                        })
                        .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
                dialogBuilder.create();
                dialogBuilder.show();
            }else {
                showBottomSheetDialog();
            }
        });

        binding.shareBtnPlayerActivity.setOnClickListener(view -> {
            Intent intent1 = new Intent();
            intent1.setAction(Intent.ACTION_SEND);
            intent1.setType("audio/*");
            intent1.putExtra(Intent.EXTRA_STREAM, Uri.parse(playersongArrayList.get(poisition).getPath()));
            startActivity(Intent.createChooser(intent1, "Sharing File "));
        });

        binding.favouritePA.setOnClickListener(view -> {
            try {
                if (isFavourite) {
                    isFavourite = false;
                    binding.favouritePA.setImageResource(R.drawable.favorite_border);
                    songArrayList.remove(favouriteIndex);
                } else {
                    isFavourite = true;
                    binding.favouritePA.setImageResource(R.drawable.favourite);
                    songArrayList.add(playersongArrayList.get(poisition));
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initializationMediaPlayer(){
        try{
            MyMusic.search = false;
            poisition = getIntent().getIntExtra("positionAdapter",0);
            className = getIntent().getStringExtra("className");
            switch (className) {
                case "MyMusicAdapter":
                    startServices();
                    playersongArrayList = new ArrayList<>();
                    playersongArrayList.addAll(MyMusic.songArrayListAll);
                    setLayout();
                    playMusic();
                    break;
                case "favourite":
                    startServices();
                    playersongArrayList = new ArrayList<>();
                    playersongArrayList.addAll(songArrayList);
                    setLayout();
                    playMusic();
                    break;
                case "MyMusicAdapterSearch":
                    startServices();
                    playersongArrayList = new ArrayList<>();
                    playersongArrayList.addAll(MyMusic.filteredArraylist);
                    setLayout();
                    playMusic();
                    break;
                case "FavouriteShuffle":
                    startServices();
                    playersongArrayList = new ArrayList<>();
                    playersongArrayList.addAll(songArrayList);
                    Collections.shuffle(playersongArrayList);
                    setLayout();
                    playMusic();
                    break;


                case "PlaylistDetailAdapter":
                    startServices();
                    playersongArrayList = new ArrayList<>();
                    playersongArrayList.addAll(Playlist.plalistDataArrayList.get(playlistDetailedActivity.currPlaylistpoisition).playlistsong);
                    setLayout();
                    playMusic();
                    break;

                case "PlaylistDetailActivityShuffle" :
                    startServices();
                    playersongArrayList = new ArrayList<>();
                    playersongArrayList.addAll(Playlist.plalistDataArrayList.get(playlistDetailedActivity.currPlaylistpoisition).playlistsong);
                    Collections.shuffle(playersongArrayList);
                    setLayout();
                    playMusic();
                    break;


                case "NowPlaying":
                    setLayout();
                    binding.tvCurrTime.setText(Global.formatDuration((long) songServices.mediaPlayer.getCurrentPosition()));
                    binding.tvTotalTime.setText(Global.formatDuration((long) songServices.mediaPlayer.getDuration()));
                    binding.seekBarPlayer.setProgress(songServices.mediaPlayer.getCurrentPosition());
                    binding.seekBarPlayer.setMax(songServices.mediaPlayer.getDuration());
                    if (isPlaying) binding.btnPlayPause.setIconResource(R.drawable.playy);
                    else binding.btnPlayPause.setIconResource(R.drawable.pause);
                    break;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLayout(){
        Song song = playersongArrayList.get(poisition);
        favouriteIndex = Global.checkFavouriteornot(song.getId());
        try{Glide.with(this).load(song.getSongImage()).centerCrop()
                .placeholder(music_icon).into(binding.musicIcon);
        binding.musicName.setText(song.getTitle());} catch (Exception ignored){}
        binding.album.setText(song.getAlbum());
        if(repeat) binding.repeatButtonPlayer.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.purple_500));
        if(min15 || min30 || min45 || min60)
            binding.timerPlayerActivity.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.purple_500));
        if(isFavourite) binding.favouritePA.setImageResource(R.drawable.favourite);
        else binding.favouritePA.setImageResource(R.drawable.favorite_border);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void playMusic(){
        if(songServices.mediaPlayer == null)
           songServices.mediaPlayer = new MediaPlayer();

        songServices.mediaPlayer.reset();
        try {
            songServices.mediaPlayer.setDataSource(playersongArrayList.get(poisition).getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            songServices.mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        songServices.mediaPlayer.start();
        isPlaying = true;
        songServices.showNotification(R.drawable.playy);
        binding.btnPlayPause.setIconResource(R.drawable.playy);
        binding.tvTotalTime.setText(Global.formatDuration((long) songServices.mediaPlayer.getDuration()));
        binding.seekBarPlayer.setProgress(0);
        binding.seekBarPlayer.setMax(songServices.mediaPlayer.getDuration());
        songServices.mediaPlayer.setOnCompletionListener(this);
        nowPlayingSongId = playersongArrayList.get(poisition).getId();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void preNextSong(boolean incre){
        Global.setSongPosition(incre);
        setLayout();
        playMusic();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        SongServices.Mybinder binder = (SongServices.Mybinder) iBinder;
        songServices = binder.getServices();
        playMusic();
        songServices.setUpSeekBar();
        songServices.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        songServices.audioManager.requestAudioFocus(songServices,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        songServices = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Global.setSongPosition(true);
        setLayout();
        playMusic();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 3 || resultCode == RESULT_OK){
            return;
        }
    }

    private void showBottomSheetDialog(){
        Dialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.bottom_sheet_dialog);
        dialog.show();

        dialog.findViewById(R.id.min15).setOnClickListener(view -> {
            Toast.makeText(PlayerActivity.this, "Music will stop After 15 minutes", Toast.LENGTH_SHORT).show();
            binding.timerPlayerActivity.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.purple_500));
            min15 = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {Thread.sleep(15 * 60000);} catch (InterruptedException e) {e.printStackTrace();}
                    if(min15) Global.autoCloseApplication();
                }
            }).start();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.min30).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PlayerActivity.this, "Music will stop After 30 minutes", Toast.LENGTH_SHORT).show();
                binding.timerPlayerActivity.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.purple_500));
                min30 = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {Thread.sleep(30 * 60000);} catch (InterruptedException e) {e.printStackTrace();}
                        if(min30) Global.autoCloseApplication();
                    }
                }).start();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.min45).setOnClickListener(view -> {
            Toast.makeText(PlayerActivity.this, "Music will stop After 45 minutes", Toast.LENGTH_SHORT).show();
            binding.timerPlayerActivity.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.purple_500));
            min45 = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {Thread.sleep(45 * 60000);} catch (InterruptedException e) {e.printStackTrace();}
                    if(min45) Global.autoCloseApplication();
                }
            }).start();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.min60).setOnClickListener(view -> {
            Toast.makeText(PlayerActivity.this, "Music will stop After 60 minutes", Toast.LENGTH_SHORT).show();
            binding.timerPlayerActivity.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.purple_500));
            min60 = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {Thread.sleep(60 * 60000);} catch (InterruptedException e) {e.printStackTrace();}
                    if(min60) Global.autoCloseApplication();
                }
            }).start();
            dialog.dismiss();
        });


    }

    public void startServices(){
        Intent intent = new Intent(this,SongServices.class);
        bindService(intent,this,BIND_AUTO_CREATE);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(playersongArrayList.get(poisition).getId() == "Unknown" && !isPlaying) Global.autoCloseApplication();
    }

}