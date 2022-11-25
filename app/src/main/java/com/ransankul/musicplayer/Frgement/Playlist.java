package com.ransankul.musicplayer.Frgement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.ransankul.musicplayer.Activity.MainActivity;
import com.ransankul.musicplayer.Adapter.PlayListAdapter;
import com.ransankul.musicplayer.Model.Global;
import com.ransankul.musicplayer.Model.PlalistData;
import com.ransankul.musicplayer.Model.Song;
import com.ransankul.musicplayer.R;
import com.ransankul.musicplayer.databinding.AddPlaylistDialogBinding;
import com.ransankul.musicplayer.databinding.FragmentPlaylistBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Playlist extends Fragment {
    FragmentPlaylistBinding binding;
    PlayListAdapter playListAdapter;

    public static ArrayList<PlalistData> plalistDataArrayList ;



    public Playlist() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_playlist, container, false);
        binding = FragmentPlaylistBinding.bind(view);

        if(!Global.permission)
            binding.fABFavourite.setVisibility(View.GONE);
        else binding.fABFavourite.setVisibility(View.VISIBLE);

        playllistSetLayout();

        return view;
    }

    private void playllistSetLayout() {

        binding.favRecyclerView.setHasFixedSize(true);
        binding.favRecyclerView.setItemViewCacheSize(15);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        playListAdapter = new PlayListAdapter(getContext(), plalistDataArrayList);
        binding.favRecyclerView.setLayoutManager(layoutManager);
        binding.favRecyclerView.setAdapter(playListAdapter);
        binding.fABFavourite.setOnClickListener(view1 -> createPlayListDialog());
    }

    private void createPlayListDialog(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_playlist_dialog, binding.getRoot(), false);
        AddPlaylistDialogBinding APDBinding = AddPlaylistDialogBinding.bind(view);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setView(view).setCancelable(false)
                .setTitle("Create Playlist")
                .setPositiveButton("ADD", (dialogInterface, i) -> {
                    String playlistName = APDBinding.playlistNameET.getText().toString();
                    if(!playlistName.isEmpty()){
                        addPlaylist(playlistName);
                        onResume();
                    }
                    dialogInterface.dismiss();
                }).setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss()).show();


    }

    private void addPlaylist(String playlistName) {
        boolean playListExist = false;

        for (PlalistData i: Playlist.plalistDataArrayList) {
            if (playlistName.equals(i.name)) {
                playListExist = true;
                break;
            }
        }

        if(playListExist) Toast.makeText(getContext(), "PlayList Exist", Toast.LENGTH_SHORT).show();
        else{
            PlalistData plalistData = new PlalistData();
            plalistData.name = playlistName;
            plalistData.playlistsong = new ArrayList<>();
            Date calendar = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyy", Locale.ENGLISH);
            plalistData.createdOn = (simpleDateFormat.format(calendar));
            Playlist.plalistDataArrayList.add(plalistData);
            playListAdapter.refreshDataOnAdd();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences editor = requireContext().getSharedPreferences("FAVOURITE", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = editor.edit();
        Gson gson = new Gson();
        String strply = gson.toJson(Playlist.plalistDataArrayList);
        edit.putString("playlist", strply);
        edit.apply();
        playListAdapter.refreshDataOnAdd();
    }
}