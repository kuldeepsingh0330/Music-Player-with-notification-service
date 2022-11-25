package com.ransankul.musicplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ransankul.musicplayer.Activity.PlayerActivity;
import com.ransankul.musicplayer.Activity.playlistDetailedActivity;
import com.ransankul.musicplayer.Frgement.Playlist;
import com.ransankul.musicplayer.Model.Global;
import com.ransankul.musicplayer.Model.Song;
import com.ransankul.musicplayer.R;
import com.ransankul.musicplayer.databinding.DisplaySongBinding;

import java.util.ArrayList;

public class PlaylistDetailAdapter extends RecyclerView.Adapter<PlaylistDetailAdapter.MyViewHolder> {


    Context context;
    ArrayList<Song> songArrayList;


    public PlaylistDetailAdapter(Context context, ArrayList<Song> songArrayList) {
        this.context = context;
        this.songArrayList = songArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.display_song,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Song song = songArrayList.get(position);
        holder.binding.songName.setText(song.getTitle());
        holder.binding.songDuration.setText(Global.formatDuration(song.getDuration()));
        holder.binding.songAlbum.setText(song.getAlbum());
        Glide.with(context).load(song.getSongImage()).centerCrop()
                .placeholder(R.drawable.music_icon).into(holder.binding.songIV);

        holder.itemView.setOnClickListener(view -> sendIntent("PlaylistDetailAdapter",position));
    }

    @Override
    public int getItemCount() {
        return songArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        DisplaySongBinding binding;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DisplaySongBinding.bind(itemView);
        }
    }

    private void sendIntent(String activity, int position){
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("positionAdapter", position);
        intent.putExtra("className",activity);
        context.startActivity(intent);
    }

    public void refreshPlaylist(){
        songArrayList = Playlist.plalistDataArrayList.get(playlistDetailedActivity.currPlaylistpoisition).playlistsong;
        notifyDataSetChanged();
    }
}
