package com.ransankul.musicplayer.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ransankul.musicplayer.Frgement.Playlist;
import com.ransankul.musicplayer.Frgement.Favourite;
import com.ransankul.musicplayer.Frgement.MyMusic;
import com.ransankul.musicplayer.Model.Global;
import com.ransankul.musicplayer.R;
import com.ransankul.musicplayer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity{

    public static ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MusicPlayer);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Global.permission = requestRunTimePermission();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrameLayout,new MyMusic());
        transaction.commit();

        binding.bottomNavView.setOnItemSelectListener(i -> {
            FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
            switch (i){
                case 0 :
                    transaction1.replace(R.id.mainFrameLayout,new MyMusic());
                    if(PlayerActivity.songServices != null)
                        MyMusic.fragmentMyMusicBinding.getRoot().setVisibility(View.VISIBLE);
                    break;
                case 1 :
                    transaction1.replace(R.id.mainFrameLayout,new Playlist());
                    break;
                case 2 :
                    transaction1.replace(R.id.mainFrameLayout,new Favourite());
                    break;
            }
            transaction1.commit();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences editor = getSharedPreferences("FAVOURITE", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = editor.edit();
        Gson gson = new Gson();
        String str = gson.toJson(Favourite.songArrayList);
        edit.putString("favouriteSong", str);
        String strply = gson.toJson(Playlist.plalistDataArrayList);
        Log.d("jjjj",strply);
        edit.putString("playlist", strply);
        edit.apply();
    }


    private boolean requestRunTimePermission(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},12);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 12){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else
                ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},12);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!PlayerActivity.isPlaying && PlayerActivity.songServices != null){
            Global.autoCloseApplication();
        }
    }

}