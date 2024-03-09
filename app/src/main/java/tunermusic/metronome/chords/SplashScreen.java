package tunermusic.metronome.chords;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import api.ApiService;
import model.DataLocalManager;
import model.LogEventManager;
import model.VersionInfor;
import model.VideoRetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity implements NetworkListener.netWorkChangeListener{
    ArrayList<String> videoIds = new ArrayList<>();
    ArrayList<String> videoTitle = new ArrayList<>();
    Intent intent;
    String html,title;
    private String pid, verSion, sAnd = "@T", timezone, lfcode, content_type = "application/json", os = "Android";
    private long startTime;
    ImageView updateButton;
    String sharedVideoURL="";
    NetworkListener networkListener = new NetworkListener();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent1 = getIntent();
        String action = intent1.getAction();
        String type = intent1.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                sharedVideoURL = intent1.getStringExtra(Intent.EXTRA_TEXT);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }

        pid = BuildConfig.APPLICATION_ID;
        verSion = BuildConfig.VERSION_CODE + "";
        timezone = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        StringBuffer sb = new StringBuffer();
        sb.append(pid); sb.append(sAnd); sb.append(verSion);sb.append(sAnd);sb.append(timezone);sb.append(sAnd);
        try {
            lfcode = Md5Sum(sb.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }


        startTime = System.currentTimeMillis();
        setContentView(R.layout.splash_screen);
        updateButton = findViewById(R.id.update_btn);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });
        YoYo.with(Techniques.RotateIn).duration(1000).playOn(findViewById(R.id.img));
        YoYo.with(Techniques.SlideInRight).duration(2000).playOn(findViewById(R.id.progress_circular));
        YoYo.with(Techniques.FadeIn).duration(2000).playOn(findViewById(R.id.appName));
        YoYo.with(Techniques.SlideInUp).duration(2000).playOn(findViewById(R.id.usage));
        String dir=getExternalFilesDir(null)+"/ChordifyClone/";
        intent = new Intent(SplashScreen.this, LogInActivity.class);
        File jsonDirectory = new File(dir);
        if (!jsonDirectory.exists()) jsonDirectory.mkdirs();


        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //Background work here
            requestVersion();
            handler.post(() -> {
                //UI Thread work here
            });
        });
    }


    private void requestVideoTrending()
    {
        ApiService apiService = VideoRetrofitClient.getRetrofitVideoTrendingInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.getVideoTrendingInfor("4gINGgt5dG1hX2NoYXJ0cw%3D%3D");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    html = response.body().string();
                    StringBuilder stringBuilder = new StringBuilder(html);
                    html="";
                    while (stringBuilder.indexOf("videoId")>=0) {

                        stringBuilder.delete(0, stringBuilder.indexOf("videoId") + 9);
                        StringTokenizer stringTokenizer = new StringTokenizer(stringBuilder.toString(), "\"");
                        stringTokenizer.nextToken();
                        String temp = stringTokenizer.nextToken();
                        if (temp.length() == 11 && !videoIds.contains(temp)) {
                            videoIds.add(temp);
                            html+=temp+" ";

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
                Intent intent1 = new Intent(SplashScreen.this, MainActivity.class);
                if(DataLocalManager.getIsLogin())
                {
                    intent1.putExtra("bundle", setBundle());
                    startActivity(intent1);
                }
                else {
                    intent.putExtra("bundle",setBundle());
                    startActivity(intent);
                }
            }
        });
    }
    private void requestVersion()
    {
        ApiService apiService = VideoRetrofitClient.getRetrofitCheckVersion().create(ApiService.class);
        Call<VersionInfor> call = apiService.getVersionInfor(content_type.trim(), pid.trim(), timezone.trim(), verSion.trim(), lfcode.trim(), os.trim());
        call.enqueue(new Callback<VersionInfor>() {
            @Override
            public void onResponse(Call<VersionInfor> call, Response<VersionInfor> response) {

                if(response.code() == 200)
                {
                    requestVideoTrending();
                }
                else if(response.code() == 406)
                {
                    findViewById(R.id.versionUpdateSplash).setVisibility(View.VISIBLE);
                    findViewById(R.id.loadingSplash).setVisibility(View.GONE);
                }

            }
            @Override
            public void onFailure(Call<VersionInfor> call, Throwable t) {
                findViewById(R.id.errorSplash).setVisibility(View.VISIBLE);
                findViewById(R.id.loadingSplash).setVisibility(View.GONE);
            }
        });
    }
    private String Md5Sum(String str2Encrypt) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] bytesOfMessage = str2Encrypt.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] theMD5digest = md.digest(bytesOfMessage);
        // Convert the encrypted bytes back to string in base 16
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < theMD5digest.length; i++)
        {
            sb.append(Integer.toString((theMD5digest[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    @Override
    public void onNetWorkChange(boolean isConnected) {
        if (!isConnected){
            // concern may be change to putextra(bundle)
            intent.putExtra("videosId","");
            intent.putExtra("videosTitle","");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                }
            },2000);
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
                title="";
                for (String s1:videoTitle){
                    title+=s1+ ";";
                }
                Intent intent1 = new Intent(SplashScreen.this, MainActivity.class);
                if(DataLocalManager.getIsLogin())
                {
                    intent1.putExtra("bundle", setBundle());
                    startActivity(intent1);
                }
                else {
                    intent.putExtra("bundle",setBundle());
                    startActivity(intent);
                }

            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bundle bundle = new Bundle();
        bundle.putLong("open_time", startTime);
        bundle.putLong("wait_time", System.currentTimeMillis() - startTime);
        LogEventManager.logUserBehavior(1, bundle, 0);
    }
    public Bundle setBundle(){
        Bundle bundle = new Bundle();
        bundle.putString("videosId",html);
        bundle.putString("videosTitle",title);
        bundle.putString("shareVideoURL",sharedVideoURL);
        bundle.putString("data","");
        bundle.putString("beat","");
        bundle.putString("currentId","");
        bundle.putString("currentTitle","");
        return bundle;
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkListener, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(networkListener != null) unregisterReceiver(networkListener);
    }
}
