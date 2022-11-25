package com.ransankul.musicplayer.Frgement;

import static android.content.Context.BIND_AUTO_CREATE;

import static com.ransankul.musicplayer.Activity.PlayerActivity.isPlaying;
import static com.ransankul.musicplayer.Activity.PlayerActivity.nowPlayingSongId;
import static com.ransankul.musicplayer.Activity.PlayerActivity.playersongArrayList;
import static com.ransankul.musicplayer.Activity.PlayerActivity.poisition;
import static com.ransankul.musicplayer.Activity.PlayerActivity.songServices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ransankul.musicplayer.Activity.MainActivity;
import com.ransankul.musicplayer.Activity.PlayerActivity;
import com.ransankul.musicplayer.Adapter.SongAdapter;
import com.ransankul.musicplayer.Model.Global;
import com.ransankul.musicplayer.Model.PlalistData;
import com.ransankul.musicplayer.Model.Song;
import com.ransankul.musicplayer.R;
import com.ransankul.musicplayer.Services.SongServices;
import com.ransankul.musicplayer.databinding.FragmentMyMusicBinding;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class MyMusic extends Fragment {

    public static FragmentMyMusicBinding fragmentMyMusicBinding;
    public static ArrayList<Song> songArrayListAll ;
    public static ArrayList<Song> filteredArraylist;
    private SongAdapter songAdapter ;
    public static boolean search = false;
    public static int sortOrder = 0;
    String[] sortingList = new String[]{MediaStore.Audio.Media.DATE_ADDED + " DESC"
            ,MediaStore.Audio.Media.DATE_ADDED
            ,MediaStore.Audio.Media.TITLE
            ,MediaStore.Audio.Media.SIZE+" DESC"};

    public MyMusic() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentMyMusicBinding = FragmentMyMusicBinding.inflate(inflater, container, false);
        fragmentMyMusicBinding.currSongPlaying.setVisibility(View.GONE);

        songArrayListAll = new ArrayList<>();

        if(Global.permission){
            setLayout();
        }

        fragmentMyMusicBinding.nowPlayPause.setOnClickListener(view -> {
            if(PlayerActivity.isPlaying) pauseMusic(); else playMusic();
        });

        fragmentMyMusicBinding.nowNext.setOnClickListener(view -> nextMusic());

        fragmentMyMusicBinding.currSongPlaying.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), PlayerActivity.class);
            intent.putExtra("positionAdapter", poisition);
            intent.putExtra("className","NowPlaying");
            ContextCompat.startActivity(requireContext(),intent,null);
        });

        fragmentMyMusicBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredArraylist = new ArrayList<>();
                if(newText != null){
                    String input = newText.toLowerCase();
                    for(Song song : songArrayListAll){
                        if(song.getTitle().contains(input))
                            filteredArraylist.add(song);
                    }
                    search = true;
                    songAdapter.showfilterresult(filteredArraylist);
                }
                return true;
            }
        });

        fragmentMyMusicBinding.filter.setOnClickListener(new View.OnClickListener() {
            final String[] menuArray = new String[]{"Recently Added","Older Songs","Song Title","Song Size"};
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getContext());
                dialog.setTitle("Sorting")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            SharedPreferences sp = getContext().getSharedPreferences("SORTING",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("sortOrder",sortOrder);
                            editor.apply();
                            setLayout();
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setSingleChoiceItems(menuArray, sortOrder, (dialogInterface, i) -> sortOrder = i).create().show();
            }
        });

        return fragmentMyMusicBinding.getRoot();
    }

    public void setLayout(){
        setFavPlaylist();
        search = false;
        SharedPreferences editorSorting = requireContext().getSharedPreferences("SORTING", Context.MODE_PRIVATE);
        sortOrder = editorSorting.getInt("sortOrder",0);
        songArrayListAll = getAllAudio();
        fragmentMyMusicBinding.recyclerView.setHasFixedSize(true);
        fragmentMyMusicBinding.recyclerView.setItemViewCacheSize(15);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        songAdapter = new SongAdapter(getContext(),songArrayListAll);
        fragmentMyMusicBinding.recyclerView.setLayoutManager(layoutManager);
        fragmentMyMusicBinding.recyclerView.setAdapter(songAdapter);
    }

    public void setFavPlaylist(){
        Favourite.songArrayList = new ArrayList<>();
        SharedPreferences editor = requireContext().getSharedPreferences("FAVOURITE", Context.MODE_PRIVATE);
        String gsonString = editor.getString("favouriteSong",null);
        Type typeToken = new TypeToken<ArrayList<Song>>(){}.getType();
        Gson gson = new Gson();
        if(gsonString != null){
            ArrayList<Song> data = gson.fromJson(gsonString, typeToken);
            if(data != null)
                Favourite.songArrayList.addAll(data);
        }

        Playlist.plalistDataArrayList = new ArrayList<>();
        String gsonStringplaylist = editor.getString("playlist",null);
        Type typeTokenply = new TypeToken<ArrayList<PlalistData>>(){}.getType();
        if(gsonStringplaylist != null){
            ArrayList<PlalistData> dataplaylist = gson.fromJson(gsonStringplaylist, typeTokenply);
            if(dataplaylist != null)
                Playlist.plalistDataArrayList.addAll(dataplaylist);
        }
    }


    private ArrayList<Song> getAllAudio(){
        ArrayList<Song> arrayList = new ArrayList<>();
        String selection = MediaStore.Audio.Media.IS_MUSIC+" != 0 ";
        String[] proje = {MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE
                ,MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM_ID};

        Cursor cursor = requireContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,proje,selection,null,
                sortingList[sortOrder],null);

        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    @SuppressLint("Range") String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    @SuppressLint("Range") String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    @SuppressLint("Range") String albumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                    @SuppressLint("Range") long Duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    Uri uri = Uri.parse("content://media/external/audio/albumart");
                    String albumUri = Uri.withAppendedPath(uri,albumId).toString();

                    Song song = new Song(id,title,album,artist,path,albumUri,Duration);
                    File file = new File(song.getPath());
                    if(file.exists())
                        arrayList.add(song);
                }while (cursor.moveToNext());
                cursor.close();
            }
        }
        return arrayList;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences editorSorting = requireContext().getSharedPreferences("SORTING", Context.MODE_PRIVATE);
        int sort = editorSorting.getInt("sortOrder",0);
        if(sort != sortOrder){
            sortOrder = sort;
            songArrayListAll = getAllAudio();
            songAdapter.showfilterresult(songArrayListAll);
        }
        if(songServices != null) {
            fragmentMyMusicBinding.currSongPlaying.setVisibility(View.VISIBLE);
            setLayoutNow();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void playMusic(){
        songServices.mediaPlayer.start();
        fragmentMyMusicBinding.nowPlayPause.setIconResource(R.drawable.playy);
        songServices.showNotification(R.drawable.playy);
        PlayerActivity.binding.btnPlayPause.setIconResource(R.drawable.playy);
        PlayerActivity.isPlaying = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void pauseMusic() {
        songServices.mediaPlayer.pause();
        fragmentMyMusicBinding.nowPlayPause.setIconResource(R.drawable.pause);
        songServices.showNotification(R.drawable.pause);
        PlayerActivity.binding.btnPlayPause.setIconResource(R.drawable.pause);
        PlayerActivity.isPlaying = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void nextMusic(){
        songServices.mediaPlayer.reset();
        Global.setSongPosition(true);
        setLayoutNow();
        startNextMusic();
    }

    public void setLayoutNow(){
        fragmentMyMusicBinding.getRoot().setVisibility(View.VISIBLE);
        fragmentMyMusicBinding.songNameNow.setSelected(true);
        Song song = playersongArrayList.get(poisition);

        try{
            Glide.with(this).load(song.getSongImage()).centerCrop()
                    .placeholder(R.drawable.music_icon).into(MyMusic.fragmentMyMusicBinding.songIVNow);}
        catch (Exception e){}

        fragmentMyMusicBinding.songNameNow.setText(song.getTitle());
        if(PlayerActivity.isPlaying) fragmentMyMusicBinding.nowPlayPause.setIconResource(R.drawable.playy);
        else fragmentMyMusicBinding.nowPlayPause.setIconResource(R.drawable.pause);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startNextMusic(){
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
        isPlaying = true;
        songServices.mediaPlayer.start();
        songServices.showNotification(R.drawable.playy);
        nowPlayingSongId = playersongArrayList.get(poisition).getId();
    }




}