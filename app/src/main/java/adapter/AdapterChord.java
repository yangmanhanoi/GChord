package adapter;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import model.Pair;
import tunermusic.metronome.chords.R;

public class  AdapterChord extends RecyclerView.Adapter<AdapterChord.ViewHolder> {
    private Context context;
    private int layout;
    private List<Pair> list;
    private List<Double> listBeatTime;
    private int currentChord = 0;
    private long mLastClickTime = 0;
    private List<Pair> sendList;
    private static int  currentNode = 1;
    private onChordSelected listener;
public AdapterChord(Context context, int layout, List<Pair> sendList ,onChordSelected listener)
{
    this.context = context;
    this.layout = layout;
    this.sendList = sendList;
    this.listener = listener;
}

    public void setSendList(List<Pair> sendList) {
        this.sendList = sendList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_chord_beat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position % 4 == 0) holder.indicator.setVisibility(View.VISIBLE);
        else holder.indicator.setVisibility(View.GONE);
        String txt_chord = sendList.get(position).getKey();
        if(!txt_chord.equals("")) txt_chord = Character.toUpperCase(txt_chord.charAt(0)) + txt_chord.substring(1);
        holder.txt_node.setText(txt_chord);
        if(position == currentChord)
        {
            holder.cardView.setActivated(true);
            holder.txt_node.setTextColor(context.getResources().getColor(R.color.black));
        }
        else {
            holder.cardView.setActivated(false);
            holder.txt_node.setTextColor(context.getResources().getColor(R.color.black));
        }

        //holder.cardView.setSelected(!holder.cardView.isSelected());
    }

    @Override
    public int getItemCount() {
        return sendList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View indicator;
        LinearLayout cardView;
        TextView txt_node;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            indicator = itemView.findViewById(R.id.indicator);
            cardView = itemView.findViewById(R.id.cardViewNote);
            txt_node = itemView.findViewById(R.id.note);

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
                    notifyItemChanged(currentChord);
                    currentChord = getAdapterPosition();
                    notifyItemChanged(currentChord);
                    listener.iChordClicked(currentChord);
                    notifyDataSetChanged();
                }
            });
        }
    }
    public interface onChordSelected{
        void iChordClicked(int pos);
        void iContinousClicked();
    }
    public void changeSelected(int pos)
    {
        notifyItemChanged(currentChord);
        currentChord = pos;
        notifyItemChanged(currentChord);
    }
}
