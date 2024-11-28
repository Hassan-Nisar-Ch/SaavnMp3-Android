package com.harsh.shah.saavnmp3;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.harsh.shah.saavnmp3.adapters.ActivityMainAlbumItemAdapter;
import com.harsh.shah.saavnmp3.databinding.ActivityMainBinding;
import com.harsh.shah.saavnmp3.modals.AlbumItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int span = calculateNoOfColumns(this, 200);
        binding.playlistRecyclerView.setLayoutManager(new GridLayoutManager(this,span));
        binding.playlistRecyclerView.setAdapter(new PlaylistAdapter());

        binding.popularSongsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<AlbumItem> data = new ArrayList<>();
        data.add(new AlbumItem("Album 1", "Sub Title 1", ""));
        data.add(new AlbumItem("Album 2", "Sub Title 2", ""));
        data.add(new AlbumItem("Album 3", "Sub Title 3", ""));
        data.add(new AlbumItem("Album 4", "Sub Title 4", ""));
        data.add(new AlbumItem("Album 5", "Sub Title 5", ""));
        data.add(new AlbumItem("Album 6", "Sub Title 6", ""));
        data.add(new AlbumItem("Album 7", "Sub Title 7", ""));
        data.add(new AlbumItem("Album 8", "Sub Title 8", ""));
        data.add(new AlbumItem("Album 9", "Sub Title 9", ""));
        data.add(new AlbumItem("Album 10", "Sub Title 10", ""));
        binding.popularSongsRecyclerView.setAdapter(new ActivityMainAlbumItemAdapter(data));
    }


    static class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistAdapterViewHolder> {

        @NonNull
        @Override
        public PlaylistAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View _v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_playlist_item, null, false);
            _v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new PlaylistAdapterViewHolder(_v);
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        @Override
        public void onBindViewHolder(@NonNull PlaylistAdapterViewHolder holder, int position) {

        }

        static class PlaylistAdapterViewHolder extends RecyclerView.ViewHolder {
            public PlaylistAdapterViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }

    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return  (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
    }

}