package adapter;

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
import tunermusic.metronome.chords.R;

import java.util.ArrayList;

public class AdapterRecyclerTrending extends RecyclerView.Adapter<AdapterRecyclerTrending.ViewHolderTrending> {

    Context context;
    ArrayList<String> videoIds;
    ArrayList<String> videoTitle;
    private onVideoChosen listener;

    public AdapterRecyclerTrending(Context context, ArrayList<String> videoIds, ArrayList<String> videoTitle, onVideoChosen listener) {
        this.context = context;
        this.videoIds = videoIds;
        this.videoTitle = videoTitle;
        this.listener = listener;
    }
    public class ViewHolderTrending extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        LinearLayout video;
        RelativeLayout relativeLayout;
        public ViewHolderTrending(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.thumbnailYoutube);
            textView = itemView.findViewById(R.id.nameYoutube);
            video = itemView.findViewById(R.id.videoYoutube);
            relativeLayout = itemView.findViewById(R.id.parentRelative);
        }
    }
    @NonNull
    @Override
    public ViewHolderTrending onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_trending_layout,parent,false);
        return new ViewHolderTrending(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTrending holder, int position) {
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
                    bundle.putBoolean("IsFromTrending", true);
                    listener.iVideoClicked(bundle);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (videoIds.size()>30){
            return 30;
        } else return videoIds.size();
    }
    public interface onVideoChosen{
        void iVideoClicked(Bundle params);
    }

}