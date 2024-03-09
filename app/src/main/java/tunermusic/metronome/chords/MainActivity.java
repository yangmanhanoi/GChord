package tunermusic.metronome.chords;

import static java.util.ResourceBundle.getBundle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.ConfigUpdate;
import com.google.firebase.remoteconfig.ConfigUpdateListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import adapter.AdapterRecyclerTrending;
import model.DataLocalManager;
import model.LogEventManager;
import tunermusic.metronome.chords.fragments.ChordFragment;
import tunermusic.metronome.chords.fragments.FeedbackFragment;
import tunermusic.metronome.chords.fragments.HomeFragment;
import tunermusic.metronome.chords.fragments.SearchFragment;
import tunermusic.metronome.chords.fragments.TuningFragment;

public class MainActivity extends AppCompatActivity {
    public static final String BANNER_ID = "adsBannerId";
    public static final String MOD_APP_ID = "adsMobAppId";
    public static final String NATIVE_ID = "adsNativeId";
    public static final String REWARD_ID = "adsRewardedId";
    public static final String INTERSTITIAL_ID = "adsInterstitialId";
    public static final String NO_SONG = "noSongUnlocked";
    public static final String TAG = "MainActivity";
    public static final String TIMER_AD = "timerAd";


    ImageView homeBut,searchBut, chordBut, tunningBut, propertiesBut;
    private FirebaseAnalytics mFirebaseAnalytics;
    public FrameLayout adContainerView;
    public AdView mAdView;
    LinearLayout bottomBar;
    LinearLayout logOutBut,recentBut, premiumBut,feedBackBut,aboutBut,signInBut;
    public String ids, titles;
    public String videoId="", videoTitle="", shareVideoURL ="";
    public String data="",beat="";
    public long startTime;

    public String adsBannerId, adsNativeId, adsRewardedId, adsInterstitialId, adsMobAppId, noSongUnlocked;
    InterstitialAd mInterstitialAd =null;
    public RewardedAd mRewardedAd = null;
    long timerAds;
    boolean isShowedAds = true;
    Handler handler;
    public String currenVideoId;
    public Fragment currentFragment;
    private long waitTime;
    private boolean isFirstChosen = true;
    public int countHome = 0;

