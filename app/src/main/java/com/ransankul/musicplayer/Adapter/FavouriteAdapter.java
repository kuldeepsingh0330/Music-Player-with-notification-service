package com.ransankul.musicplayer.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ransankul.musicplayer.Activity.PlayerActivity;
import com.ransankul.musicplayer.Model.Song;
import com.ransankul.musicplayer.R;
import com.ransankul.musicplayer.databinding.FavouriteLayoutBinding;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyViewHolder> {
    Context context;
    ArrayList<Song> FAarrayList;

    public FavouriteAdapter(Context context, ArrayList<Song> FAarrayList) {
        this.context = context;
        this.FAarrayList = FAarrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favourite_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Song song = FAarrayList.get(position);
        holder.binding.favSongNaame.setText(song.getTitle());
        try{  Glide.with(context)
                .load(PlayerActivity.playersongArrayList.get(PlayerActivity.poisition).getSongImage())
                .placeholder(R.drawable.music_icon).centerCrop().into(holder.binding.favSongImage);}
        catch (Exception e) {
            e.printStackTrace();
        }


        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("positionAdapter", position);
            intent.putExtra("className","favourite");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if(FAarrayList == null) return 0;
        return FAarrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        FavouriteLayoutBinding binding;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = FavouriteLayoutBinding.bind(itemView);
        }
    }
}
