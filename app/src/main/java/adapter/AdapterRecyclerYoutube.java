package adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import tunermusic.metronome.chords.R;

public class AdapterRecyclerYoutube extends RecyclerView.Adapter<AdapterRecyclerYoutube.ViewHolderYoutube> {

    Context context;
    ArrayList<String> videoIds;
    ArrayList<String> videoTitle;
    private onVideoChosen listener;
    public AdapterRecyclerYoutube(Context context, ArrayList<String> videoIds, ArrayList<String> videoTitle, onVideoChosen listener) {
        this.context = context;
        this.videoIds = videoIds;
        this.videoTitle = videoTitle;
        this.listener = listener;
    }
    public class ViewHolderYoutube extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        LinearLayout video;
        RelativeLayout relativeLayout;
        public ViewHolderYoutube(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.thumbnailYoutube);
            textView = itemView.findViewById(R.id.nameYoutube);
            video = itemView.findViewById(R.id.videoYoutube);
            relativeLayout = itemView.findViewById(R.id.parentRelative);
        }
    }
    @NonNull
    @Override
    public ViewHolderYoutube onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_youtube_layout,parent,false);
        return new ViewHolderYoutube(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderYoutube holder, @SuppressLint("RecyclerView") int position) {
        if (videoIds.size()!=0){
            String title = videoTitle.get(position);
            String Id = videoIds.get(position);
            String url = "https://img.youtube.com/vi/" +Id+ "/hqdefault.jpg";
            holder.textView.setText(title);
            Glide.with(context).load(url).into(holder.imageView);
            holder.video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("videoId",Id);
                    bundle.putString("videoTitle",title);
                    bundle.putInt("videoPos", position);
                    listener.iVideoClicked(bundle);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return videoIds.size();
    }
    public interface onVideoChosen{
        void iVideoClicked(Bundle params);
    }

}