    private NativeAd mNativeAd;
    private NativeAdView adView;
    private AdLoader adLoader;
    public DrawerLayout drawerLayout;
    public ArrayList<String> videoIdsTrending = new ArrayList<>();
    public ArrayList<String> videoTitleTrending = new ArrayList<>();
    ModelBottomSheet modelBottomSheet;
    public Bundle bd;
    Animation animation;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animation = AnimationUtils.loadAnimation(this, R.anim.blink);
        if(savedInstanceState != null)
        {
            ids = savedInstanceState.getString("videosId","");
            titles = savedInstanceState.getString("videosTitle","");
            data = savedInstanceState.getString("data","");
            beat = savedInstanceState.getString("beat","");
            videoId = savedInstanceState.getString("currentId","");
            videoTitle = savedInstanceState.getString("currentTitle","");
        }
        else {
            bd = getIntent().getBundleExtra("bundle");
            ids = bd.getString("videosId");
            titles = bd.getString("videosTitle");
            shareVideoURL = bd.getString("shareVideoURL");
            data = "";
            beat = "";
        }
        init();
        remoteConfig();
        handler = new Handler();
        bottomBar = findViewById(R.id.bottomBar);
        startTime = System.currentTimeMillis();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        currentFragment = HomeFragment.getInstance();
        propertiesBut = findViewById(R.id.propertiesButMain);
        logOutBut = findViewById(R.id.logOutButton);
        signInBut = findViewById(R.id.signInButton);
        recentBut = findViewById(R.id.recentVideoButton);
        feedBackBut = findViewById(R.id.feedBackButton);
        drawerLayout = findViewById(R.id.naviagationView);
        adContainerView = findViewById(R.id.ad_view_container);
        aboutBut = findViewById(R.id.aboutButton);
        drawerLayout.closeDrawers();
        propertiesBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
        if (DataLocalManager.getIsLogin()){
            logOutBut.setVisibility(View.VISIBLE);
            signInBut.setVisibility(View.GONE);
        }else {
            logOutBut.setVisibility(View.GONE);
            signInBut.setVisibility(View.VISIBLE);
        }
        logOutBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                DataLocalManager.setIsLogin(false);
                drawerLayout.closeDrawer(Gravity.LEFT);
                signInBut.setVisibility(View.VISIBLE);
                logOutBut.setVisibility(View.GONE);
            }
        });
        feedBackBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                loadFragment(new FeedbackFragment(), 2);

            }
        });
        recentBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                loadFragment(new HomeFragment(),1);
            }
        });
        signInBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(MainActivity.this,LogInActivity.class);
                intent.putExtra("bundle",bd);
                startActivity(intent);
            }
        });
        aboutBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                ModelBottomSheet bottomSheet = new ModelBottomSheet(getResources().getString(R.string.what_can_tune), getResources().getString(R.string.what_tune),getmNativeAd());
                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            }
        });
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        if(ids != null || ids.length() > 0)
        {
            StringTokenizer stringTokenizer = new StringTokenizer(ids);
            while (stringTokenizer.hasMoreTokens()) {
                videoIdsTrending.add(stringTokenizer.nextToken());
            }
            stringTokenizer = new StringTokenizer(titles, ";");
            while (stringTokenizer.hasMoreTokens()) {
                videoTitleTrending.add(stringTokenizer.nextToken());
            }
            loadFragment(new HomeFragment(),1);
        }
        // Load native ads
        List<String> testDeviceIds = Arrays.asList(AdRequest.DEVICE_ID_EMULATOR);
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
        if (shareVideoURL.length()!=0){
            reset();
            searchBut.setImageResource(R.drawable.search_c);
            loadFragment(new SearchFragment(),1);
            if(!(currentFragment instanceof SearchFragment))
            {
                waitTime = System.currentTimeMillis() - startTime;
                Bundle searchBundle = generateBundle(2, countHome, waitTime);
                LogEventManager.logUserBehavior(LogEventManager.USER_IN_MENU, searchBundle, 0);
                startTime = System.currentTimeMillis();
            }
            currentFragment = SearchFragment.getInstance();

        }
    }

    private void loadInterstitialAds(){
        mInterstitialAd=null;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, adsInterstitialId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mInterstitialAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
            }
        });
    }
    private void loadRewardedAd(){
        mRewardedAd=null;
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, adsRewardedId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mRewardedAd=null;
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                mRewardedAd = rewardedAd;
            }
        });
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
                            getAdID();
                        } else {

                        }

                    }
                });

        mFirebaseRemoteConfig.addOnConfigUpdateListener(new ConfigUpdateListener() {
            @Override
            public void onUpdate(@NonNull ConfigUpdate configUpdate) {
                Log.d(TAG, "Updated keys: " + configUpdate.getUpdatedKeys());
                if (Arrays.asList(BANNER_ID, MOD_APP_ID, NATIVE_ID, REWARD_ID, INTERSTITIAL_ID, NO_SONG).contains(configUpdate.getUpdatedKeys())) {
                    mFirebaseRemoteConfig.activate()
                            .addOnCompleteListener(task -> {
                                getAdID();
                            });
                }
            }

            @Override
            public void onError(@NonNull FirebaseRemoteConfigException error) {

            }
        });

    }
    private void getAdID()
    {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        this.adsBannerId = mFirebaseRemoteConfig.getString(BANNER_ID);
        this.adsInterstitialId = mFirebaseRemoteConfig.getString(INTERSTITIAL_ID);
        this.adsRewardedId = mFirebaseRemoteConfig.getString(REWARD_ID);
        this.adsNativeId = mFirebaseRemoteConfig.getString(NATIVE_ID);
        this.adsMobAppId = mFirebaseRemoteConfig.getString(MOD_APP_ID);
        this.noSongUnlocked = mFirebaseRemoteConfig.getString(NO_SONG);
        this.timerAds = mFirebaseRemoteConfig.getLong(TIMER_AD);

        loadNativeAd();
        // load banner ad
        loadBannerAd();
        loadInterstitialAds();
        loadRewardedAd();
    }
    private void clickForInterstitialAds(){
        if (isShowedAds){
            isShowedAds = false;
            if (mInterstitialAd!=null) {
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        loadInterstitialAds();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        loadInterstitialAds();
                    }
                });
                mInterstitialAd.show(MainActivity.this);
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isShowedAds=true;
                }
            },timerAds);
        }
    }
    private void loadBannerAd()
    {
        mAdView = new AdView(this);
        mAdView.setAdUnitId(adsBannerId);
        mAdView.setAdSize(getAdSize());

        adContainerView.removeAllViews();
        adContainerView.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adContainerView.setVisibility(View.VISIBLE);
                adContainerView.removeAllViews();
                adContainerView.addView(mAdView);
                mAdView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
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
    public AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
    private void init() {
        homeBut = findViewById(R.id.homeBut);
        homeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                reset();
                homeBut.setImageResource(R.drawable.home_c);
                if(!(currentFragment instanceof HomeFragment))
                {
                    clickForInterstitialAds();
                    loadFragment(new HomeFragment(),1);
                    waitTime = System.currentTimeMillis() - startTime;
                    Bundle homeBundle = generateBundle(0, countHome, waitTime);
                    LogEventManager.logUserBehavior(LogEventManager.USER_IN_MENU, homeBundle, 0);
                    startTime = System.currentTimeMillis();
                    countHome++;
                }
                currentFragment = HomeFragment.getInstance();
            }
        });
        searchBut = findViewById(R.id.searchBut);
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                reset();
                searchBut.setImageResource(R.drawable.search_c);
                if(!(currentFragment instanceof SearchFragment))
                {
                    clickForInterstitialAds();
                    loadFragment(new SearchFragment(),1);
                    waitTime = System.currentTimeMillis() - startTime;
                    Bundle searchBundle = generateBundle(1, countHome, waitTime);
                    LogEventManager.logUserBehavior(LogEventManager.USER_IN_MENU, searchBundle, 0);
                    startTime = System.currentTimeMillis();
                }
                currentFragment = SearchFragment.getInstance();
            }
        });
        chordBut = findViewById(R.id.chordBut);
        chordBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                clickForInterstitialAds();
                if (beat.length()!=0){
                    loadFragment(ChordFragment.getInstance(),2);
                } else {
                    WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    DisplayMetrics metrics = new DisplayMetrics();
                    display.getMetrics(metrics);
                    modelBottomSheet = new ModelBottomSheet(getResources().getString(R.string.chord_fragment), getResources().getString(R.string.chord_fragment_content), videoIdsTrending, videoTitleTrending, new AdapterRecyclerTrending.onVideoChosen() {
                        @Override
                        public void iVideoClicked(Bundle params) {
                            ChordFragment.getInstance().setArguments(params);
                            modelBottomSheet.dismiss();
                            loadFragment(ChordFragment.getInstance(),2);
                        }
                    });
                    modelBottomSheet.show(getSupportFragmentManager(), modelBottomSheet.getTag());
                }
                currentFragment = ChordFragment.getInstance();
            }
        });
        tunningBut = findViewById(R.id.tunningBut);
        tunningBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                if (ContextCompat.checkSelfPermission(getApplication(), android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    if(!DataLocalManager.getIsLogin())
                    {
                        if(DataLocalManager.getIsAvail() && !(currentFragment instanceof TuningFragment))
                        {
                            clickForInterstitialAds();
                            loadFragment(TuningFragment.getInstance(),2);
                            DataLocalManager.setIsAvailable(false);
                            waitTime = System.currentTimeMillis() - startTime;
                            Bundle tunningBundle = generateBundle(3, countHome, waitTime);;
                            LogEventManager.logUserBehavior(LogEventManager.USER_IN_MENU, tunningBundle, 0);
                            currentFragment = TuningFragment.getInstance();
                        }
                        else {
                            WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                            Display display = manager.getDefaultDisplay();
                            DisplayMetrics metrics = new DisplayMetrics();
                            display.getMetrics(metrics);
                            ImageView logInButton = new ImageView(getApplication());
                            logInButton.setImageResource(R.drawable.login_btn);
                            ImageView watchAdButton = new ImageView(getApplication());
                            watchAdButton.setImageResource(R.drawable.watch_ad_but);
                            modelBottomSheet = new ModelBottomSheet(getString(R.string.limited_access), getString(R.string.limited_access_content), logInButton, watchAdButton, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    v.startAnimation(animation);
                                    Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                                    intent.putExtra("bundle", bd);
                                    finish();
                                    startActivity(intent);
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    v.startAnimation(animation);
                                    if (mRewardedAd!=null){
                                        mRewardedAd.show(MainActivity.this, new OnUserEarnedRewardListener() {
                                            @Override
                                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                                loadFragment(TuningFragment.getInstance(),2);
                                                loadRewardedAd();
                                                waitTime = System.currentTimeMillis() - startTime;
                                                Bundle tunningBundle = generateBundle(3, countHome, waitTime);;
                                                LogEventManager.logUserBehavior(LogEventManager.USER_IN_MENU, tunningBundle, 0);
                                                currentFragment = TuningFragment.getInstance();
                                                modelBottomSheet.dismiss();
                                            }
                                        });
                                    }
                                }
                            });
                            modelBottomSheet.show(getSupportFragmentManager(), modelBottomSheet.getTag());
                        }
                    }
                    else if (!(currentFragment instanceof TuningFragment)) {
                        clickForInterstitialAds();
                        loadFragment(TuningFragment.getInstance(),2);
                        waitTime = System.currentTimeMillis() - startTime;
                        Bundle tunningBundle = generateBundle(3, countHome, waitTime);;
                        LogEventManager.logUserBehavior(LogEventManager.USER_IN_MENU, tunningBundle, 0);
                        currentFragment = TuningFragment.getInstance();
                    }
                }else{
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},0);
                }
            }
        });
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setBeat(String beat) {
        this.beat = beat;
    }

    public ImageView getHomeBut() {
        return homeBut;
    }

    public String getData() {
        return data;
    }

    public String getBeat() {
        return beat;
    }

    public NativeAd getmNativeAd() {
        return mNativeAd;
    }

    private Bundle generateBundle(int page, int count, long time)
    {
        Bundle bundle = new Bundle();
        bundle.putInt("page", page);
        bundle.putInt("count", count);
        bundle.putLong("time", time);
        return bundle;
    }
    public void loadFragment(Fragment fragment,int type){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (type==2){
            transaction.setCustomAnimations(R.anim.slide_up,R.anim.fade_out,R.anim.fade_in,R.anim.slide_down);
            bottomBar.setVisibility(View.GONE);
        }else {
            if(fragment instanceof HomeFragment)
            {
                transaction.setCustomAnimations(R.anim.slide_right,R.anim.fade_out,R.anim.fade_in,R.anim.slide_left);
            }
            else {
                transaction.setCustomAnimations(R.anim.slide_left,R.anim.fade_out,R.anim.fade_in,R.anim.slide_right);
            }
            bottomBar.setVisibility(View.VISIBLE);
        }
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(fragment.getId()+"");
        transaction.commit();
    }

    private void reset(){
        homeBut.setImageResource(R.drawable.home);
        searchBut.setImageResource(R.drawable.search);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }
    public void showBottomDialog(String title, String infor) {
        WindowManager manager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        ModelBottomSheet bottomSheet = new ModelBottomSheet(title, infor, mNativeAd);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
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
    @Override
    protected void onRestart() {
        super.onRestart();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("videosId",ids);
        outState.putString("videosTitle",titles);
        outState.putString("data",data);
        outState.putString("beat",beat);
        outState.putString("currentId",videoId);
        outState.putString("currentTitle",videoTitle);
    }
    public void setCurrentFragment()
    {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if(backStackEntryCount > 0)
        {
            FragmentManager.BackStackEntry topEntry = getSupportFragmentManager().getBackStackEntryAt(backStackEntryCount - 1);
            Fragment topFragment = getSupportFragmentManager().findFragmentById(topEntry.getId());
            currentFragment = topFragment;
        }
    }
    /** Called when returning to the activity */
    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }
    /** Called when leaving the activity */
    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();

    }
    /** Called before the activity is destroyed */
    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==0 && grantResults.length!=0){
            if (grantResults[0]==-1){
                WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);
                ImageView settingButton = new ImageView(getApplication());
                settingButton.setImageResource(R.drawable.setting_but);
                modelBottomSheet = new ModelBottomSheet(getString(R.string.permission_access), getString(R.string.permission_access_content),settingButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(animation);
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        modelBottomSheet.dismiss();
                    }
                });
                modelBottomSheet.show(getSupportFragmentManager(), modelBottomSheet.getTag());
            } else {
                if(!DataLocalManager.getIsLogin())
                {
                    if(DataLocalManager.getIsAvail() && !(currentFragment instanceof TuningFragment))
                    {
                        clickForInterstitialAds();
                        loadFragment(TuningFragment.getInstance(),2);
                        DataLocalManager.setIsAvailable(false);
                        waitTime = System.currentTimeMillis() - startTime;
                        Bundle tunningBundle = generateBundle(3, countHome, waitTime);;
                        LogEventManager.logUserBehavior(LogEventManager.USER_IN_MENU, tunningBundle, 0);
                        currentFragment = TuningFragment.getInstance();
                    }
                    else {
                        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                        Display display = manager.getDefaultDisplay();
                        DisplayMetrics metrics = new DisplayMetrics();
                        display.getMetrics(metrics);
                        ImageView logInButton = new ImageView(getApplication());
                        logInButton.setImageResource(R.drawable.login_btn);
                        ImageView watchAdButton = new ImageView(getApplication());
                        watchAdButton.setImageResource(R.drawable.watch_ad_but);
                        modelBottomSheet = new ModelBottomSheet(getString(R.string.limited_access), getString(R.string.limited_access_content), logInButton, watchAdButton, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.startAnimation(animation);
                                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                                intent.putExtra("bundle", bd);
                                finish();
                                startActivity(intent);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mRewardedAd!=null){
                                    mRewardedAd.show(MainActivity.this, new OnUserEarnedRewardListener() {
                                        @Override
                                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                            loadFragment(TuningFragment.getInstance(),2);
                                            loadRewardedAd();
                                            waitTime = System.currentTimeMillis() - startTime;
                                            Bundle tunningBundle = generateBundle(3, countHome, waitTime);;
                                            LogEventManager.logUserBehavior(LogEventManager.USER_IN_MENU, tunningBundle, 0);
                                            currentFragment = TuningFragment.getInstance();
                                            modelBottomSheet.dismiss();
                                        }
                                    });
                                }
                            }
                        });
                        modelBottomSheet.show(getSupportFragmentManager(), modelBottomSheet.getTag());
                    }
                }
                else if (!(currentFragment instanceof TuningFragment)) {
                    clickForInterstitialAds();
                    loadFragment(TuningFragment.getInstance(), 2);
                    waitTime = System.currentTimeMillis() - startTime;
                    Bundle tunningBundle = generateBundle(3, countHome, waitTime);
                    ;
                    LogEventManager.logUserBehavior(LogEventManager.USER_IN_MENU, tunningBundle, 0);
                    currentFragment = TuningFragment.getInstance();
                }
                }
            }
        }
}