package com.harsh.shah.saavnmp3.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.harsh.shah.saavnmp3.ApplicationClass;
import com.harsh.shah.saavnmp3.R;
import com.harsh.shah.saavnmp3.adapters.ActivityListSongsItemAdapter;
import com.harsh.shah.saavnmp3.adapters.UserCreatedSongsListAdapter;
import com.harsh.shah.saavnmp3.databinding.ActivityListBinding;
import com.harsh.shah.saavnmp3.model.AlbumItem;
import com.harsh.shah.saavnmp3.network.ApiManager;
import com.harsh.shah.saavnmp3.network.utility.RequestNetwork;
import com.harsh.shah.saavnmp3.records.AlbumSearch;
import com.harsh.shah.saavnmp3.records.PlaylistSearch;
import com.harsh.shah.saavnmp3.records.SongResponse;
import com.harsh.shah.saavnmp3.records.sharedpref.SavedLibraries;
import com.harsh.shah.saavnmp3.utils.SharedPreferenceManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    ActivityListBinding binding;

    private final List<String> trackQueue = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.i("ListActivity", "onCreate: reached ListActivity");

        showShimmerData();

        binding.playAllBtn.setOnClickListener(view -> {
            if (!trackQueue.isEmpty()) {
                ((ApplicationClass)getApplicationContext()).setTrackQueue(trackQueue);
                ((ApplicationClass)getApplicationContext()).nextTrack();
                startActivity(new Intent(ListActivity.this, MusicOverviewActivity.class).putExtra("id", ApplicationClass.MUSIC_ID));
            }
        });
        final SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(ListActivity.this);

        binding.addToLibrary.setOnClickListener(view -> {
            if (albumItem == null) return;

            if(isAlbumInLibrary(albumItem, sharedPreferenceManager.getSavedLibrariesData())){
                int index = getAlbumIndexInLibrary(albumItem, sharedPreferenceManager.getSavedLibrariesData());
                if(index==-1) return;
                sharedPreferenceManager.removeLibraryFromSavedLibraries(index);
                Snackbar.make(binding.getRoot(), "Removed from Library", Snackbar.LENGTH_SHORT).show();
            }else {
                SavedLibraries.Library library = new SavedLibraries.Library(
                        albumItem.id(),
                        false,
                        isAlbum,
                        binding.albumTitle.getText().toString(),
                        albumItem.albumCover(),
                        binding.albumSubTitle.getText().toString(),
                        new ArrayList<>()
                );
                sharedPreferenceManager.addLibraryToSavedLibraries(library);
                Snackbar.make(binding.getRoot(), "Added to Library", Snackbar.LENGTH_SHORT).show();
            }

            updateAlbumInLibraryStatus();
        });

        showData();
    }

    private void updateAlbumInLibraryStatus(){
        SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(ListActivity.this);
        if(sharedPreferenceManager.getSavedLibrariesData() == null)
            binding.addToLibrary.setImageResource(R.drawable.round_add_24);
        else {
            final SavedLibraries savedLibraries = sharedPreferenceManager.getSavedLibrariesData();
            binding.addToLibrary.setImageResource(isAlbumInLibrary(albumItem, savedLibraries) ? R.drawable.round_done_24 : R.drawable.round_add_24);
        }
    }
    @SuppressLint("NewApi")
    private boolean isAlbumInLibrary(AlbumItem albumItem, SavedLibraries savedLibraries) {
        if (savedLibraries == null || savedLibraries.lists() == null) {
            return false;
        }
        Log.i("ListActivity", "isAlbumInLibrary: " + savedLibraries);
        if(savedLibraries.lists().isEmpty()) return false;
        return savedLibraries.lists().stream().anyMatch(library -> library.id().equals(albumItem.id()));
    }

    @SuppressLint("NewApi")
    private int getAlbumIndexInLibrary(AlbumItem albumItem, SavedLibraries savedLibraries) {
        if (savedLibraries == null || savedLibraries.lists() == null) {
            return -1;
        }
        Log.i("ListActivity", "getAlbumIndexInLibrary: " + savedLibraries);
        if(savedLibraries.lists().isEmpty()) return -1;
        int index = -1;
        for(SavedLibraries.Library library: savedLibraries.lists()){
            if(library.id().equals(albumItem.id())){
                index = savedLibraries.lists().indexOf(library);
                break;
            }
        }
        return index;
    }

    private void showShimmerData() {
        List<SongResponse.Song> data = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            data.add(new SongResponse.Song(
                    "<shimmer>",
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    "",
                    false,
                    0,
                    "",
                    false,
                    "",
                    null,
                    "",
                    "",
                    null,
                    null, null, null
            ));
        }
        binding.recyclerView.setAdapter(new ActivityListSongsItemAdapter(data));
    }

    private AlbumItem albumItem;
    private boolean isAlbum = false;
    private void showData() {
        if (getIntent().getExtras() == null) return;
        albumItem = new Gson().fromJson(getIntent().getExtras().getString("data"), AlbumItem.class);
        updateAlbumInLibraryStatus();
        binding.albumTitle.setText(albumItem.albumTitle());
        binding.albumSubTitle.setText(albumItem.albumSubTitle());
        if(!albumItem.albumCover().isBlank())
            Picasso.get().load(Uri.parse(albumItem.albumCover())).into(binding.albumCover);

        final ApiManager apiManager = new ApiManager(this);
        final SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(this);

        if(getIntent().getExtras().getBoolean("createdByUser", false)){
            onUserCreatedFetch();
            return;
        }

        if (getIntent().getExtras().getString("type", "").equals("album")) {
            isAlbum = true;
            if(sharedPreferenceManager.getAlbumResponseById(albumItem.id()) != null){
                onAlbumFetched(sharedPreferenceManager.getAlbumResponseById(albumItem.id()));
                return;
            }
            apiManager.retrieveAlbumById(albumItem.id(), new RequestNetwork.RequestListener() {
                @Override
                public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                    AlbumSearch albumSearch = new Gson().fromJson(response, AlbumSearch.class);
                    if (albumSearch.success()) {
                        sharedPreferenceManager.setAlbumResponseById(albumItem.id(), albumSearch);
                        onAlbumFetched(albumSearch);
                    }
                }

                @Override
                public void onErrorResponse(String tag, String message) {

                }
            });
            return;
        }

        if(sharedPreferenceManager.getPlaylistResponseById(albumItem.id()) != null){
            onPlaylistFetched(sharedPreferenceManager.getPlaylistResponseById(albumItem.id()));
            return;
        }
        apiManager.retrievePlaylistById(albumItem.id(), 0, 50, new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                Log.i("API_RESPONSE", "onResponse: " + response);
                PlaylistSearch playlistSearch = new Gson().fromJson(response, PlaylistSearch.class);
                if (playlistSearch.success()) {
                    sharedPreferenceManager.setPlaylistResponseById(albumItem.id(), playlistSearch);
                    onPlaylistFetched(playlistSearch);
                }
            }

            @Override
            public void onErrorResponse(String tag, String message) {

            }
        });
    }

    private void onUserCreatedFetch(){

        binding.addToLibrary.setVisibility(View.INVISIBLE);

        final SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance(this);
        SavedLibraries savedLibraries = sharedPreferenceManager.getSavedLibrariesData();
        if(savedLibraries == null || savedLibraries.lists().isEmpty()) finish();
        SavedLibraries.Library library = null;
        for(SavedLibraries.Library l: savedLibraries.lists()){
            if(l.id().equals(albumItem.id())){
                library = l;
                break;
            }
        }
        if(library == null) finish();
        if(library != null) {
            binding.albumTitle.setText(library.name());
            binding.albumSubTitle.setText(library.description());
            Picasso.get().load(Uri.parse(library.image())).into(binding.albumCover);
            binding.recyclerView.setAdapter(new UserCreatedSongsListAdapter(library.songs()));
            for (SavedLibraries.Library.Songs song : library.songs())
                trackQueue.add(song.id());
        }

    }

    private void onAlbumFetched(AlbumSearch albumSearch){
        binding.albumTitle.setText(albumSearch.data().name());
        binding.albumSubTitle.setText(albumSearch.data().description());
        Picasso.get().load(Uri.parse(albumSearch.data().image().get(albumSearch.data().image().size() - 1).url())).into(binding.albumCover);
        binding.recyclerView.setAdapter(new ActivityListSongsItemAdapter(albumSearch.data().songs()));
        for (SongResponse.Song song : albumSearch.data().songs())
            trackQueue.add(song.id());

        //((ApplicationClass)getApplicationContext()).setTrackQueue(trackQueue);
    }

    private void onPlaylistFetched(PlaylistSearch playlistSearch){
        binding.albumTitle.setText(playlistSearch.data().name());
        binding.albumSubTitle.setText(playlistSearch.data().description());
        Picasso.get().load(Uri.parse(playlistSearch.data().image().get(playlistSearch.data().image().size() - 1).url())).into(binding.albumCover);
        binding.recyclerView.setAdapter(new ActivityListSongsItemAdapter(playlistSearch.data().songs()));
        for (SongResponse.Song song : playlistSearch.data().songs())
            trackQueue.add(song.id());

        //((ApplicationClass)getApplicationContext()).setTrackQueue(trackQueue);
    }

    public void backPress(View view) {
        finish();
    }

}