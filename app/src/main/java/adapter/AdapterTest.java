package adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dqt.libs.chorddroid.components.ChordTextureView;

import java.util.List;

import model.Pair;
import tunermusic.metronome.chords.R;

public class AdapterTest extends RecyclerView.Adapter<AdapterTest.ViewHolder> {
    private List<Pair> list;
    private Context context;
    private int layout;
    private int currentImg = 0;
    private int lastPosition = -1;
    public AdapterTest(Context context, int layout, List<Pair> list)
    {
        this.context = context;
        this.layout = layout;
        this.list = list;
    }
    @NonNull
    @Override
    public AdapterTest.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_test, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTest.ViewHolder holder, int position) {
        Log.e("chord", list.get(position).getKey());
        holder.chordTextureView.drawChord(list.get(position).getKey());

        //holder.chordTextureView.drawChord("fmaj7");
    }

    @Override
    public int getItemCount() {
        Log.e("size", list.size() + "");
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ChordTextureView chordTextureView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chordTextureView = itemView.findViewById(R.id.chord_texture);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels/2;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            chordTextureView.setLayoutParams(layoutParams);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyItemChanged(currentImg);
                    currentImg = getAdapterPosition();
                    notifyItemChanged(currentImg);
                    // listener.iImageClicked(currentImg);
                    notifyDataSetChanged();
                }
            });
        }
    }
}