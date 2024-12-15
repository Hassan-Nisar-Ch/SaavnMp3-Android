package com.harsh.shah.saavnmp3.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.harsh.shah.saavnmp3.R;
import com.harsh.shah.saavnmp3.adapters.ActivitySearchListItemAdapter;
import com.harsh.shah.saavnmp3.databinding.ActivitySearchBinding;
import com.harsh.shah.saavnmp3.model.SearchListItem;
import com.harsh.shah.saavnmp3.network.ApiManager;
import com.harsh.shah.saavnmp3.network.utility.RequestNetwork;
import com.harsh.shah.saavnmp3.records.GlobalSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    private final String TAG = "SearchActivity";

    GlobalSearch globalSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        OverScrollDecoratorHelper.setUpOverScroll(binding.hscrollview);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.edittext.requestFocus();

        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            Log.i("SearchActivity","checkedIds: " + checkedIds);
            if(globalSearch!=null){
                if(globalSearch.success()){
                    refreshData();
                }
            }
        });

        binding.edittext.setOnEditorActionListener((textView, i, keyEvent) -> {
            showData(textView.getText().toString());
            Log.i(TAG, "onCreate: " + textView.getText().toString());
            binding.edittext.clearFocus();

            return true;
        });

        //showData("");

    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showData(String query) {
        showShimmerData();

        final ApiManager apiManager = new ApiManager(this);
        apiManager.globalSearch(query, new RequestNetwork.RequestListener() {
            @Override
            public void onResponse(String tag, String response, HashMap<String, Object> responseHeaders) {
                globalSearch = new Gson().fromJson(response, GlobalSearch.class);
                if (globalSearch.success()){
                    refreshData();
                }
                Log.i(TAG, "onResponse: " + response);
            }

            @Override
            public void onErrorResponse(String tag, String message) {
                Log.e(TAG, "onErrorResponse: " + message);
            }
        });
    }

    private void refreshData() {
        final List<SearchListItem> data = new ArrayList<>();
        int checkedChipId = binding.chipGroup.getCheckedChipId();
        if (checkedChipId == R.id.chip_all) {
            globalSearch.data().topQuery().results().forEach(item -> {
                if (!(item.type().equals("song") || item.type().equals("album") || item.type().equals("playlist") || item.type().equals("artist")))
                    return;
                data.add(
                        new SearchListItem(
                                item.id(),
                                item.title(),
                                item.description(),
                                item.image().get(item.image().size() - 1).url(),
                                SearchListItem.Type.valueOf(item.type().toUpperCase())
                        )
                );
            });
            addSongsData(data);
            addAlbumsData(data);
            addPlaylistsData(data);
            addArtistsData(data);

        } else if (checkedChipId == R.id.chip_song) {
            addSongsData(data);
        } else if (checkedChipId == R.id.chip_albums) {
           addAlbumsData(data);
        } else if (checkedChipId == R.id.chip_playlists) {
            addPlaylistsData(data);
        } else if (checkedChipId == R.id.chip_artists) {
            addArtistsData(data);
        } else {
            throw new IllegalStateException("Unexpected value: " + binding.chipGroup.getCheckedChipId());
        }
        if(!data.isEmpty())binding.recyclerView.setAdapter(new ActivitySearchListItemAdapter(data));
    }

    private void addSongsData(List<SearchListItem> data){
        globalSearch.data().songs().results().forEach(item -> {
            data.add(
                    new SearchListItem(
                            item.id(),
                            item.title(),
                            item.description(),
                            item.image().get(item.image().size() - 1).url(),
                            SearchListItem.Type.SONG
                    )
            );
        });
    }

    private void addAlbumsData(List<SearchListItem> data){
        globalSearch.data().albums().results().forEach(item -> {
            data.add(
                    new SearchListItem(
                            item.id(),
                            item.title(),
                            item.description(),
                            item.image().get(item.image().size() - 1).url(),
                            SearchListItem.Type.ALBUM
                    )
            );
        });
    }

    private void addPlaylistsData(List<SearchListItem> data){
        globalSearch.data().playlists().results().forEach(item -> {
            data.add(
                    new SearchListItem(
                            item.id(),
                            item.title(),
                            item.description(),
                            item.image().get(item.image().size() - 1).url(),
                            SearchListItem.Type.PLAYLIST
                    )
            );
        });
    }

    private void addArtistsData(List<SearchListItem> data){
        globalSearch.data().artists().results().forEach(item -> {
            data.add(
                    new SearchListItem(
                            item.id(),
                            item.title(),
                            item.description(),
                            item.image().get(item.image().size() - 1).url(),
                            SearchListItem.Type.ARTIST
                    )
            );
        });
    }

    private void showShimmerData() {
        List<SearchListItem> data = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            data.add(new SearchListItem(
                    "<shimmer>",
                    "",
                    "",
                    "",
                    SearchListItem.Type.SONG
            ));
        }
        binding.recyclerView.setAdapter(new ActivitySearchListItemAdapter(data));
    }

    public void backPress(View view) {
        finish();
    }
}