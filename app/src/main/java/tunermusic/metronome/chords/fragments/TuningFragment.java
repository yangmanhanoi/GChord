package tunermusic.metronome.chords.fragments;


import static java.lang.Math.abs;
import static java.lang.Math.round;
import static java.lang.Math.signum;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import tunermusic.metronome.chords.R;

import tunermusic.metronome.chords.MainActivity;
import tunermusic.metronome.chords.ModelBottomSheet;

import model.LogEventManager;

public class TuningFragment extends Fragment {
    TextView textViewNote;
    ImageView stringE1,stringA, stringB, stringE2, stringG, stringD,backBut;
    ImageView arrow1, arrow2, arrow3, arrow4, arrow5, arrow6;
    private long startTime;
    private static TuningFragment instance;
    private MainActivity mainActivity;
    ImageView ruler1, ruler2, ruler3, ruler4, ruler5, ruler6, ruler7, ruler8, ruler9, ruler10, ruler11, ruler12, ruler13, btnHelp;

    public int steps = 5;
    public int count = 0;
    List<Integer> recentHz = new ArrayList<>();

    // list of all the notes , string names and their frequencies
    List<String> notes = Arrays.asList("A","A#","B","C","C#","D","D#","E","F","F#","G","G#");
    List<String> strings = Arrays.asList("E2", "A", "D", "G", "B", "E4");
    float[] numbers = new float[]{(float) 82.4, 110, (float) 146.83, (float) 196, (float) 246.94, (float) 329.63};
    AudioDispatcher dispatcher;
    CardView nativeAdLayout;
    NativeAdView nativeAdView;
    public static TuningFragment getInstance()
    {
        if(instance == null) instance = new TuningFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tuning, container, false);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textViewNote = view.findViewById(R.id.textViewNote);
        stringE1 = view.findViewById(R.id.stringE1);
        stringE2 = view.findViewById(R.id.stringE2);
        stringA = view.findViewById(R.id.stringA);
        stringB = view.findViewById(R.id.stringB);
        stringD = view.findViewById(R.id.stringD);
        stringG = view.findViewById(R.id.stringG);
        backBut = view.findViewById(R.id.backBut);
        btnHelp = view.findViewById(R.id.btnHelp);
        startTime = System.currentTimeMillis();
        nativeAdLayout = view.findViewById(R.id.nativeAdTuning);
        mainActivity = (MainActivity) getActivity();

