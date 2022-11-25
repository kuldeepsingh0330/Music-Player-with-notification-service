package com.ransankul.musicplayer.Adapter;

import static com.ransankul.musicplayer.Activity.playlistDetailedActivity.currPlaylistpoisition;
import static com.ransankul.musicplayer.R.color.black;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ransankul.musicplayer.Activity.SongSelectionActivity2;
import com.ransankul.musicplayer.Activity.playlistDetailedActivity;
import com.ransankul.musicplayer.Frgement.Playlist;
import com.ransankul.musicplayer.Model.Global;
import com.ransankul.musicplayer.Model.Song;
import com.ransankul.musicplayer.R;
import com.ransankul.musicplayer.databinding.DisplaySongBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class SongSelectionAdapter extends RecyclerView.Adapter<SongSelectionAdapter.MyViewHolder> {

    Context context;
    ArrayList<Song> detailedsongArrayList;

    public SongSelectionAdapter(Context context, ArrayList<Song> detailedsongArrayList) {
        this.context = context;
        this.detailedsongArrayList = detailedsongArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.display_song,parent,false);
        return new MyViewHolder(v);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Song song = detailedsongArrayList.get(position);
        holder.binding.songName.setText(song.getTitle());
        holder.binding.songDuration.setText(Global.formatDuration(song.getDuration()));
        holder.binding.songAlbum.setText(song.getAlbum());
        Glide.with(context).load(song.getSongImage()).centerCrop()
                .placeholder(R.drawable.music_icon).into(holder.binding.songIV);

        holder.itemView.setOnClickListener(view -> {
            if(addSong(song)){
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.skyBlue));
            } else{
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, black));
            }
        });
    }

    @Override
    public int getItemCount() {
        return detailedsongArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        DisplaySongBinding binding;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DisplaySongBinding.bind(itemView);
        }
    }

    private boolean addSong(Song song) {

        if(SongSelectionActivity2.hashSet.contains(song.getId())){
            Playlist.plalistDataArrayList.get(currPlaylistpoisition).playlistsong.remove(song);
            SongSelectionActivity2.hashSet.remove(song.getId());
            return false;
        }else{
            Log.d("kkkk","dddd");
            Playlist.plalistDataArrayList.get(currPlaylistpoisition).playlistsong.add(song);
            SongSelectionActivity2.hashSet.add(song.getId());
            return true;
        }
    }

    public void showfilterresult(ArrayList<Song> filteredArrayList){
        detailedsongArrayList = new ArrayList<>();
        detailedsongArrayList.addAll(filteredArrayList);
        notifyDataSetChanged();
    }
}
