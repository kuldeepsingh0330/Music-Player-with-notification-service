package com.ransankul.musicplayer.Activity;

import static com.ransankul.musicplayer.Activity.playlistDetailedActivity.currPlaylistpoisition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.ransankul.musicplayer.Adapter.SongAdapter;
import com.ransankul.musicplayer.Adapter.SongSelectionAdapter;
import com.ransankul.musicplayer.Frgement.MyMusic;
import com.ransankul.musicplayer.Frgement.Playlist;
import com.ransankul.musicplayer.Model.Song;
import com.ransankul.musicplayer.R;
import com.ransankul.musicplayer.databinding.ActivitySongSelection2Binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class SongSelectionActivity2 extends AppCompatActivity {

    private ActivitySongSelection2Binding binding;
    private SongSelectionAdapter adapter;
    private ArrayList<Song> selectionArraylist ;
    private ArrayList<Song> filterselectionArraylist ;
    public static HashSet<String> hashSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySongSelection2Binding.inflate(getLayoutInflater());
        setTheme(R.style.Theme_MusicPlayer);
        setContentView(binding.getRoot());
        displaySongOrNot();
        setLayoutSSA();


        binding.searchViewSS.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterselectionArraylist = new ArrayList<>();
                if(newText != null){
                    String input = newText.toLowerCase();
                    for(Song song : selectionArraylist){
                        if(song.getTitle().contains(input))
                            filterselectionArraylist.add(song);
                    }
                    MyMusic.search = true;
                    adapter.showfilterresult(filterselectionArraylist);

                }
                return true;
            }
        });

    }

    private void setLayoutSSA() {
        binding.SelectionRecyclerView.setItemViewCacheSize(15);
        binding.SelectionRecyclerView.setHasFixedSize(true);
        binding.SelectionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SongSelectionAdapter(this,selectionArraylist);
        binding.SelectionRecyclerView.setAdapter(adapter);
        binding.backSA.setOnClickListener(view -> finish());
    }

    private void displaySongOrNot(){

        selectionArraylist = new ArrayList<>();
        for(Song currSong: Playlist.plalistDataArrayList.get(playlistDetailedActivity.currPlaylistpoisition).playlistsong){
            String str = currSong.getId();
            hashSet.add(str);
        }

        for (Song song: MyMusic.songArrayListAll) {
            if(!hashSet.contains(song.getId())){
                selectionArraylist.add(song);
            }
        }
    }
}