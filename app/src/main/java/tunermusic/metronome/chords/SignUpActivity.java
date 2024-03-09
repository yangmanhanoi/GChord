package tunermusic.metronome.chords;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

public class SignUpActivity extends Fragment implements View.OnClickListener{
    public static final  String TAG = "SignUpAct";
    private TextInputEditText tf_emailSu, tf_pwdSu, tf_rePwdSu;
    private TextInputLayout emailSu, pwdSu, rePwdSu;
    private ImageButton btnSignUp;
    private TextView btnBackLogIn;
    LogInActivity logInActivity;
    private FirebaseAuth auth;
    private CardView cdNativeAds;
    ImageView imgBack;
    Context context;
    Animation animation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_signup, container, false);
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
        imgBack = v.findViewById(R.id.img_back);
        tf_emailSu = v.findViewById(R.id.tf_emailSu);
        tf_pwdSu = v.findViewById(R.id.tf_pwdSu);
        tf_rePwdSu = v.findViewById(R.id.tf_rePwd);
        btnSignUp = v.findViewById(R.id.btnSignUp);
        btnBackLogIn = v.findViewById(R.id.backLogIn);
        emailSu = v.findViewById(R.id.email_layoutSU);
        pwdSu = v.findViewById(R.id.pwd_layoutSU);
        rePwdSu = v.findViewById(R.id.repwd_layout);
        cdNativeAds = v.findViewById(R.id.native_container);

        auth = FirebaseAuth.getInstance();
        logInActivity = (LogInActivity) getActivity();
        context = v.getContext();

        double angleInRadians = Math.toRadians(0);
        double length2 = btnBackLogIn.getPaint().getTextSize();

        double endX2 = Math.sin(angleInRadians)*length2;
        double endY2 = Math.cos(angleInRadians)*length2;


        Shader shader1 = new LinearGradient((float) 0, (float) 0, (float) endX2, (float) endY2,
                context.getResources().getColor(R.color.gradient_1), context.getResources().getColor(R.color.gradient_2),Shader.TileMode.CLAMP);
        btnBackLogIn.getPaint().setShader(shader1);

        btnSignUp.setOnClickListener(this);
        btnBackLogIn.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        NativeAd nativeAd= logInActivity.getmNativeAd();
        if(nativeAd != null)
        {
            NativeAdView nativeAdView =(NativeAdView) getLayoutInflater().inflate(R.layout.native_ad, null);
            cdNativeAds.addView(nativeAdView);
            logInActivity.populateNativeAdView(nativeAd, nativeAdView);
        }
        v.findViewById(R.id.mainLayoutSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager)logInActivity.getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(emailSu.getWindowToken(), 0);
                ((InputMethodManager)logInActivity.getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(pwdSu.getWindowToken(), 0);
                ((InputMethodManager)logInActivity.getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(rePwdSu.getWindowToken(), 0);
                emailSu.clearFocus();
                pwdSu.clearFocus();
                rePwdSu.clearFocus();
            }
        });


        return v;
    }
    @Override
    public void onClick(View view) {
        view.startAnimation(animation);
        int id = view.getId();
        switch (id)
        {
            case R.id.btnSignUp:
                String user = tf_emailSu.getText().toString();
                String pwd = tf_pwdSu.getText().toString();
                String rePwd = tf_rePwdSu.getText().toString();
                if(user.isEmpty()) emailSu.setError(getResources().getString(R.string.email_signUp_empty));
                if(pwd.isEmpty()) pwdSu.setError(getResources().getString(R.string.pwd_signUp_empty));
                if(rePwd.isEmpty()) rePwdSu.setError(getResources().getString(R.string.rePwd_empty));
                else
                {
                    if(pwd.equals(rePwd))
                    {
                        auth.createUserWithEmailAndPassword(user, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    new AestheticDialog.Builder(getActivity(), DialogStyle.FLAT, DialogType.SUCCESS)
                                            .setTitle("Success")
                                            .setMessage(getResources().getString(R.string.sign_up_success))
                                            .setCancelable(true)
                                            .setAnimation(DialogAnimation.SHRINK)
                                            .setDarkMode(true)
                                            .setOnClickListener(new OnDialogClickListener() {
                                                @Override
                                                public void onClick(@NonNull AestheticDialog.Builder builder) {
                                                    getActivity().getSupportFragmentManager().popBackStack();
                                                    builder.dismiss();
                                                }
                                            }).show();
                                }
                                else
                                {
                                    new AestheticDialog.Builder(getActivity(), DialogStyle.FLAT, DialogType.ERROR)
                                            .setTitle("Failed")
                                            .setMessage(getResources().getString(R.string.sign_up_failed))
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
                    else{
                        rePwdSu.setError(getResources().getString(R.string.rePwd_error));
                    }
                }
                break;
            case R.id.backLogIn:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
                break;
            case R.id.img_back:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

        }
    }

    @Override
    public void onStop() {
        logInActivity.getContainerView().setFocusable(false);
        logInActivity.getContainerView().setClickable(false);

        ((InputMethodManager)logInActivity.getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(emailSu.getWindowToken(), 0);
        ((InputMethodManager)logInActivity.getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(pwdSu.getWindowToken(), 0);
        ((InputMethodManager)logInActivity.getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(rePwdSu.getWindowToken(), 0);
        emailSu.clearFocus();
        pwdSu.clearFocus();
        rePwdSu.clearFocus();
        super.onStop();
    }
}