        NativeAd nativeAd= mainActivity.getmNativeAd();
        if (nativeAd!=null){
            nativeAdView=(NativeAdView) getLayoutInflater().inflate(R.layout.native_ad, null);
            nativeAdLayout.addView(nativeAdView);
            populateNativeAdView(nativeAd, nativeAdView);
        }
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animation);
                ModelBottomSheet bottomSheet = new ModelBottomSheet(getResources().getString(R.string.what_can_tune), getResources().getString(R.string.what_tune), ((MainActivity) getActivity()).getmNativeAd());
                bottomSheet.show(getActivity().getSupportFragmentManager(), bottomSheet.getTag());
            }
        });
        backBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
                mainActivity.setCurrentFragment();
            }
        });
        setUpView();
        reset();
        start();
    }

    private void setUpView() {
        ruler1 = getView().findViewById(R.id.ruler1);
        ruler2 = getView().findViewById(R.id.ruler2);
        ruler3 = getView().findViewById(R.id.ruler3);
        ruler4 = getView().findViewById(R.id.ruler4);
        ruler5 = getView().findViewById(R.id.ruler5);
        ruler6 = getView().findViewById(R.id.ruler6);
        ruler7 = getView().findViewById(R.id.ruler7);
        ruler8 = getView().findViewById(R.id.ruler8);
        ruler9 = getView().findViewById(R.id.ruler9);
        ruler10 = getView().findViewById(R.id.ruler10);
        ruler11 = getView().findViewById(R.id.ruler11);
        ruler12 = getView().findViewById(R.id.ruler12);
        ruler13 = getView().findViewById(R.id.ruler13);
        arrow1 = getView().findViewById(R.id.arrow1);
        arrow2 = getView().findViewById(R.id.arrow2);
        arrow3 = getView().findViewById(R.id.arrow3);
        arrow4 = getView().findViewById(R.id.arrow4);
        arrow5 = getView().findViewById(R.id.arrow5);
        arrow6 = getView().findViewById(R.id.arrow6);
    }



    private void calibrate(double num) {
        resetTune();
        if (num>=1){
            if (num<=2||num>=12)  ruler1.setImageResource(R.drawable.tune_chosen_red);
            else if (3<=num&&num<=11&&num!=7) ruler1.setImageResource(R.drawable.tune_chosen_yellow);
            else if (num==7) ruler1.setImageResource(R.drawable.tune_chosen);
        }
        if (num>=2){
            if (num<=2||num>=12)  ruler2.setImageResource(R.drawable.tune_chosen_red);
            else if (3<=num&&num<=11&&num!=7) ruler2.setImageResource(R.drawable.tune_chosen_yellow);
            else if (num==7) ruler2.setImageResource(R.drawable.tune_chosen);
        }
        if (num>=3){
            if (num<=2||num>=12)  ruler3.setImageResource(R.drawable.tune_chosen_red);
            else if (3<=num&&num<=11&&num!=7) ruler3.setImageResource(R.drawable.tune_chosen_yellow);
            else if (num==7) ruler3.setImageResource(R.drawable.tune_chosen);
        }
        if (num>=4){
            if (num<=2||num>=12)  ruler4.setImageResource(R.drawable.tune_chosen_red);
            else if (3<=num&&num<=11&&num!=7) ruler4.setImageResource(R.drawable.tune_chosen_yellow);
            else if (num==7) ruler4.setImageResource(R.drawable.tune_chosen);
        }
        if (num>=5){
            if (num<=2||num>=12)  ruler5.setImageResource(R.drawable.tune_chosen_red);
            else if (3<=num&&num<=11&&num!=7) ruler5.setImageResource(R.drawable.tune_chosen_yellow);
            else if (num==7) ruler5.setImageResource(R.drawable.tune_chosen);
        }
        if (num>=6){
            if (num<=2||num>=12)  ruler6.setImageResource(R.drawable.tune_chosen_red);
            else if (3<=num&&num<=11&&num!=7) ruler6.setImageResource(R.drawable.tune_chosen_yellow);
            else if (num==7) ruler6.setImageResource(R.drawable.tune_chosen);
        }
        if (num>=7){
            if (num<=2||num>=12)  ruler7.setImageResource(R.drawable.tune_center_chosen_red);
            else if (3<=num&&num<=11&&num!=7) ruler7.setImageResource(R.drawable.tune_center_chosen_yellow);
            else if (num==7) ruler7.setImageResource(R.drawable.tune_center_chosen);
        }
        if (num>=8){
            if (num<=2||num>=12)  ruler8.setImageResource(R.drawable.tune_chosen_red);
            else if (3<=num&&num<=11&&num!=7) ruler8.setImageResource(R.drawable.tune_chosen_yellow);
            else if (num==7) ruler8.setImageResource(R.drawable.tune_chosen);
        }
        if (num>=9){
            if (num<=2||num>=12)  ruler9.setImageResource(R.drawable.tune_chosen_red);
            else if (3<=num&&num<=11&&num!=7) ruler9.setImageResource(R.drawable.tune_chosen_yellow);
            else if (num==7) ruler9.setImageResource(R.drawable.tune_chosen);
        }
        if (num>=10){
            if (num<=2||num>=12)  ruler10.setImageResource(R.drawable.tune_chosen_red);
            else if (3<=num&&num<=11&&num!=7) ruler10.setImageResource(R.drawable.tune_chosen_yellow);
            else if (num==7) ruler10.setImageResource(R.drawable.tune_chosen);
        }
        if (num>=11){
            if (num<=2||num>=12)  ruler11.setImageResource(R.drawable.tune_chosen_red);
            else if (3<=num&&num<=11&&num!=7) ruler11.setImageResource(R.drawable.tune_chosen_yellow);
            else if (num==7) ruler11.setImageResource(R.drawable.tune_chosen);
        }
        if (num>=12){
            if (num<=2||num>=12)  ruler12.setImageResource(R.drawable.tune_chosen_red);
            else if (3<=num&&num<=11&&num!=7) ruler12.setImageResource(R.drawable.tune_chosen_yellow);
            else if (num==7) ruler12.setImageResource(R.drawable.tune_chosen);
        }
        if (num>=13){
            if (num<=2||num>=12)  ruler13.setImageResource(R.drawable.tune_chosen_red);
            else if (3<=num&&num<=11&&num!=7) ruler13.setImageResource(R.drawable.tune_chosen_yellow);
            else if (num==7) ruler13.setImageResource(R.drawable.tune_chosen);
        }
    }

    public void tuning(String note,double num){
        if (note.equals("A")){
            reset();
            stringA.setImageResource(R.drawable.string_5_c);
            arrow5.setVisibility(View.VISIBLE);
            if (num<7) arrow5.setImageResource(R.drawable.arrow_up);
            else if (num>7) arrow5.setImageResource(R.drawable.arrow_down);
            else arrow5.setImageResource(R.drawable.correct_tune);
        }else if (note.equals("B")){
            reset();
            stringB.setImageResource(R.drawable.string_2_c);
            arrow2.setVisibility(View.VISIBLE);
            if (num<7) arrow2.setImageResource(R.drawable.arrow_up);
            else if (num>7) arrow2.setImageResource(R.drawable.arrow_down);
            else arrow2.setImageResource(R.drawable.correct_tune);
        }else if (note.equals("D")){
            reset();
            stringD.setImageResource(R.drawable.string_4_c);
            arrow4.setVisibility(View.VISIBLE);
            if (num<7) arrow4.setImageResource(R.drawable.arrow_up);
            else if (num>7) arrow4.setImageResource(R.drawable.arrow_down);
            else arrow4.setImageResource(R.drawable.correct_tune);
        }else if (note.equals("G")){
            reset();
            stringG.setImageResource(R.drawable.string_3_c);
            arrow3.setVisibility(View.VISIBLE);
            if (num<7) arrow3.setImageResource(R.drawable.arrow_up);
            else if (num>7) arrow3.setImageResource(R.drawable.arrow_down);
            else arrow3.setImageResource(R.drawable.correct_tune);
        }else if (note.equals("E4")){
            reset();
            stringE1.setImageResource(R.drawable.string_1_c);
            arrow1.setVisibility(View.VISIBLE);
            if (num<7) arrow1.setImageResource(R.drawable.arrow_up);
            else if (num>7) arrow1.setImageResource(R.drawable.arrow_down);
            else arrow1.setImageResource(R.drawable.correct_tune);
        }else if (note.equals("E2")){
            reset();
            stringE2.setImageResource(R.drawable.string_6_c);
            arrow6.setVisibility(View.VISIBLE);
            if (num<7) arrow6.setImageResource(R.drawable.arrow_up);
            else if (num>7) arrow6.setImageResource(R.drawable.arrow_down);
            else arrow6.setImageResource(R.drawable.correct_tune);
        }
    }
    public void reset(){
        stringD.setImageResource(R.drawable.string_4);
        stringA.setImageResource(R.drawable.string_5);
        stringB.setImageResource(R.drawable.string_2);
        stringG.setImageResource(R.drawable.string_3);
        stringE1.setImageResource(R.drawable.string_1);
        stringE2.setImageResource(R.drawable.string_6);
        arrow1.setVisibility(View.GONE);
        arrow2.setVisibility(View.GONE);
        arrow3.setVisibility(View.GONE);
        arrow4.setVisibility(View.GONE);
        arrow5.setVisibility(View.GONE);
        arrow6.setVisibility(View.GONE);
    }


    public void resetTune(){
        ruler1.setImageResource(R.drawable.tune);
        ruler2.setImageResource(R.drawable.tune);
        ruler3.setImageResource(R.drawable.tune);
        ruler4.setImageResource(R.drawable.tune);
        ruler5.setImageResource(R.drawable.tune);
        ruler6.setImageResource(R.drawable.tune);
        ruler7.setImageResource(R.drawable.tune_center);
        ruler8.setImageResource(R.drawable.tune);
        ruler9.setImageResource(R.drawable.tune);
        ruler10.setImageResource(R.drawable.tune);
        ruler11.setImageResource(R.drawable.tune);
        ruler12.setImageResource(R.drawable.tune);
        ruler13.setImageResource(R.drawable.tune);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Bundle bundle = new Bundle();
        bundle.putInt("time", (int) (System.currentTimeMillis() - startTime));
        LogEventManager.logUserBehavior(LogEventManager.USER_IN_FRAG, bundle, R.id.tunningBut);
        mainActivity.startTime = System.currentTimeMillis();
        if (!dispatcher.isStopped()) dispatcher.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Bundle bundle = new Bundle();
//        bundle.putInt("time", (int) (System.currentTimeMillis() - startTime));
//        LogEventManager.logUserBehavior(LogEventManager.USER_IN_FRAG, bundle, R.id.tunningBut);
//        mainActivity.startTime = System.currentTimeMillis();
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }


    public void start(){
        // getting pitch from live audio , and sending it to processPitch()

        int SAMPLE_RATE = 44100;
        int BUFFER_SIZE = 1024 * 4;
        int OVERLAP = 768 * 4;

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, BUFFER_SIZE, OVERLAP);

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e){
                final float pitchInHz = res.getPitch();
                if (mainActivity!=null){
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            processPitch(pitchInHz);
                        }
                    });
                }
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, SAMPLE_RATE, BUFFER_SIZE, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);

        Thread audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();
    }

    public void processPitch(float pitchInHz) {
        // store pitch 5 times , and then return the most repeated pitch out of it
        // just to reduce randomness , kinda makes it lil bit stable to use ;)
        // steps = 5

        if (count == steps){
            count = 0;
            display(mostCommon(recentHz));
            recentHz.clear();
        }else{
            recentHz.add(round(pitchInHz));
            count++;
        }
    }

    public void display(int pitchInHz){
        // display to user

        String closest_note;
        int i = 0;

        // calculate i , no. of half steps from A4 note (440hz) (https://en.wikipedia.org/wiki/Equal_temperament)
        i = (int) Math.round(12*(Math.log((float) pitchInHz/440) / Math.log(2)));

        // using i , find the index of note played in the "notes" array
        int index = i%12;
        if (index < 0){
            index+=12;
        }

        // if pitch is low causing i = 0 .
        if (i!=0){
            getStringName(pitchInHz);
        }

    }

    public void getStringName(float pitchInHz){
        // guess the string played using pitch
        // (find the closest frequency)
//        button.setTextSize(28);
        float distance = Math.abs(numbers[0] - pitchInHz);
        int idx = 0;
        for(int c = 1; c < numbers.length; c++){
            float cdistance = Math.abs(numbers[c] - pitchInHz);
            if(cdistance < distance){
                idx = c;
                distance = cdistance;
            }
        }
        float targetPitchInHz = numbers[idx];
        int num = (int) ((targetPitchInHz-pitchInHz)/2+7);
        tuning(strings.get(idx),num);
        calibrate(num);
        textViewNote.setText(strings.get(idx));

    }
    public static <T> T mostCommon(List<T> list) {
        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<T, Integer> max = null;

        for (Map.Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return max.getKey();
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView){
        // set the MediaView
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // set other ad assets
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if(nativeAd.getBody() == null)
        {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        }else{
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
    }
}