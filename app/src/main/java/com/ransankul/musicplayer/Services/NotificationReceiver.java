package com.ransankul.musicplayer.Services;

import static com.ransankul.musicplayer.Activity.PlayerActivity.binding;
import static com.ransankul.musicplayer.Activity.PlayerActivity.songServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.ransankul.musicplayer.Activity.PlayerActivity;
import com.ransankul.musicplayer.Frgement.MyMusic;
import com.ransankul.musicplayer.Model.Global;
import com.ransankul.musicplayer.Model.Song;
import com.ransankul.musicplayer.R;

public class NotificationReceiver extends BroadcastReceiver{

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() != null){
            if(intent.getAction().equals(ApplicationClass.PLAY)){
                if(PlayerActivity.isPlaying) pauseMusic();
                else playMusic();
            }else if(intent.getAction().equals(ApplicationClass.PREVIOUS)){
                preNextSong(context,false);
            }else if(intent.getAction().equals(ApplicationClass.NEXT)){
                preNextSong(context,true);
            }else if(intent.getAction().equals(ApplicationClass.EXIT)){
                Global.autoCloseApplication();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void playMusic(){
        PlayerActivity.isPlaying = true;
        songServices.mediaPlayer.start();
        songServices.showNotification(R.drawable.playy);
        binding.btnPlayPause.setIconResource(R.drawable.playy);
        MyMusic.fragmentMyMusicBinding.nowPlayPause.setIconResource(R.drawable.playy);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void pauseMusic(){
        PlayerActivity.isPlaying = false;
        songServices.mediaPlayer.pause();
        songServices.showNotification(R.drawable.pause);
        binding.btnPlayPause.setIconResource(R.drawable.pause);
        MyMusic.fragmentMyMusicBinding.nowPlayPause.setIconResource(R.drawable.pause);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    static void preNextSong(Context context, boolean incr){
        Global.setSongPosition(incr);
        songServices.playMusic();
        Song song = PlayerActivity.playersongArrayList.get(PlayerActivity.poisition);
        Glide.with(context).load(song.getSongImage()).centerCrop()
                .placeholder(R.drawable.music_icon).into(binding.musicIcon);
        binding.musicName.setText(song.getTitle());
        binding.album.setText(song.getAlbum());
        PlayerActivity.favouriteIndex = Global.checkFavouriteornot(PlayerActivity.playersongArrayList.get(PlayerActivity.poisition).getId());
        if(PlayerActivity.isFavourite) binding.favouritePA.setImageResource(R.drawable.favourite);
        else binding.favouritePA.setImageResource(R.drawable.favorite_border);
        MyMusic.fragmentMyMusicBinding.songNameNow.setText(song.getTitle());
        Glide.with(context).load(song.getSongImage()).centerCrop()
                .placeholder(R.drawable.music_icon).into(MyMusic.fragmentMyMusicBinding.songIVNow);
        playMusic();
    }
}
