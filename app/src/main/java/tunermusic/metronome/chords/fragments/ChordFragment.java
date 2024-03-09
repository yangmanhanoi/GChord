package tunermusic.metronome.chords.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.LinearGradient;
import android.graphics.PointF;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.dqt.libs.chorddroid.helper.ChordHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.takusemba.multisnaprecyclerview.MultiSnapHelper;
import com.takusemba.multisnaprecyclerview.SnapGravity;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import adapter.AdapterChord;
import adapter.AdapterChordImage;
import api.ApiService;
import cn.pedant.SweetAlert.SweetAlertDialog;
import model.BeatInfor;
import model.ChordRetrofitClient;
import model.DataLocalManager;
import model.LogEventManager;
import model.Pair;
import model.SongInfor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tunermusic.metronome.chords.CustomLinearLayoutManager;
import tunermusic.metronome.chords.DiaologBackWithSave;
import tunermusic.metronome.chords.LogInActivity;
import tunermusic.metronome.chords.MainActivity;
import tunermusic.metronome.chords.ModelBottomSheet;
import tunermusic.metronome.chords.NetworkListener;
import tunermusic.metronome.chords.R;

public class ChordFragment extends Fragment implements NetworkListener.netWorkChangeListener, View.OnClickListener{
    private List<Pair> listChord = new ArrayList<>();
    private List<Pair> sendList = new ArrayList<>();
    private List<Pair> imgList = new ArrayList<>();
    private List<Pair> originalSendList, originalImgList;
    private Handler handler = new Handler();
    private int BEAT_TIME = 670; // Thời gian trễ (1 giây)
    private String songUrl;
    private String folder = "f153376704924c52";
    private static String beatData = "";
    private String filename = "Hoàng_Dũng_x_GDucky_-_Đôi_Mươi__Remix__-_Official_MV.mp3";
    private static String data = "";
    private Context context;
    private boolean isFirstVisit = true;
//    private NativeAd mNativeAd;
//    private NativeAdView adView;
    private int currentSquare = 0, currentImage = 0, chosenCapo = 0; // Khởi tạo vị trí bắt đầu
    LinearLayoutManager layoutManager;
    GridLayoutManager layoutImageManager;
    AdapterChord adapterChord;
    AdapterChordImage adapterChordImage;
    ImageView play, prev, plusCapo, minusCapo, btnBack, btnHelp;
    YouTubePlayerView youTubePlayerView;
    YouTubePlayer player;
    YouTubePlayerTracker youTubePlayerTracker = new YouTubePlayerTracker();
    ImageView loop, volume, timer;
    TextView timerText, capoText, songName;
    boolean isLoop = false, isVolume = true, isTimer = false, isStart = true, dataLoadComplete = false, lostConnection = false, isSwipe = true, isBannerLoad = true;
    String videoId;
    ExtendedFloatingActionButton extendedFAB;
    RelativeLayout noInternet;
    NetworkListener networkListener = new NetworkListener();
    RecyclerView rcl_chord, rcl_node;
    String title;
    File jsonDirectory;
    MainActivity mainActivity;
    SweetAlertDialog sweetAlertDialog;
    private long startTime;
    private static int replayCount;
    private Boolean isFromTrending = false;
    private int count = 0;
    private long mLastClickTime = 0;
    Bundle bundle;
    File jsonFile;
    ModelBottomSheet modelBottomSheet;
    private boolean initialLayoutComplete=false;

    private static ChordFragment instance;
    public static ChordFragment getInstance()
    {
        if(instance == null) instance = new ChordFragment();
        return  instance;
    }
    public ChordFragment()
    {

    }

