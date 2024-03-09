package tunermusic.metronome.chords.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;

import adapter.AdapterRecyclerTrending;
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
import tunermusic.metronome.chords.R;

public class HomeFragment extends Fragment implements LifecycleObserver, NetworkListener.netWorkChangeListener {
    ImageView premiumBut,propertiesButton,btnHelp;
    private static HomeFragment instance;
    RecyclerView recyclerViewHistory,recyclerViewTrending;
    private long startTime;
    ArrayList<String> videoIdsTrending = new ArrayList<>();
    ArrayList<String> videoTitleTrending = new ArrayList<>();
    TextView noInternet;
    CircularProgressIndicator progressIndicator;
    private MainActivity mainActivity;
    public static HomeFragment getInstance()
    {
        if(instance == null) instance = new HomeFragment();
        return instance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        premiumBut = v.findViewById(R.id.premiumBut);
        recyclerViewHistory = v.findViewById(R.id.recycleViewHistory);
        recyclerViewTrending = v.findViewById(R.id.recyclerViewTrending);
        btnHelp = v.findViewById(R.id.helpButHome);
        progressIndicator = v.findViewById(R.id.progressBarTrending);
        noInternet = v.findViewById(R.id.noInternetTrending);
        propertiesButton = v.findViewById(R.id.propertiesButHome);
        startTime = System.currentTimeMillis();
        mainActivity = (MainActivity) getActivity();
        videoIdsTrending = mainActivity.videoIdsTrending;
        videoTitleTrending = mainActivity.videoTitleTrending;
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
        propertiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                mainActivity.drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        premiumBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new PremiumFragment());
                fragmentTransaction.commit();
            }
        });
        ArrayList<String> videoIdHistory = new ArrayList<>();
        ArrayList<String> videoTitleHistory = new ArrayList<>();
        File jsonDirectory = new File(getActivity().getExternalFilesDir(null)+"/ChordifyClone/");
        if (jsonDirectory.exists()){
                File[] jsonFiles = jsonDirectory.listFiles();
                if (jsonFiles.length!=0){
                    Arrays.sort(jsonFiles, new Comparator<File>() {
                        @Override
                        public int compare(File o1, File o2) {
                            if (o1.lastModified() > o2.lastModified()) {
                                return -1;
                            } else if (o1.lastModified() < o2.lastModified()) {
                                return +1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    for (int i=0;i< jsonFiles.length;i++){
                        File file = jsonFiles[i];
                        if (i<30){
                            try {
                                JSONObject jsonObject = new JSONObject(readJsonFile(file));
                                videoIdHistory.add(jsonObject.getString("id"));
                                videoTitleHistory.add(jsonObject.getString("title"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        } else file.delete();
                    }
                    AdapterRecyclerTrending adapterRecyclerHistory = new AdapterRecyclerTrending(getActivity(), videoIdHistory, videoTitleHistory, new AdapterRecyclerTrending.onVideoChosen() {
                        @Override
                        public void iVideoClicked(Bundle params) {
                            ChordFragment.getInstance().setArguments(params);
                            if (!params.getString("videoId").equals(mainActivity.videoId)){
                                mainActivity.beat="";
                                mainActivity.data="";
                            }
                            mainActivity.loadFragment(ChordFragment.getInstance(), 2);
                        }
                    });
                    adapterRecyclerHistory.notifyDataSetChanged();
                    recyclerViewHistory.setRecycledViewPool(new RecyclerView.RecycledViewPool());
                    recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
                    recyclerViewHistory.setAdapter(adapterRecyclerHistory);
                }
        }

        if (videoIdsTrending.size()!=0) {
            AdapterRecyclerTrending adapterRecyclerTrending1 = new AdapterRecyclerTrending(getActivity(), videoIdsTrending, videoTitleTrending, new AdapterRecyclerTrending.onVideoChosen() {
                @Override
                public void iVideoClicked(Bundle params) {
                    ChordFragment.getInstance().setArguments(params);
                    mainActivity.loadFragment(ChordFragment.getInstance(), 2);
                }
            });
            adapterRecyclerTrending1.notifyDataSetChanged();
            recyclerViewTrending.setRecycledViewPool(new RecyclerView.RecycledViewPool());
            recyclerViewTrending.setVisibility(View.VISIBLE);
            recyclerViewTrending.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewTrending.setAdapter(adapterRecyclerTrending1);

        } else {
            recyclerViewTrending.setVisibility(View.GONE);
            noInternet.setVisibility(View.VISIBLE);
        }

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animation);
                WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);
                ModelBottomSheet bottomSheet = new ModelBottomSheet(getResources().getString(R.string.what_can_do), getResources().getString(R.string.what_do), ((MainActivity) getActivity()).getmNativeAd());
                bottomSheet.show(getActivity().getSupportFragmentManager(), bottomSheet.getTag());
                //updateBottomSheetHeights();
                //((MainActivity) getActivity()).showBottomDialog(getResources().getString(R.string.what_can_do), getResources().getString(R.string.what_do));
            }
        });
    }

    public String readJsonFile(File file)
    {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
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

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        super.onStop();
        Bundle bundle = new Bundle();
        //bundle.putInt("time", (int) (System.currentTimeMillis() - startTime));
        LogEventManager.logUserBehavior(LogEventManager.USER_IN_FRAG, bundle, R.id.homeBut);
        mainActivity.startTime = System.currentTimeMillis();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        super.onResume();
        mainActivity.countHome++;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onNetWorkChange(boolean isConnected) {
        if (isConnected){
            noInternet.setVisibility(View.GONE);
            if (videoIdsTrending.size()==0) {
                progressIndicator.setVisibility(View.VISIBLE);
                ApiService apiService = VideoRetrofitClient.getRetrofitVideoTrendingInstance().create(ApiService.class);
                Call<ResponseBody> call = apiService.getVideoTrendingInfor("4gINGgt5dG1hX2NoYXJ0cw%3D%3D");
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String html = response.body().string();
                            StringBuilder stringBuilder = new StringBuilder(html);
                            html="";
                            while (stringBuilder.indexOf("videoId")>=0) {

                                stringBuilder.delete(0, stringBuilder.indexOf("videoId") + 9);
                                StringTokenizer stringTokenizer = new StringTokenizer(stringBuilder.toString(), "\"");
                                stringTokenizer.nextToken();
                                String temp = stringTokenizer.nextToken();
                                if (temp.length() == 11 && !videoIdsTrending.contains(temp)) {
                                    videoIdsTrending.add(temp);
                                    html+=temp+" ";

                                }
                            }
                            for (int i=0 ;i<videoIdsTrending.size();i++){
                                new DownloadTitleTask(i).execute(videoIdsTrending.get(i));
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
            } else {
                AdapterRecyclerTrending adapterRecyclerTrending2 = new AdapterRecyclerTrending(getActivity(), videoIdsTrending, videoTitleTrending, new AdapterRecyclerTrending.onVideoChosen() {
                    @Override
                    public void iVideoClicked(Bundle params) {
                        ChordFragment.getInstance().setArguments(params);
                        mainActivity.loadFragment(ChordFragment.getInstance(), 2);
                    }
                });
                adapterRecyclerTrending2.notifyDataSetChanged();
                recyclerViewTrending.setRecycledViewPool(new RecyclerView.RecycledViewPool());
                recyclerViewTrending.setVisibility(View.VISIBLE);
                recyclerViewTrending.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                recyclerViewTrending.setAdapter(adapterRecyclerTrending2);
            }
        } else {
            recyclerViewTrending.setVisibility(View.GONE);
            noInternet.setVisibility(View.VISIBLE);
        }
    }
    class DownloadTitleTask extends AsyncTask<String,Void, String> {
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
            videoTitleTrending.add(s);
            if (counter==videoIdsTrending.size()-1){
                int counter =0;
                while (counter<videoTitleTrending.size()){
                    if (videoTitleTrending.get(counter).length()==0){
                        videoTitleTrending.remove(counter);
                        videoIdsTrending.remove(counter);
                    }
                    counter++;
                }
                String title ="";
                for (String s1:videoTitleTrending){
                    title+=s1+" ";
                }
                String id ="";
                for (String s1:videoIdsTrending){
                    id+=s1+" ";
                }
                mainActivity.ids=id;
                mainActivity.titles=title;
                mainActivity.videoIdsTrending=videoIdsTrending;
                mainActivity.videoTitleTrending=videoTitleTrending;
                AdapterRecyclerTrending adapterRecyclerTrending3 = new AdapterRecyclerTrending(getActivity(), videoIdsTrending, videoTitleTrending, new AdapterRecyclerTrending.onVideoChosen() {
                    @Override
                    public void iVideoClicked(Bundle params) {
                        ChordFragment.getInstance().setArguments(params);
                        mainActivity.loadFragment(ChordFragment.getInstance(), 2);
                    }
                });
                adapterRecyclerTrending3.notifyDataSetChanged();
                recyclerViewTrending.setRecycledViewPool(new RecyclerView.RecycledViewPool());
                recyclerViewTrending.setVisibility(View.VISIBLE);
                recyclerViewTrending.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                recyclerViewTrending.setAdapter(adapterRecyclerTrending3);
                progressIndicator.setVisibility(View.GONE);
            }
        }
    }
}