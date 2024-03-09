package tunermusic.metronome.chords;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import tunermusic.metronome.chords.R;

import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

public class ForgotPwdActivity extends Fragment {
    ImageView btnBack;
    private TextInputEditText resetEmail;
    TextInputLayout resetEmailLayout;
    TextInputLayout resetLayout;
    CardView cdNativeAds;
    private ImageButton btnReset;
    private FirebaseAuth auth;
    private Context context;
    LogInActivity logInActivity;
    Animation animation;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_forgot_pwd, container, false);
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
        resetEmail = v.findViewById(R.id.email_reset);
        cdNativeAds = v.findViewById(R.id.native_container);
        resetLayout = v.findViewById(R.id.email_reset_layout);

        btnReset = v.findViewById(R.id.btnSendPwd);
        btnBack = v.findViewById(R.id.btnBack);
        auth = FirebaseAuth.getInstance();

        context = getContext();
        logInActivity = (LogInActivity) getActivity();
        NativeAd nativeAd= logInActivity.getmNativeAd();
        if(nativeAd != null)
        {
            NativeAdView nativeAdView =(NativeAdView) getLayoutInflater().inflate(R.layout.native_ad, null);
            cdNativeAds.addView(nativeAdView);
            logInActivity.populateNativeAdView(nativeAd, nativeAdView);
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animation);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animation);
                String email = resetEmail.getText().toString();
                if(!email.isEmpty())
                {
                    if(checkEmail(email))
                    {
                        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    new AestheticDialog.Builder(logInActivity, DialogStyle.FLAT, DialogType.INFO)
                                            .setTitle("Infor")
                                            .setMessage(getResources().getString(R.string.sended))
                                            .setCancelable(true)
                                            .setAnimation(DialogAnimation.SHRINK)
                                            .setDarkMode(true)
                                            .setOnClickListener(new OnDialogClickListener() {
                                                @Override
                                                public void onClick(@NonNull AestheticDialog.Builder builder) {
                                                    builder.dismiss();
                                                }
                                            }).show();
                                }
                            }
                        });
                    }
                }
                else{
                    resetLayout.setError(getResources().getString(R.string.email_signUp_empty));
                }
            }
        });
        ((InputMethodManager)logInActivity.getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(resetEmail.getWindowToken(), 0);
        resetEmail.clearFocus();

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener((view, keyCode, event) -> {
            if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
            {
                getActivity().getSupportFragmentManager().popBackStack();

                return true;
            }
            return false;
        });

        resetEmailLayout = v.findViewById(R.id.email_reset_layout);
        v.findViewById(R.id.mainLayoutForgot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager)logInActivity.getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(resetEmailLayout.getWindowToken(), 0);
                resetEmailLayout.clearFocus();
            }
        });

        return v;
    }
    private boolean checkEmail(String email)
    {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    logInActivity.getContainerView().setFocusable(false);
                    logInActivity.getContainerView().setClickable(false);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStop() {
        logInActivity.getContainerView().setFocusable(false);
        logInActivity.getContainerView().setClickable(false);
        ((InputMethodManager)logInActivity.getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(resetEmailLayout.getWindowToken(), 0);
        resetEmailLayout.clearFocus();
        super.onStop();
    }
}