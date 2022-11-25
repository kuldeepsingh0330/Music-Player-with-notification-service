package com.ransankul.musicplayer.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ransankul.musicplayer.Activity.playlistDetailedActivity;
import com.ransankul.musicplayer.Frgement.Playlist;
import com.ransankul.musicplayer.Model.PlalistData;
import com.ransankul.musicplayer.R;
import com.ransankul.musicplayer.databinding.PlaylistLayoutBinding;

import java.util.ArrayList;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.MyViewHolder>{
    Context context;
    ArrayList<PlalistData> playListarrayList;

    public PlayListAdapter(Context context, ArrayList<PlalistData> playListarrayList) {
        this.context = context;
        this.playListarrayList = playListarrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PlalistData plalistData = playListarrayList.get(position);
        holder.binding.songNamePL.setText(plalistData.name);
        holder.binding.songNamePL.setSelected(true);

        if(Playlist.plalistDataArrayList.get(position).playlistsong.size() > 0){
            Glide.with(context)
                    .load(Playlist.plalistDataArrayList.get(position).playlistsong.get(0).getSongImage())
                    .placeholder(R.drawable.music_icon)
                    .into(holder.binding.songImagePL);
        }else {
            holder.binding.songImagePL.setImageResource(R.drawable.music_icon);
        }

        holder.binding.deleteBtnPL.setOnClickListener(view -> {
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
            dialogBuilder.setTitle(plalistData.name)
                    .setMessage("Do you want to delete the Playlist ?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        Playlist.plalistDataArrayList.remove(position);
                        refreshDataOnAdd();
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
        });


        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, playlistDetailedActivity.class);
            intent.putExtra("poisition",position);
            ContextCompat.startActivity(context,intent,null);
        });


    }

    @Override
    public int getItemCount() {
        if(playListarrayList == null) return 0;
        return playListarrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        PlaylistLayoutBinding binding;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = PlaylistLayoutBinding.bind(itemView);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshDataOnAdd(){
        playListarrayList = new ArrayList<>();
        if(Playlist.plalistDataArrayList != null)
            playListarrayList.addAll(Playlist.plalistDataArrayList);
        notifyDataSetChanged();
    }
}