    Animation animation;
    View spacing;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chord, container, false);
        mainActivity = (MainActivity) getActivity();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
        context = getContext();
        bundle = getArguments();
        noInternet = getView().findViewById(R.id.noInternetLayout);
        isFromTrending = bundle.getBoolean("IsFromTrending");
        videoId = bundle.getString("videoId");
        jsonFile = new File(getActivity().getExternalFilesDir(null) + "/ChordifyClone/" + videoId + ".json");
        songUrl = "https://youtu.be/" + videoId + "?si=AqzE0H2dqJNnk00b";

        spacing = getView().findViewById(R.id.spacing);

        capoText = getView().findViewById(R.id.capo);
        minusCapo = getView().findViewById(R.id.minus_capo);
        plusCapo = getView().findViewById(R.id.plus_capo);
        btnBack = getView().findViewById(R.id.btn_back);
        btnHelp = getView().findViewById(R.id.btn_help);
        count = 1;
        songName = getView().findViewById(R.id.songName);
        rcl_chord = getView().findViewById(R.id.displayChord);
        rcl_chord.setNestedScrollingEnabled(false);
        rcl_node = getView().findViewById(R.id.rcl_chord);
        rcl_node.setNestedScrollingEnabled(false);
        extendedFAB = getView().findViewById(R.id.extended_fab);
        play = getView().findViewById(R.id.playButton);
        prev = getView().findViewById(R.id.prevButton);
        loop = getView().findViewById(R.id.loopButton);
        volume = getView().findViewById(R.id.volumeButton);
        timer = getView().findViewById(R.id.timerButton);
        timerText = getView().findViewById(R.id.timerYoutube);
        startTime = System.currentTimeMillis();
        isFromTrending = getArguments().getBoolean("IsFromTrending");
        replayCount = 0;
        //

        youTubePlayerView = getView().findViewById(R.id.youtubeView);
        getLifecycle().addObserver(youTubePlayerView);
        YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                player = youTubePlayer;
                youTubePlayer.loadVideo(videoId, 0);
                player.addListener(youTubePlayerTracker);
                youTubePlayerTracker.onStateChange(player, PlayerConstants.PlayerState.PAUSED);
                play.setImageResource(R.drawable.play_but);
                youTubePlayer.cueVideo(videoId, 0);
            }

        };

        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).rel(0).ivLoadPolicy(3).ccLoadPolicy(1).build();
        youTubePlayerView.setEnableAutomaticInitialization(false);
        youTubePlayerView.initialize(listener, options);

        minusCapo.setOnClickListener(this);
        plusCapo.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        volume.setOnClickListener(this);
        timer.setOnClickListener(this);
        play.setOnClickListener(this);
        prev.setOnClickListener(this);
        loop.setOnClickListener(this);
        extendedFAB.setOnClickListener(this);
        networkListener.setNetWorkChangeListener(this);
        btnBack.setOnClickListener(this);

        // Load native ads
        List<String> testDeviceIds = Arrays.asList(AdRequest.DEVICE_ID_EMULATOR);
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        title = bundle.getString("videoTitle");
        songName.setText(title);

        jsonDirectory = new File(context.getExternalFilesDir(null) + "/ChordifyClone/");

        double angleInRadians = Math.toRadians(0);
        double length = extendedFAB.getPaint().getTextSize();
        double endX = Math.sin(angleInRadians) * length;
        double endY = Math.cos(angleInRadians) * length;
        Shader shader = new LinearGradient((float) 0, (float) 0, (float) endX, (float) endY,
                getResources().getColor(R.color.gradient_1), getResources().getColor(R.color.gradient_2),Shader.TileMode.CLAMP);
        extendedFAB.getPaint().setShader(shader);
        extendedFAB.hide();
        initialState();
    }


    public void setIsFromTrending(Boolean f)
    {
        isFromTrending = f;
    }

    private Runnable colorChangeRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentSquare < sendList.size()) {
                if ((currentSquare % 4 == 0) && isSwipe == true) {
                    rcl_chord.smoothScrollToPosition(currentSquare + 6);
                }
                if (sendList.get(currentSquare).getKey() != "") {
                    if (isSwipe == true) rcl_node.smoothScrollToPosition(currentImage + 1);
                    adapterChordImage.changeSelected(sendList.get(currentSquare).getImagePos());
                    adapterChordImage.notifyItemChanged(sendList.get(currentSquare).getImagePos());
                    currentImage++;
                    rcl_node.setRecycledViewPool(new RecyclerView.RecycledViewPool());
                    rcl_node.setNestedScrollingEnabled(false);
                }
                adapterChord.changeSelected(currentSquare);
                adapterChord.notifyItemChanged(currentSquare);
                rcl_chord.setRecycledViewPool(new RecyclerView.RecycledViewPool());
                rcl_chord.setNestedScrollingEnabled(false);
                currentSquare++;
                if (!lostConnection) handler.postDelayed(colorChangeRunnable, BEAT_TIME);
            }
        }
    };
    private void appendList() {
        try{
            if(data.length() >= 2)
            {
                data = data.substring(0, data.length() - 2);
                listChord.clear();
                if (data != null && data != "") {
                    StringTokenizer tokenizer = new StringTokenizer(data, ";");
                    while (tokenizer.hasMoreTokens()) {
                        String temp = tokenizer.nextToken();
                        StringTokenizer stringTokenizer = new StringTokenizer(temp, ",");
                        int i = 0;
                        Double time = (double) 0;
                        String note = null;
                        while (stringTokenizer.hasMoreTokens()) {
                            if (i == 0) {
                                time = Double.parseDouble(stringTokenizer.nextToken());
                            } else {
                                note = stringTokenizer.nextToken();
                                if (note.contains("\"")) note = note.replace("\\" + "\"", "");
                            }
                            i++;
                        }
                        listChord.add(new Pair(note, time));
                    }
                    if(listChord.size() > 1)
                    {
                        listChord.remove(listChord.get(listChord.size() - 1));
                        convertNote();
                    }

                }
            }
        }catch (Exception ex)
        {
            showAlertDialog();
        }


    }
    private void convertNote()
    {
        for(Pair p: listChord)
        {
            String name = p.getKey();
            name = ignoreBassNote(name);
            name = ChordHelper.simplifyName(name);
            if(name.contains("aug") && name.charAt(1) == 'b') name = name.charAt(0) + name.substring(2);
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            name = name.replace("\"", "");
            p.setKey(name);
        }
    }
    private static String ignoreBassNote(String name) {
        return name.replaceAll("\\/.*$", "");
    }
    private void initialState()
    {
        if (!jsonFile.exists()) {
            if (!mainActivity.videoId.equals(videoId)){
                mainActivity.videoId=videoId;
                mainActivity.videoTitle=title;
                mainActivity.beat="";
                mainActivity.data="";
                new onDownloadData().execute();
                sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.setTitle("Loading Chord");
                sweetAlertDialog.setContentText("Please wait for a minute, your chord is loading....");
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.hideConfirmButton();
                sweetAlertDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.secondary_purple));
                sweetAlertDialog.show();
            } else if (mainActivity.data.length()!=0 && mainActivity.beat.length()!=0){
                data=((MainActivity)getActivity()).data;
                beatData=((MainActivity)getActivity()).beat;
                executeBeatData();
            }
        } else {
            if (jsonDirectory.exists()) {
                try {
                    JSONObject jsonObject = new JSONObject(readJsonFile(jsonFile));
                    data = jsonObject.getString("data");
                    beatData = jsonObject.getString("beat");
                    appendList();
                    executeBeatData();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }
    private void showAlertDialog()
    {
        new AestheticDialog.Builder(mainActivity, DialogStyle.FLAT, DialogType.ERROR)
                .setTitle("Error")
                .setMessage("Something wrong with the chords. Please visit later")
                .setGravity(Gravity.CENTER)
                .setAnimation(DialogAnimation.SHRINK)
                .setDarkMode(false)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(@NonNull AestheticDialog.Builder builder) {
                        builder.dismiss();
                        getActivity().getSupportFragmentManager().popBackStack();
                        getActivity().findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
                    }
                })
                .show();
    }
    private void execute()
    {
        new onDownloadData().execute();
        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitle("Loading Chord");
        sweetAlertDialog.setContentText("Please wait for a minute, your chord is loading....");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.hideConfirmButton();
        sweetAlertDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.secondary_purple));
        sweetAlertDialog.show();
    }
    public String readJsonFile(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
    private void getBeatTime() {
        if (beatData != null) {
            sendList.clear();
            StringTokenizer tokenizer = new StringTokenizer(beatData, ";");
            while (tokenizer.hasMoreTokens()) {
                Double x = Double.parseDouble(tokenizer.nextToken());
                sendList.add(new Pair("", x));
            }
            BEAT_TIME = (int) (1000 * (sendList.get(sendList.size() - 1).getValue() - sendList.get(1).getValue()) / (sendList.size() - 1));
        }
    }
    private void init()
    {
        // adapter chord
        layoutManager = new CustomLinearLayoutManager(context, CustomLinearLayoutManager.HORIZONTAL, false);
        rcl_node.setVisibility(View.VISIBLE);
        layoutImageManager = new GridLayoutManager(context,1, LinearLayoutManager.HORIZONTAL, false);
        adapterChord = new AdapterChord(context, R.layout.custom_chord_beat, sendList, new AdapterChord.onChordSelected() {
            @Override
            public void iChordClicked(int pos) {
                currentSquare = pos;
                int tmp = pos;
                while (tmp >= 0 && sendList.get(tmp).getKey() == "") tmp--;
                if (tmp < 0) tmp = 0;
                int displayImagePos = sendList.get(tmp).getImagePos();
                currentImage = (sendList.get(pos).getKey() == "") ? sendList.get(tmp).getImagePos() + 1 : sendList.get(pos).getImagePos();
                if(player != null)
                {
                    player.seekTo((float) sendList.get(pos).getValue());
                    rcl_node.smoothScrollToPosition(displayImagePos);
                    if (youTubePlayerTracker.getState() == PlayerConstants.PlayerState.PAUSED || youTubePlayerTracker.getState() == PlayerConstants.PlayerState.UNSTARTED) {
                        youTubePlayerTracker.onStateChange(player, PlayerConstants.PlayerState.PLAYING);
                    }
                    play.setImageResource(R.drawable.pause_but);
                    resetScroll();
                    handler.postDelayed(colorChangeRunnable, BEAT_TIME);
                }
            }

            @Override
            public void iContinousClicked() {
                if(youTubePlayerTracker.getState() == PlayerConstants.PlayerState.PLAYING) {
                    youTubePlayerTracker.onStateChange(player, PlayerConstants.PlayerState.PAUSED);
                    youTubePlayerTracker.onStateChange(player, PlayerConstants.PlayerState.PAUSED);
                    play.setImageResource(R.drawable.play_but);
                    if (!lostConnection) player.pause();
                    handler.removeCallbacks(colorChangeRunnable);
                }
            }
        });
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rcl_chord);
        rcl_chord.setOnFlingListener(null);
        rcl_chord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if(!isStart)    stopScroll();
                        break;
                }
                return false;
            }
        });
        rcl_chord.setLayoutManager(layoutManager);
        rcl_chord.setAdapter(adapterChord);
        adapterChord.notifyDataSetChanged();

        // adapter image
        adapterChordImage = new AdapterChordImage(context, R.layout.custom_img_chord, imgList, new AdapterChordImage.onImageChosen() {
            @Override
            public void iImageClicked(int pos) {
                if(player != null)
                {
                    currentImage = pos;
                    currentSquare = imgList.get(pos).getImagePos();
                    player.seekTo((float) imgList.get(pos).getValue());
                    rcl_chord.smoothScrollToPosition(currentSquare);
                    if (youTubePlayerTracker.getState() == PlayerConstants.PlayerState.PAUSED || youTubePlayerTracker.getState() == PlayerConstants.PlayerState.UNSTARTED) {
                        youTubePlayerTracker.onStateChange(player, PlayerConstants.PlayerState.PLAYING);
                    }
                    play.setImageResource(R.drawable.pause_but);
                    resetScroll();
                    handler.postDelayed(colorChangeRunnable, BEAT_TIME);
                }

            }
            @Override
            public void iContinousClicked() {
                if(youTubePlayerTracker.getState() == PlayerConstants.PlayerState.PLAYING) {
                    youTubePlayerTracker.onStateChange(player, PlayerConstants.PlayerState.PAUSED);
                    youTubePlayerTracker.onStateChange(player, PlayerConstants.PlayerState.PAUSED);
                    play.setImageResource(R.drawable.play_but);
                    if (!lostConnection) player.pause();
                    handler.removeCallbacks(colorChangeRunnable);
                }
            }
        });
        rcl_node.setLayoutManager(layoutImageManager);
        MultiSnapHelper helper = new MultiSnapHelper(SnapGravity.START, 1, 0);
        rcl_node.setOnFlingListener(null);
        helper.attachToRecyclerView(rcl_node);
        rcl_node.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if(!isStart) stopScroll();
                        break;
                }
                return false;
            }
        });
        rcl_node.setAdapter(adapterChordImage);
        rcl_node.setLayoutManager(layoutImageManager);
        adapterChordImage.notifyDataSetChanged();
    }
    private void executeBeatData()
    {
        getBeatTime();
        // already haven BEAT_TIME
        int countSquare = (int) Math.round(1000*listChord.get(listChord.size() - 1).getValue() / BEAT_TIME);
        sendList = new ArrayList<Pair>(countSquare);
        for(int i = 0; i < countSquare; i++) sendList.add(new Pair("", 0));
        imgList.clear();
        for (int i = 1; i < listChord.size(); i++) {
            int posi = (int) Math.round(1000* listChord.get(i).getValue() / BEAT_TIME);
            if(posi < sendList.size() && posi >= 0)
            {
                sendList.get(posi).setKey(listChord.get(i).getKey());
                sendList.get(posi).setValue(listChord.get(i).getValue());
                Pair in4 = new Pair(sendList.get(posi).getKey(), listChord.get(i).getValue());
                in4.setImagePos(posi);
                imgList.add(in4);
                sendList.get(posi).setImagePos(imgList.size() - 1);
            }
        }
        originalSendList = new ArrayList<>(sendList);
        originalImgList = new ArrayList<>(imgList);
        init();
        dataLoadComplete = true;
    }
    private void requestBeat() {
        ApiService apiService = ChordRetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<BeatInfor> call = apiService.getBeatInfor(folder, filename);

        call.enqueue(new Callback<BeatInfor>() {
            @Override
            public void onResponse(Call<BeatInfor> call, Response<BeatInfor> response) {
                beatData = response.body().getData();
                mainActivity.data = data;
                mainActivity.beat = beatData;
                executeBeatData();
                sweetAlertDialog.dismissWithAnimation();
            }

            @Override
            public void onFailure(Call<BeatInfor> call, Throwable t) {
            }
        });
    }
    class onDownloadBeat extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            requestBeat();
            return true;
        }
    }
    private void requestChord() {
        ApiService apiService = ChordRetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<SongInfor> call = apiService.getSongInfor(folder, songUrl);
        sendList = new ArrayList<>();
        imgList = new ArrayList<>();
        call.enqueue(new Callback<SongInfor>() {
            @Override
            public void onResponse(@NonNull Call<SongInfor> call, @NonNull Response<SongInfor> response) {
                if(response.body() != null)
                {
                    filename = response.body().getFilename();
                    data = response.body().getData();
                    if(data != ""){
                        appendList();
                        new onDownloadBeat().execute();
                    }

                }
                else
                {
                    showAlertDialog();
                }
            }

            @Override
            public void onFailure(Call<SongInfor> call, Throwable t) {
                sweetAlertDialog.dismissWithAnimation();
                showAlertDialog();
            }
        });
    }
    class onDownloadData extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            requestChord();
            return true;
        }
    }
    private void resetScroll() {
        isSwipe = true;
        if (extendedFAB.isShown()) {
            extendedFAB.hide();
            extendedFAB.shrink();
        }
    }
    private void stopScroll() {
        isSwipe = false;
        if (!extendedFAB.isShown()) {
            extendedFAB.show();
            extendedFAB.extend();
        }
    }
    public void playVideo() {
        if (isStart) {
            if (isTimer) {
                timerText.setVisibility(View.VISIBLE);
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long l) {
                        timerText.setText((l + 1000) / 1000 + "");
                    }

                    @Override
                    public void onFinish() {
                        if (!lostConnection) player.play();
                        timerText.setVisibility(View.GONE);
                    }
                }.start();
            }
        } else {
            if (!lostConnection) player.play();
        }
    }
    @Override
    public void onClick(View view) {
        if(SystemClock.elapsedRealtime() - mLastClickTime <1000)
        {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        int id = view.getId();
        view.startAnimation(animation);
        switch (id)
        {
            case R.id.btn_back:
                if (!lostConnection) {
                    DiaologBackWithSave diaologBackWithSave = new DiaologBackWithSave(null ,new DiaologBackWithSave.ListenDialog() {
                        @Override
                        public void onClose() {
                            getActivity().getSupportFragmentManager().popBackStack();
                            getActivity().findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onSave() {
                            if (jsonDirectory.exists()) {
                                if (!jsonFile.exists()){
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("id", videoId);
                                        jsonObject.put("title", title);
                                        jsonObject.put("data", data);
                                        jsonObject.put("beat", beatData);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    try {
                                        FileWriter fileWriter = new FileWriter(jsonFile);
                                        if(jsonObject != null)
                                        {
                                            fileWriter.write(jsonObject.toString());
                                            fileWriter.flush();
                                            fileWriter.close();
                                        }

                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                            getActivity().getSupportFragmentManager().popBackStack();
                            getActivity().findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancel() {
                        }
                    });

                    if (!jsonFile.exists()){
                        diaologBackWithSave.show(getActivity().getSupportFragmentManager(), null);
                    }
                    else{
                        getActivity().getSupportFragmentManager().popBackStack();
                        getActivity().findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
                    }

                } else
                {
                    getActivity().getSupportFragmentManager().popBackStack();
                    getActivity().findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
                }
                mainActivity.setCurrentFragment();
                break;
            case R.id.extended_fab:
                resetScroll();
                rcl_chord.smoothScrollToPosition(currentSquare);
                rcl_node.smoothScrollToPosition(currentImage);
                break;
            case R.id.timerButton:
                isTimer = !isTimer;
                if (isTimer) {
                    player.pause();
                }
                break;
            case R.id.volumeButton:
                isVolume = !isVolume;
                if (isVolume) {
                    volume.setImageResource(R.drawable.volume_but);
                    player.unMute();
                } else {
                    volume.setImageResource(R.drawable.mute_but);
                    player.mute();
                }
                break;
            case R.id.loopButton:
                isLoop = !isLoop;
                player.setLoop(isLoop);
                if (isLoop){
                    ((ImageView)view).setColorFilter(ContextCompat.getColor(context, R.color.gradient_2), android.graphics.PorterDuff.Mode.MULTIPLY);
                } else {
                    ((ImageView)view).setColorFilter(ContextCompat.getColor(context, R.color.main_gray_color), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
                break;
            case R.id.prevButton:
                player.seekTo(0);
                playVideo();
                currentImage = 0;
                currentSquare = 0;
                rcl_chord.smoothScrollToPosition(0);
                rcl_node.smoothScrollToPosition(0);
                handler.removeCallbacks(colorChangeRunnable);
                if (!lostConnection) handler.postDelayed(colorChangeRunnable, BEAT_TIME);
                play.setImageResource(R.drawable.pause_but);
                replayCount++;
                break;
            case R.id.playButton:
                if (youTubePlayerTracker.getState() == PlayerConstants.PlayerState.PLAYING) {
                    youTubePlayerTracker.onStateChange(player, PlayerConstants.PlayerState.PAUSED);
                    play.setImageResource(R.drawable.play_but);
                    if (!lostConnection) player.pause();
                    handler.removeCallbacks(colorChangeRunnable);
                } else {
                    youTubePlayerTracker.onStateChange(player, PlayerConstants.PlayerState.PLAYING);
                    if (youTubePlayerTracker.getState() == PlayerConstants.PlayerState.PLAYING) {
                        // isStart: click play button for the first time
                        play.setImageResource(R.drawable.pause_but);
                        if (isStart) {
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (!lostConnection) {
                                        rcl_chord.smoothScrollToPosition(0);
                                        rcl_node.smoothScrollToPosition(0);
                                        handler.postDelayed(colorChangeRunnable, BEAT_TIME);
                                    }
                                }
                            };
                            Handler h = new Handler();
                            h.postDelayed(runnable, 1000);
                            isStart = false;
                        } else {
                            if (!lostConnection)
                                handler.postDelayed(colorChangeRunnable, BEAT_TIME);
                        }
                        playVideo();
                    }

                }
                break;
            case R.id.minus_capo:
                if (DataLocalManager.getIsLogin()){
                    if(chosenCapo > 0)
                    {
                        chosenCapo = Integer.parseInt(capoText.getText().toString()) - 1;
                        capoText.setText(chosenCapo + "");
                        resetAdapter((chosenCapo + 1));
                        resetAdapter(-chosenCapo);
                    }
                } else {
                    ImageView logInButton = new ImageView(mainActivity.getApplication());
                    logInButton.setImageResource(R.drawable.login_btn);
                    ImageView watchAdButton = new ImageView(mainActivity.getApplication());
                    watchAdButton.setImageResource(R.drawable.watch_ad_but);
                     modelBottomSheet = new ModelBottomSheet(getString(R.string.limited_access), getString(R.string.limited_access_content), logInButton, watchAdButton, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.startAnimation(animation);
                            Intent intent = new Intent(getActivity(), LogInActivity.class);
                            intent.putExtra("bundle", mainActivity.bd);
                            mainActivity.finish();
                            startActivity(intent);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mainActivity.mRewardedAd!=null){
                                mainActivity.mRewardedAd.show(mainActivity, new OnUserEarnedRewardListener() {
                                    @Override
                                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                        modelBottomSheet.dismiss();
                                        mainActivity.mRewardedAd=null;
                                        AdRequest adRequest = new AdRequest.Builder().build();
                                        RewardedAd.load(getActivity(), mainActivity.adsRewardedId, adRequest, new RewardedAdLoadCallback() {
                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                super.onAdFailedToLoad(loadAdError);
                                                mainActivity.mRewardedAd=null;
                                            }

                                            @Override
                                            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                                                super.onAdLoaded(rewardedAd);
                                                mainActivity.mRewardedAd = rewardedAd;
                                            }
                                        });
                                        if(chosenCapo > 0)
                                        {
                                            chosenCapo = Integer.parseInt(capoText.getText().toString()) - 1;
                                            capoText.setText(chosenCapo + "");
                                            resetAdapter((chosenCapo + 1));
                                            resetAdapter(-chosenCapo);
                                        }
                                    }
                                });
                            }
                        }
                    });
                    modelBottomSheet.show(getActivity().getSupportFragmentManager(), modelBottomSheet.getTag());
                }
                break;
            case R.id.plus_capo:
                if (DataLocalManager.getIsLogin()){
                    if(chosenCapo < 6)
                    {
                        chosenCapo = Integer.parseInt(capoText.getText().toString()) + 1;
                        capoText.setText(chosenCapo + "");
                        resetAdapter((chosenCapo - 1));
                        resetAdapter(-chosenCapo);
                    }
                }else {
                    ImageView logInButton = new ImageView(mainActivity.getApplication());
                    logInButton.setImageResource(R.drawable.login_btn);
                    ImageView watchAdButton = new ImageView(mainActivity.getApplication());
                    watchAdButton.setImageResource(R.drawable.watch_ad_but);
                     modelBottomSheet = new ModelBottomSheet(getString(R.string.limited_access), getString(R.string.limited_access_content), logInButton, watchAdButton, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.startAnimation(animation);
                            Intent intent = new Intent(getActivity(), LogInActivity.class);
                            intent.putExtra("bundle", mainActivity.bd);
                            mainActivity.finish();
                            startActivity(intent);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mainActivity.mRewardedAd!=null){
                                mainActivity.mRewardedAd.show(mainActivity, new OnUserEarnedRewardListener() {
                                    @Override
                                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                        modelBottomSheet.dismiss();
                                        mainActivity.mRewardedAd=null;
                                        AdRequest adRequest = new AdRequest.Builder().build();
                                        RewardedAd.load(getActivity(), mainActivity.adsRewardedId, adRequest, new RewardedAdLoadCallback() {
                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                super.onAdFailedToLoad(loadAdError);
                                                mainActivity.mRewardedAd=null;
                                            }

                                            @Override
                                            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                                                super.onAdLoaded(rewardedAd);
                                                mainActivity.mRewardedAd = rewardedAd;
                                            }
                                        });
                                        if(chosenCapo < 6)
                                        {
                                            chosenCapo = Integer.parseInt(capoText.getText().toString()) + 1;
                                            capoText.setText(chosenCapo + "");
                                            resetAdapter((chosenCapo - 1));
                                            resetAdapter(-chosenCapo);
                                        }
                                    }
                                });
                            }
                        }
                    });
                    modelBottomSheet.show(getActivity().getSupportFragmentManager(), modelBottomSheet.getTag());
                }
                break;
            case R.id.btn_help:
                ((MainActivity) getActivity()).showBottomDialog(getResources().getString(R.string.what_can_do), getResources().getString(R.string.what_do));
                break;
        }
    }
    private void resetAdapter(int capo){
        fillData2CapoList(capo);
    }
    private void fillData2CapoList(int capo) {
        List<Pair> capoList, capoImageList;
        capoList = new ArrayList<>(sendList);
        capoImageList = new ArrayList<>(imgList);
        int i = 0;
        for (Pair p : capoList) {
            if (p.getKey() != "") {
                String chord = p.getKey();
                chord = ChordHelper.transpose(chord, capo);
                p.setKey(chord);
                capoImageList.get(i).setKey(chord);
                capoImageList.get(i).setValue(p.getValue());
                capoImageList.get(i).setImagePos(capoImageList.indexOf(p));
                i++;
            }
        }
        adapterChord.setSendList(capoList);
        adapterChord.notifyDataSetChanged();
        adapterChordImage.setImgName(capoImageList);
        adapterChordImage.notifyDataSetChanged();
    }
    @Override
    public void onNetWorkChange(boolean isConnected) {
        if (!isConnected) {
            if (sweetAlertDialog != null && sweetAlertDialog.isShowing()) sweetAlertDialog.dismissWithAnimation();
            if(player != null){
                player.pause();
            }
            handler.removeCallbacks(colorChangeRunnable);
            if (!dataLoadComplete) noInternet.setVisibility(View.VISIBLE);
            lostConnection = true;
        } else {
            noInternet.setVisibility(View.GONE);
            if (!dataLoadComplete && lostConnection) {
                getActivity().finish();
            }
            lostConnection = false;
        }
    }

    @Override
    public void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkListener, intentFilter);
        super.onStart();
    }
    @Override
    public void onStop() {
        mainActivity.currenVideoId = videoId;
        handler.removeCallbacks(colorChangeRunnable);
        play.setImageResource(R.drawable.play_but);
        if(networkListener != null) getActivity().unregisterReceiver(networkListener);
        rcl_chord.setOnFlingListener(null);
        currentImage = 0;
        currentSquare = 0;
        isFirstVisit = false;
        rcl_chord.smoothScrollToPosition(currentSquare);
        rcl_node.smoothScrollToPosition(currentImage);
        super.onStop();
    }
    @Override
    public void onDestroy() {
        handler.removeCallbacks(colorChangeRunnable);
        Bundle bundle = new Bundle();
        bundle.putInt("time", (int) (System.currentTimeMillis() - startTime));
        bundle.putInt("replay", replayCount);
        youTubePlayerView.release();
        LogEventManager.logUserBehavior(LogEventManager.USER_IN_FRAG, bundle, R.id.chordBut);
        super.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        if(Boolean.compare(getArguments().getBoolean("IsFromTrending"), true) == 0)
        {
            initialState();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
