package tunermusic.metronome.chords.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleableRes;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import tunermusic.metronome.chords.R;

import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.StringTokenizer;

import adapter.AdapterRecyclerYoutube;
import api.ApiService;
import model.LogEventManager;
import model.VideoRetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tunermusic.metronome.chords.MainActivity;
import tunermusic.metronome.chords.ModelBottomSheet;
import tunermusic.metronome.chords.NetworkListener;

public class SearchFragment extends Fragment implements NetworkListener.netWorkChangeListener, LifecycleObserver {
    private static final String TITLES_LIST = "list_title";
    private static final String IDS_LIST = "list_ids";
    RecyclerView recyclerView;
    TextInputLayout textInputLayout;
    private static SearchFragment instance;
    TextInputEditText editText;
    ImageView background,propertiesButton,searchButton;
    CircularProgressIndicator indicator;
    private long startTime;
    private MainActivity mainActivity;
    RelativeLayout noInternet;
    ArrayList<String> videoIds = new ArrayList<>();
    ArrayList<String> videoTitle = new ArrayList<>();
    NetworkListener networkListener = new NetworkListener();
    AdapterRecyclerYoutube adapterRecyclerYoutube;
    ImageView btnHelp;
    View disableSearch;
    private int currentVideoPos = 0;
    private boolean isStart = true;
    NativeAdView nativeAdView;
    Animation animation;
    public static SearchFragment getInstance()
    {
        if(instance == null) instance = new SearchFragment();
        return instance;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
        background = v.findViewById(R.id.background);
        indicator = v.findViewById(R.id.progress_circular);
        textInputLayout = v.findViewById(R.id.textField);
        editText = v.findViewById(R.id.editText);
        noInternet = v.findViewById(R.id.noInternetLayout);
        btnHelp = v.findViewById(R.id.helpButtonSearch);
        recyclerView = v.findViewById(R.id.recycleViewYoutube);
        searchButton = v.findViewById(R.id.searchButton);
        propertiesButton = v.findViewById(R.id.propertiesButSearch);
        startTime = System.currentTimeMillis();
        mainActivity = (MainActivity) getActivity();
        disableSearch = v.findViewById(R.id.disableSearch);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        networkListener.setNetWorkChangeListener(this);
        if (mainActivity.shareVideoURL.length()!=0){
            textInputLayout.getEditText().setText(mainActivity.shareVideoURL);
            mainActivity.shareVideoURL="";
            searchYoutube();
        }
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_SEARCH){
                    if (textInputLayout.getEditText().getText().length()!=0){
                        videoTitle.clear();
                        videoIds.clear();
                        isStart = false;
                        searchButton.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        searchYoutube();
                    }
                }
                return false;
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    recyclerView.setVisibility(View.GONE);
                    searchButton.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    searchButton.setVisibility(View.GONE);
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InputMethodManager) getActivity().getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(textInputLayout.getWindowToken(), 0);
                textInputLayout.clearFocus();;
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                if (textInputLayout.getEditText().getText().length()!=0){
                    videoTitle.clear();
                    videoIds.clear();
                    isStart = false;
                    searchButton.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    searchYoutube();
                }

            }
        });
        propertiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                mainActivity.drawerLayout.openDrawer(Gravity.LEFT);
                textInputLayout.clearFocus();
                ((InputMethodManager) getActivity().getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(textInputLayout.getWindowToken(), 0);
            }
        });
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animation);
                ModelBottomSheet bottomSheet = new ModelBottomSheet(getResources().getString(R.string.what_can_search), getResources().getString(R.string.what_search), ((MainActivity) getActivity()).getmNativeAd());
                bottomSheet.show(getActivity().getSupportFragmentManager(), bottomSheet.getTag());
            }
        });
    }

    public void searchYoutube() {
        String s = textInputLayout.getEditText().getText().toString().trim();
        textInputLayout.getEditText().setText(s);

        if (!s.isEmpty()) {
            disableSearch.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            indicator.setVisibility(View.VISIBLE);
            background.setVisibility(View.VISIBLE);
            // Search by song url
            if (s.contains("youtu")){
                textInputLayout.setError(null);
                String str="";
                if (s.contains("youtu.be")) {
                    str=s.substring(17,28);
                }
                if (s.contains("youtube.com")){
                    if (s.contains("m.youtube.com")){
                        str=s.substring(50,61);
                    }else{
                        StringTokenizer stringTokenizer = new StringTokenizer(s,"=");
                        stringTokenizer.nextToken();
                        str=stringTokenizer.nextToken();
                        str = str.substring(0,11);
                    }
                }
                indicator.setVisibility(View.VISIBLE);
                background.setVisibility(View.VISIBLE);

                new DownloadTitleTaskURL().execute(str);
                // Search by name
            }else {
                ApiService apiService = VideoRetrofitClient.getRetrofitVideoInstance().create(ApiService.class);
                Call<ResponseBody> call = apiService.getVideoInfor(s);
                videoIds = new ArrayList<>();
                videoTitle = new ArrayList<>();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String html = response.body().string();
                            StringBuilder stringBuilder = new StringBuilder(html);
                            while (stringBuilder.indexOf("videoId")>=0) {
                                stringBuilder.delete(0, stringBuilder.indexOf("videoId") + 9);
                                StringTokenizer stringTokenizer = new StringTokenizer(stringBuilder.toString(), "\"");
                                stringTokenizer.nextToken();
                                String temp = stringTokenizer.nextToken();
                                if (temp.length() == 11 && !videoIds.contains(temp)) {
                                    videoIds.add(temp);
                                }
                            }
                            for (int i=0 ;i<videoIds.size();i++){
                                new DownloadTitleTask(i).execute(videoIds.get(i));
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
            }

        }
        ((InputMethodManager) getActivity().getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(textInputLayout.getWindowToken(), 0);
        textInputLayout.clearFocus();
    }

    @Override
    public void onNetWorkChange(boolean isConnected) {
        if (!isConnected){
            noInternet.setVisibility(View.VISIBLE);
        } else noInternet.setVisibility(View.GONE);
    }
    class DownloadTitleTask extends AsyncTask<String,Void, String>{
        int counter;

        public DownloadTitleTask(int counter) {
            this.counter = counter;
        }

        @Override
        protected  String doInBackground(String... urls) {
            URL embededURL = null;
            try {
                embededURL = new URL("https://www.youtube.com/oembed?url=youtube.com/watch?v=" + urls[0] + "&format=json");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            try {
                return new JSONObject(IOUtils.toString(embededURL, StandardCharsets.UTF_8)).getString("title");
            } catch (JSONException | IOException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            videoTitle.add(s);
            if (counter==videoIds.size()-1){
                int counter =0;
                while (counter<videoTitle.size()){
                    if (videoTitle.get(counter).length()==0){
                        videoTitle.remove(counter);
                        videoIds.remove(counter);
                    }
                    counter++;
                }
                setUpRecyclerView(videoIds, videoTitle);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    class DownloadTitleTaskURL extends AsyncTask<String,Void, String> {
        String title;

        @Override
        protected  String doInBackground(String... urls) {
            title = urls[0];
            URL embededURL = null;
            try {
                embededURL = new URL("https://www.youtube.com/oembed?url=youtube.com/watch?v=" + title + "&format=json");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            try {
                return new JSONObject(IOUtils.toString(embededURL, StandardCharsets.UTF_8)).getString("title");
            } catch (JSONException | IOException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Bundle bundle = new Bundle();
            bundle.putString("videoId", title);
            bundle.putString("videoTitle",s);
            indicator.setVisibility(View.GONE);
            background.setVisibility(View.GONE);
            mainActivity.setBeat("");
            mainActivity.setData("");
            ChordFragment.getInstance().setArguments(bundle);
            mainActivity.loadFragment(ChordFragment.getInstance(),2);
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
        getActivity().unregisterReceiver(networkListener);
        Bundle bundle = new Bundle();
        LogEventManager.logUserBehavior(LogEventManager.USER_IN_FRAG, bundle, R.id.searchBut);
        mainActivity.startTime = System.currentTimeMillis();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        setUpRecyclerView(videoIds, videoTitle);
        if(!isStart) {
            searchButton.setVisibility(View.GONE);

        }
        else {
            currentVideoPos = 0;
            recyclerView.setVisibility(View.GONE);
        }

    }
    private void setUpRecyclerView(ArrayList<String> videoIds, ArrayList<String> videoTitle)
    {
        adapterRecyclerYoutube = new AdapterRecyclerYoutube(getActivity(), videoIds, videoTitle, new AdapterRecyclerYoutube.onVideoChosen() {
            @Override
            public void iVideoClicked(Bundle params) {
                currentVideoPos = params.getInt("videoPos");
                ChordFragment.getInstance().setArguments(params);
                if (!params.getString("videoId").equals(mainActivity.videoId)){
                    mainActivity.beat="";
                    mainActivity.data="";
                }

                mainActivity.loadFragment(ChordFragment.getInstance(), 2);
            }
        });

        recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),1);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterRecyclerYoutube);

        disableSearch.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        indicator.setVisibility(View.GONE);
        background.setVisibility(View.GONE);
    }

}