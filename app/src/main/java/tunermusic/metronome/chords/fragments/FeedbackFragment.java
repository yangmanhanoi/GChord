package tunermusic.metronome.chords.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.ktx.Firebase;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import model.LogEventManager;
import tunermusic.metronome.chords.LogInActivity;
import tunermusic.metronome.chords.R;

public class FeedbackFragment extends Fragment {
    ImageView btnBack;
    LinearLayout container;
    TextInputLayout feedbackLayout;
    private RatingBar ratingBar;
    private MaterialButton sendFbBtn;
    private EditText fbMsg;
    Animation animation;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feedback, container, false);
        ratingBar = v.findViewById(R.id.ratingBar);
        sendFbBtn = v.findViewById(R.id.sendFbButton);
        fbMsg = v.findViewById(R.id.feedback_message);
        btnBack = v.findViewById(R.id.backButFeedback);
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
        container = v.findViewById(R.id.feedback_container);
        feedbackLayout = v.findViewById(R.id.feedback_layout);


        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        // ratingBar.getRating() -> get the rating
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InputMethodManager) getActivity().getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(feedbackLayout.getWindowToken(), 0);
                feedbackLayout.clearFocus();
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
            }
        });
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InputMethodManager) getActivity().getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(feedbackLayout.getWindowToken(), 0);
                feedbackLayout.clearFocus();
            }
        });
        sendFbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animation);
                Bundle bundle = new Bundle();
                bundle.putString("rating", ratingBar.getRating() + "");
                String msg = String.valueOf(fbMsg.getText());
                new AestheticDialog.Builder(getActivity(), DialogStyle.FLAT, DialogType.INFO)
                        .setTitle(getResources().getString(R.string.make_sure))
                        .setMessage(getResources().getString(R.string.sure_msg))
                        .setGravity(Gravity.CENTER)
                        .setCancelable(true)
                        .setAnimation(DialogAnimation.SHRINK)
                        .setDarkMode(true)
                        .setOnClickListener(new OnDialogClickListener() {
                            @Override
                            public void onClick(@NonNull AestheticDialog.Builder builder) {
                                builder.dismiss();
                                bundle.putString("message", msg);
                                LogEventManager.logUserBehavior(LogEventManager.USER_SEND_FEEDBACK, bundle, 0);
                            }
                        })
                        .show();
            }
        });
        return v;
    }
}