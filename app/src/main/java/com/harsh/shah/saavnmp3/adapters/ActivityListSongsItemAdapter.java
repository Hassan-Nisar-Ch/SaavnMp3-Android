package com.harsh.shah.saavnmp3.adapters;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.harsh.shah.saavnmp3.R;
import com.harsh.shah.saavnmp3.activities.MusicOverviewActivity;
import com.harsh.shah.saavnmp3.records.SongResponse;

import java.util.List;

public class ActivityListSongsItemAdapter extends RecyclerView.Adapter<ActivityListSongsItemAdapter.ViewHolder> {

    private final List<SongResponse.Song> data;

    public ActivityListSongsItemAdapter(List<SongResponse.Song> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View _v = View.inflate(parent.getContext(), viewType == 0 ? R.layout.activity_list_song_item : R.layout.activity_list_shimmer, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        _v.setLayoutParams(layoutParams);
        return new ViewHolder(_v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position) == 1) {
            ((ShimmerFrameLayout) holder.itemView.findViewById(R.id.shimmer)).startShimmer();
            return;
        }

        SongResponse.Song song = data.get(position);

        holder.itemView.findViewById(R.id.title).setSelected(true);
        holder.itemView.findViewById(R.id.artist).setSelected(true);

        ((TextView) holder.itemView.findViewById(R.id.title)).setText(song.name());
        StringBuilder artistsNames = new StringBuilder();
        for (int i = 0; i < song.artists().all().size(); i++) {
            artistsNames.append(song.artists().all().get(i).name());
            artistsNames.append(",");
        }
        ((TextView) holder.itemView.findViewById(R.id.artist)).setText(artistsNames.toString());

        holder.itemView.setOnClickListener(view -> {
            holder.itemView.getContext().startActivity(new Intent(view.getContext(), MusicOverviewActivity.class));
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).id().equals("<shimmer>")) return 1;
        else return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
