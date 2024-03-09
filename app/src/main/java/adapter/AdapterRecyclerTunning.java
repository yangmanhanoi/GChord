package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import tunermusic.metronome.chords.R;

public class AdapterRecyclerTunning extends RecyclerView.Adapter<AdapterRecyclerTunning.ViewHolderTunning> {
    public AdapterRecyclerTunning() {
    }

    public class ViewHolderTunning extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolderTunning(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewTunning);
        }
    }
    @NonNull
    @Override
    public ViewHolderTunning onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_recycler_tunning,parent,false);
        return new ViewHolderTunning(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTunning holder, int position) {
        if (position==6){
            holder.imageView.setImageResource(R.drawable.tune_center);
        } else holder.imageView.setImageResource(R.drawable.tune);
    }

    @Override
    public int getItemCount() {
        return 13;
    }
}
