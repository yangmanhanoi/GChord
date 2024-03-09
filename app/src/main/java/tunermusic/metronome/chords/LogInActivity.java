package tunermusic.metronome.chords;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import tunermusic.metronome.chords.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.remoteconfig.ConfigUpdate;
import com.google.firebase.remoteconfig.ConfigUpdateListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import cn.pedant.SweetAlert.SweetAlertDialog;
import model.DataLocalManager;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener{
    public FirebaseAuth auth;
    private String videoTrending, videoTitle;
    private TextInputLayout passwordLayout, emailLayout;
    private TextInputEditText tf_email, tf_pwd;
    private ImageButton logInGoogle;
    private ImageButton logIn;
    private String adsNativeId;
    private ImageView btnBack;
    private TextView forgotPwd, creAcc;
    private static final String TAG = "LogInActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int FB_SIGN_IN = 9002;
    public static final String NATIVE_ID = "adsNativeId";
    private GoogleSignInClient mGoogleSignInClient;
    private NativeAd mNativeAd;
    private NativeAdView adView;
    private AdLoader adLoader;
    SweetAlertDialog sweetAlertDialog;
    private FrameLayout containerView;
    private CardView cdNativeAds;
    private Context context;
    private CallbackManager mCallBackManager;
    FirebaseUser user;
    Bundle bundle;
    Animation animation;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        animation = AnimationUtils.loadAnimation(this, R.anim.blink);
        bundle = getIntent().getBundleExtra("bundle");
        mCallBackManager = CallbackManager.Factory.create();
        tf_email = findViewById(R.id.tf_email);
        tf_pwd = findViewById(R.id.tf_password);
        cdNativeAds = findViewById(R.id.cdNativeAds);
        logIn = findViewById(R.id.btnLogIn);
        btnBack = findViewById(R.id.btnBack);
        logInGoogle = findViewById(R.id.logInGoogle);
        passwordLayout = findViewById(R.id.password_layout);
        emailLayout = findViewById(R.id.email_layout);
        auth = FirebaseAuth.getInstance();
        forgotPwd = findViewById(R.id.forgot_pwd);
        creAcc = findViewById(R.id.create_account);
        context = this;
        containerView = findViewById(R.id.fragment_container_view);

        videoTitle = getIntent().getStringExtra("videosTitle");
        videoTrending = getIntent().getStringExtra("videosId");

        logIn.setOnClickListener(this);
        forgotPwd.setOnClickListener(this);
        creAcc.setOnClickListener(this);
        logInGoogle.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        remoteConfig();
        findViewById(R.id.mainLayoutLogIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(emailLayout.getWindowToken(), 0);
                emailLayout.clearFocus();
                ((InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(passwordLayout.getWindowToken(), 0);
                passwordLayout.clearFocus();
            }
        });


        LoginManager.getInstance().registerCallback(mCallBackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //handleFacebookAccessToken(loginResult.getAccessToken());
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(@Nullable JSONObject jsonObject, @Nullable GraphResponse graphResponse) {
                                        if(jsonObject != null)
                                        {
                                            try {
                                                String name = jsonObject.getString("name");
                                                String email = jsonObject.getString("email");
                                                String fbUserID = jsonObject.getString("id");
                                                disconnectFromFacebook();
                                                updateUI(null);

                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }

                                        }
                                    }
                                });
                        Bundle param = new Bundle();
                        param.putString(
                                "fields",
                                "id, name, email, gender, birthday"
                        );
                        request.setParameters(param);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(@NonNull FacebookException e) {
                    }
                });


    }
    private void disconnectFromFacebook()
    {
        if(AccessToken.getCurrentAccessToken() == null)
            return;
        new GraphRequest(AccessToken.getCurrentAccessToken(),
                "/me/permissions/",
                null,
                HttpMethod.DELETE,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(@NonNull GraphResponse graphResponse) {
                        LoginManager.getInstance().logOut();
                    }
                }).executeAsync();
    }
    @Override
    protected void onStart() {
        super.onStart();
        user = auth.getCurrentUser();
    }
    private void signIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void firebaseAuthWithGoogle(String idToken)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser user = auth.getCurrentUser();
                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            DataLocalManager.setIsLogin(true);
                            intent.putExtra("bundle",bundle);
                            startActivity(intent);
                            sweetAlertDialog.dismissWithAnimation();
                        }
                        else {
                            new AestheticDialog.Builder(LogInActivity.this, DialogStyle.FLAT, DialogType.ERROR)
                                    .setTitle("Error")
                                    .setMessage("Login failed")
                                    .setDuration(2000)
                                    .setGravity(Gravity.CENTER)
                                    .setCancelable(true)
                                    .setAnimation(DialogAnimation.SHRINK)
                                    .setDarkMode(true)
                                    .show();
                        }
                    }
                });
    }

    public FrameLayout getContainerView() {
        return containerView;
    }

    @Override
    public void onClick(View view) {
        view.startAnimation(animation);
        int id = view.getId();
        switch (id)
        {
            case R.id.btnLogIn:
                sweetAlertDialog = new SweetAlertDialog(LogInActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.setTitle("Processing ...");
                sweetAlertDialog.setCancelable(false);sweetAlertDialog.hideConfirmButton();
                sweetAlertDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.secondary_purple));
                String email = tf_email.getText().toString().trim();
                String pass = tf_pwd.getText().toString().trim();
                if(!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    if(!pass.isEmpty())
                    {
                        sweetAlertDialog = new SweetAlertDialog(LogInActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                        sweetAlertDialog.setTitle("Processing ...");
                        sweetAlertDialog.setCancelable(false);sweetAlertDialog.hideConfirmButton();
                        sweetAlertDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.secondary_purple));
                        sweetAlertDialog.show();
                        auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                sweetAlertDialog.dismissWithAnimation();
                                DataLocalManager.setIsLogin(true);
                                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                intent.putExtra("bundle",bundle);
                                startActivity(intent);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                new AestheticDialog.Builder(LogInActivity.this, DialogStyle.FLAT, DialogType.ERROR)
                                        .setTitle("Error")
                                        .setMessage("Login failed")
                                        .setDuration(2000)
                                        .setGravity(Gravity.CENTER)
                                        .setAnimation(DialogAnimation.SHRINK)
                                        .setDarkMode(false)
                                        .show();
                                sweetAlertDialog.dismissWithAnimation();
                            }

                        });
                    }
                    else{
                        passwordLayout.setError(getResources().getString(R.string.pwd_empty));
                    }
                }
                else if(email.isEmpty())
                {
                    emailLayout.setError(getResources().getString(R.string.email_empty));
                }
                else{
                    emailLayout.setError(getResources().getString(R.string.email_error));
                }
                break;
            case R.id.create_account:
                containerView.setFocusable(true);
                containerView.setClickable(true);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_left,R.anim.fade_out,R.anim.fade_in,R.anim.slide_right).replace(R.id.fragment_container_view, new SignUpActivity()).addToBackStack(SignUpActivity.TAG).commit();
                break;
            case R.id.forgot_pwd:
                containerView.setFocusable(true);
                containerView.setClickable(true);
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_up,R.anim.fade_out,R.anim.fade_in,R.anim.slide_down).replace(R.id.fragment_container_view, new ForgotPwdActivity()).addToBackStack(null).commit();
                break;
            case R.id.logInGoogle:
                sweetAlertDialog = new SweetAlertDialog(LogInActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.setTitle("Loading Chord");
                sweetAlertDialog.setCancelable(false);sweetAlertDialog.hideConfirmButton();
                sweetAlertDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.secondary_purple));
                sweetAlertDialog.show();

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                if(mGoogleSignInClient != null) signIn();

                break;
            case R.id.btnBack:
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mCallBackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        switch (requestCode)
        {
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if(account.getAccount() == null)
                    {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                    else
                    {
                        firebaseAuthWithGoogle(account.getIdToken());
                    }
                } catch (ApiException e) {
                    sweetAlertDialog.dismissWithAnimation();
                }
                break;
            case RESULT_CANCELED:
                sweetAlertDialog.dismissWithAnimation();
        }
    }
    private void updateUI(FirebaseUser user)
    {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        intent.putExtra("bundle",bundle);
        startActivity(intent);
    }
    private void remoteConfig()
    {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                            adsNativeId = mFirebaseRemoteConfig.getString(NATIVE_ID);
                            loadNativeAd();
                        } else {
                        }

                    }
                });

        mFirebaseRemoteConfig.addOnConfigUpdateListener(new ConfigUpdateListener() {
            @Override
            public void onUpdate(@NonNull ConfigUpdate configUpdate) {
                Log.d(TAG, "Updated keys: " + configUpdate.getUpdatedKeys());
                if (Arrays.asList(NATIVE_ID).contains(configUpdate.getUpdatedKeys())) {
                    mFirebaseRemoteConfig.activate()
                            .addOnCompleteListener(task -> {
                                FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                                adsNativeId = mFirebaseRemoteConfig.getString(NATIVE_ID);
                                loadNativeAd();
                            });
                }
            }

            @Override
            public void onError(@NonNull FirebaseRemoteConfigException error) {

            }
        });

    }
    private void loadNativeAd()
    {
        AdLoader.Builder adBuilder =new AdLoader.Builder(this, adsNativeId);
        adBuilder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                if(isDestroyed() || isFinishing() || isChangingConfigurations())
                {
                    nativeAd.destroy();
                    return;
                }
                if(mNativeAd != null)
                {
                    mNativeAd.destroy();
                }
                mNativeAd = nativeAd;
                adView = (NativeAdView) getLayoutInflater().inflate(R.layout.native_ad, null);
                populateNativeAdView(mNativeAd, adView);
                cdNativeAds.removeAllViews();
                cdNativeAds.addView(adView);
            }
        });
        VideoOptions options =new VideoOptions.Builder().setStartMuted(true).build();
        NativeAdOptions nativeAdOptions= new NativeAdOptions.Builder().setVideoOptions(options).build();
        adLoader = adBuilder.withAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }

    public NativeAd getmNativeAd() {
        return mNativeAd;
    }

    public void populateNativeAdView(NativeAd nativeAd, NativeAdView adView){
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateUI(null);
    }
    public void back2LogIn(Activity activity)
    {
        Intent intent = new Intent(activity, LogInActivity.class);
        intent.putExtra("bundle",bundle);
        startActivity(intent);
        containerView.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(emailLayout.getWindowToken(), 0);
        emailLayout.clearFocus();
        ((InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(passwordLayout.getWindowToken(), 0);
        passwordLayout.clearFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tf_email.clearFocus();
        tf_pwd.clearFocus();
        ((InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(emailLayout.getWindowToken(), 0);
        emailLayout.clearFocus();
        ((InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(passwordLayout.getWindowToken(), 0);
        passwordLayout.clearFocus();
    }
}