package com.ransankul.musicplayer.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.ransankul.musicplayer.Adapter.PlaylistDetailAdapter;
import com.ransankul.musicplayer.Adapter.SongAdapter;
import com.ransankul.musicplayer.Frgement.Favourite;
import com.ransankul.musicplayer.Frgement.Playlist;
import com.ransankul.musicplayer.Model.Global;
import com.ransankul.musicplayer.Model.Song;
import com.ransankul.musicplayer.R;
import com.ransankul.musicplayer.databinding.ActivityEmptyBinding;

import java.util.ArrayList;
import java.util.HashSet;

public class playlistDetailedActivity extends AppCompatActivity {

    ActivityEmptyBinding binding;
    PlaylistDetailAdapter adapter;
    public static int currPlaylistpoisition = -1;
    ArrayList<Song> arrayList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.splashScreenTheme);
        binding = ActivityEmptyBinding.inflate(getLayoutInflater());
        setTheme(R.style.Theme_MusicPlayer);
        setContentView(binding.getRoot());


        currPlaylistpoisition = getIntent().getIntExtra("poisition",-1);

        Playlist.plalistDataArrayList.get(currPlaylistpoisition).playlistsong = Global
                .checkExistOrNot(Playlist.plalistDataArrayList.get(currPlaylistpoisition).playlistsong);

        arrayList = new ArrayList<>();
        arrayList.addAll(Playlist.plalistDataArrayList.get(currPlaylistpoisition).playlistsong);

        binding.rvDA.setItemViewCacheSize(15);
        binding.rvDA.setHasFixedSize(true);
        binding.rvDA.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaylistDetailAdapter(this, arrayList);
        binding.rvDA.setAdapter(adapter);
        binding.backBtnDA.setOnClickListener(view -> finish());
        binding.shuffleDA.setOnClickListener(view -> {
            Intent intent = new Intent(playlistDetailedActivity.this, PlayerActivity.class);
            intent.putExtra("positionAdapter", 0);
            intent.putExtra("className","PlaylistDetailActivityShuffle");
            startActivity(intent);
        });

        binding.addBtnDA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(playlistDetailedActivity.this,SongSelectionActivity2.class);
                startActivity(intent);
            }
        });

        binding.removeAllBtnDA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(playlistDetailedActivity.this);
                dialogBuilder.setTitle("Remove")
                        .setMessage("Do you want to remove all song")
                        .setPositiveButton("YES", (dialogInterface, i) -> {
                            Playlist.plalistDataArrayList.get(currPlaylistpoisition).playlistsong.clear();
                            adapter.refreshPlaylist();
                            SongSelectionActivity2.hashSet.clear();
                            onResume();
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
                dialogBuilder.create();
                dialogBuilder.show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.playlistNameDA.setText(Playlist.plalistDataArrayList.get(currPlaylistpoisition).name);
        binding.aboutPlaylist.setText("Total Song : "+adapter.getItemCount()+
                 "\n\n"+"Created on : "+ Playlist.plalistDataArrayList.get(currPlaylistpoisition).createdOn);

        if(adapter.getItemCount() > 0){
            Glide.with(this)
                    .load(Playlist.plalistDataArrayList.get(currPlaylistpoisition).playlistsong.get(0).getSongImage())
                    .placeholder(R.drawable.music_icon)
                    .centerCrop()
                    .into(binding.playlistImageDA);
            binding.shuffleDA.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();

        SharedPreferences editor = getSharedPreferences("FAVOURITE", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = editor.edit();
        Gson gson = new Gson();
        String strply = gson.toJson(Playlist.plalistDataArrayList);
        edit.putString("playlist", strply);
        edit.apply();


        //here
        SongSelectionActivity2.hashSet = new HashSet<>();

        adapter.refreshPlaylist();
    }
}

