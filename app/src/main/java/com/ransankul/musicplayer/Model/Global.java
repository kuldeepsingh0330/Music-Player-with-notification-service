package com.ransankul.musicplayer.Model;

import android.media.MediaMetadataRetriever;

import com.ransankul.musicplayer.Activity.PlayerActivity;
import com.ransankul.musicplayer.Frgement.Favourite;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Global {
    public static boolean permission = false;

    public static String formatDuration(Long Duration){
        long min = TimeUnit.MINUTES.convert(Duration,TimeUnit.MILLISECONDS);
        long second = (TimeUnit.SECONDS.convert(Duration,TimeUnit.MILLISECONDS) -
                min*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES));

        return String.format("%02d:%02d", min,second);
    }

    public static void setSongPosition(boolean incre) {
        if(PlayerActivity.repeat == false) {
            if (incre) {
                if (PlayerActivity.playersongArrayList.size() - 1 == PlayerActivity.poisition)
                    PlayerActivity.poisition = 0;
                else ++PlayerActivity.poisition;
            } else {
                if (PlayerActivity.poisition == 0)
                    PlayerActivity.poisition = PlayerActivity.playersongArrayList.size() - 1;
                else --PlayerActivity.poisition;
            }
        }
    }

    public static void autoCloseApplication(){
        if(PlayerActivity.songServices != null) {
            PlayerActivity.songServices.stopForeground(true);
            PlayerActivity.songServices.mediaPlayer.release();
            PlayerActivity.songServices = null;
            PlayerActivity.songServices.audioManager.abandonAudioFocus(PlayerActivity.songServices);
        }
        System.exit(03);

    }

    public static int checkFavouriteornot(String id){
        PlayerActivity.isFavourite = false;
        int i = 0;
        for (Song song : Favourite.songArrayList) {
            if(Objects.equals(id, song.getId())) {
                PlayerActivity.isFavourite = true;
                return i;
            }
            i++;
        }
        return -1;
    }

    public static ArrayList<Song> checkExistOrNot(ArrayList<Song> checkArrayList){
        if(checkArrayList == null) return null;
        for (Song song: checkArrayList) {
            File file = new File(song.getPath());
            if(!file.exists()){
                checkArrayList.remove(song);
            }
        }
        return checkArrayList;
    }


}


