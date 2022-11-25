package com.ransankul.musicplayer.Frgement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.ransankul.musicplayer.Activity.MainActivity;
import com.ransankul.musicplayer.Activity.PlayerActivity;
import com.ransankul.musicplayer.Adapter.FavouriteAdapter;
import com.ransankul.musicplayer.Model.Global;
import com.ransankul.musicplayer.Model.Song;
import com.ransankul.musicplayer.R;
import com.ransankul.musicplayer.databinding.FragmentFavouriteBinding;

import java.util.ArrayList;

public class Favourite extends Fragment {

    FragmentFavouriteBinding binding;
    public static ArrayList<Song> songArrayList ;
    private FavouriteAdapter favouriteAdapter;

    public Favourite() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        binding = FragmentFavouriteBinding.bind(view);

        songArrayList = Global.checkExistOrNot(songArrayList);
        binding.favRecyclerView.setHasFixedSize(true);
        binding.favRecyclerView.setItemViewCacheSize(15);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),4);
        favouriteAdapter = new FavouriteAdapter(getContext(), songArrayList);
        binding.favRecyclerView.setLayoutManager(layoutManager);
        binding.favRecyclerView.setAdapter(favouriteAdapter);

        if(songArrayList != null && Global.permission){
        if(songArrayList.size() < 1) binding.fABFavourite.setVisibility(View.GONE);
        else binding.fABFavourite.setVisibility(View.VISIBLE);}

        binding.fABFavourite.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PlayerActivity.class);
                intent.putExtra("positionAdapter", 0);
                intent.putExtra("className","FavouriteShuffle");
                requireContext().startActivity(intent);
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences editor = requireContext().getSharedPreferences("FAVOURITE", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = editor.edit();
        Gson gson = new Gson();
        String str = gson.toJson(Favourite.songArrayList);
        edit.putString("favouriteSong", str);
        edit.apply();

        if(songArrayList != null && Global.permission){
            if(songArrayList.size() < 1) binding.fABFavourite.setVisibility(View.GONE);
            else binding.fABFavourite.setVisibility(View.VISIBLE);}
    }
}