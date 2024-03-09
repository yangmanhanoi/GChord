package tunermusic.metronome.chords.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import tunermusic.metronome.chords.R;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class InstrumentFragment extends Fragment {

    MaterialCardView guitar, ukulele, piano, mandolin;
    public RadioGroup radioGroup;
    ImageView diagram;
    LinearLayout instrument;
    MaterialButton start;
    Context context;
    private String instr = "", type = "";
    public boolean isFragmentVisible = true;
    private onInstrumentAction listener;
    private Animation slideUp, slideDown;

    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_instrument, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        guitar = view.findViewById(R.id.guitarButton);
        ukulele = view.findViewById(R.id.ukuleleButton);
        piano = view.findViewById(R.id.pianoButton);
        mandolin = view.findViewById(R.id.mandolinButton);
        radioGroup = view.findViewById(R.id.tabInstrument);
        radioGroup.check(R.id.diagramInstrument);
        type = "Diagram";
        diagram = view.findViewById(R.id.diagramImage);
        instrument = view.findViewById(R.id.instrumentImage);
        start = view.findViewById(R.id.startButton);
        context = getContext();
        slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down);
    }

    @Override
    public void onResume() {
        super.onResume();
        guitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                guitar.setChecked(true);
                instr = "guitar";
            }
        });
        ukulele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                ukulele.setChecked(true);
                instr = "ukulele";
            }
        });
        piano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                piano.setChecked(true);
                instr = "piano";
            }
        });
        mandolin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
                mandolin.setChecked(true);
                instr = "mandolin";
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i==R.id.diagramInstrument) {
                    instrument.setVisibility(View.VISIBLE);
                    diagram.setVisibility(View.GONE);
                    type = "Diagram";
                } else {
                    instrument.setVisibility(View.GONE);
                    diagram.setVisibility(View.VISIBLE);
                    type = "Overview";
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.iUserChosen(instr, type);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof  onInstrumentAction)
        {
            listener = (onInstrumentAction) context;
        }
        else throw new RuntimeException(context.toString() + "xxxx");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void reset(){
        guitar.setChecked(false);
        ukulele.setChecked(false);
        piano.setChecked(false);
        mandolin.setChecked(false);
    }
    public void setListener(onInstrumentAction listener)
    {
        if(listener != null)
        {
            this.listener = listener;

        }
        else return;
    }
    public interface onInstrumentAction{
        void iUserChosen(String instrument, String type);
    }
}