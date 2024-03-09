package adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dqt.libs.chorddroid.components.ChordTextureView;

import java.util.List;

import model.Pair;
import tunermusic.metronome.chords.R;

public class AdapterChordImage extends RecyclerView.Adapter<AdapterChordImage.ViewHolder> {
    private int currentImg = 0;
    private int lastPosition = -1;
    private long mLastClickTime = 0;
    private Context context;
    private int layout;
    private List<Pair> imgName;
    private onImageChosen listener;
    public AdapterChordImage(Context context, int layout, List<Pair> imgName, onImageChosen listener)
    {
        this.context = context;
        this.layout = layout;
        this.imgName = imgName;
        this.listener = listener;
    }

    public void setImgName(List<Pair> imgName) {
        this.imgName = imgName;
    }

    @NonNull
    @Override
    public AdapterChordImage.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_img_chord, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChordImage.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(position > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_left);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
        if(imgName.get(position).getKey() != "")
        {
            holder.img.drawChord(imgName.get(position).getKey());
        }
    }

    @Override
    public int getItemCount() {
        return imgName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ChordTextureView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.chord_texture);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels/2;

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            img.setLayoutParams(layoutParams);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long currentTime = SystemClock.uptimeMillis();
                    if(currentTime - mLastClickTime < 1000)
                    {
                        listener.iContinousClicked();
                        return;
                    }
                    mLastClickTime = currentTime;
                    notifyItemChanged(currentImg);
                    currentImg = getAdapterPosition();
                    notifyItemChanged(currentImg);
                    listener.iImageClicked(currentImg);
                    notifyDataSetChanged();
                }
            });
        }
    }
    public interface onImageChosen{
        void iImageClicked(int pos);
        void iContinousClicked();
    }
    public void changeSelected(int pos)
    {
        notifyItemChanged(currentImg);
        currentImg = pos;
        notifyItemChanged(currentImg);
    }
}