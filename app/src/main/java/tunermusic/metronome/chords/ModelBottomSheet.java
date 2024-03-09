package tunermusic.metronome.chords;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.internal.EdgeToEdgeUtils;

import java.util.ArrayList;

import adapter.AdapterRecyclerTrending;
import tunermusic.metronome.chords.fragments.ChordFragment;

public class ModelBottomSheet extends BottomSheetDialogFragment {
    private String title, infor;
    public CardView cardView;
    public ImageView imageView;
    private TextView txt_title, txt_infor;
    private BottomSheetBehavior<View> behavior;
    private NativeAd mNativeAd;
    private CardView bottomSheet;
    private AdapterRecyclerTrending.onVideoChosen onVideoChosen;
    private ImageView logInButton = null;
    private ImageView watchAdButton = null;
    private View.OnClickListener onClickListener1,onClickListener2;
    private ArrayList<String> videoIdsTrending = new ArrayList<>();
    private ArrayList<String> videoTitleTrending = new ArrayList<>();


    public BottomSheetBehavior.BottomSheetCallback callback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if(newState == BottomSheetBehavior.STATE_HIDDEN)
            {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };
    public ModelBottomSheet(String titleName, String infor, NativeAd mNativeAd)
    {
        this.title = titleName;
        this.infor = infor;
        this.mNativeAd = mNativeAd;
    }
    public ModelBottomSheet(String titleName, String infor, ImageView logInButton, View.OnClickListener onClickListener1)
    {
        this.title = titleName;
        this.infor = infor;
        this.logInButton = logInButton;
        this.onClickListener1 = onClickListener1;
    }
    public ModelBottomSheet(String titleName, String infor, ImageView logInButton, ImageView watchAdButton, View.OnClickListener onClickListener1, View.OnClickListener onClickListener2)
    {
        this.title = titleName;
        this.infor = infor;
        this.logInButton = logInButton;
        this.watchAdButton = watchAdButton;
        this.onClickListener1 = onClickListener1;
        this.onClickListener2 = onClickListener2;
    }
    public ModelBottomSheet(String titleName, String infor, ArrayList<String> videoIdsTrending,ArrayList<String> videoTitleTrending,AdapterRecyclerTrending.onVideoChosen onVideoChosen)
    {
        this.title = titleName;
        this.infor = infor;
        this.videoIdsTrending = videoIdsTrending;
        this.videoTitleTrending = videoTitleTrending;
        this.onVideoChosen = onVideoChosen;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.model_bottom_sheet, container, false);
        txt_title = v.findViewById(R.id.txt_title);
        txt_infor = v.findViewById(R.id.txt_infor);
        imageView = v.findViewById(R.id.imageDialog);
        cardView = v.findViewById(R.id.cardViewNativeAds);

        txt_infor.setText(infor);
        txt_title.setText(title);

        if(mNativeAd != null)
        {
            NativeAdView view = (NativeAdView) getLayoutInflater().inflate(R.layout.native_ad, null);
            populateNativeAdView(mNativeAd, view);
            cardView.removeAllViews();
            cardView.addView(view);
        }
        if (logInButton!=null){
            if (watchAdButton!=null){
                logInButton.setOnClickListener(onClickListener1);
                watchAdButton.setOnClickListener(onClickListener2);
                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(logInButton);
                linearLayout.addView(watchAdButton);
                cardView.setCardBackgroundColor(Color.TRANSPARENT);
                cardView.setBackgroundColor(Color.TRANSPARENT);
                cardView.removeAllViews();
                cardView.addView(linearLayout);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cardView.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin,layoutParams.topMargin,layoutParams.rightMargin,layoutParams.bottomMargin+50);
                cardView.setLayoutParams(layoutParams);
            } else {
                logInButton.setOnClickListener(onClickListener1);
                cardView.setCardBackgroundColor(Color.TRANSPARENT);
                cardView.setBackgroundColor(Color.TRANSPARENT);
                cardView.removeAllViews();
                cardView.addView(logInButton);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cardView.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin,layoutParams.topMargin,layoutParams.rightMargin,layoutParams.bottomMargin+50);
                cardView.setLayoutParams(layoutParams);
            }

        }
        if (videoIdsTrending.size()!=0){
            imageView.setVisibility(View.GONE);
            RecyclerView recyclerView = new RecyclerView(getActivity());
            AdapterRecyclerTrending adapterRecyclerTrending = new AdapterRecyclerTrending(getActivity(), videoIdsTrending, videoTitleTrending, onVideoChosen);
            recyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapterRecyclerTrending);
            cardView.addView(recyclerView);
            cardView.setCardBackgroundColor(Color.TRANSPARENT);
            cardView.setBackgroundColor(Color.TRANSPARENT);
            recyclerView.setPadding(20,0,20,100);
        }
        if(behavior != null && behavior instanceof  BottomSheetBehavior)
        {
            behavior.addBottomSheetCallback(callback);
        }

        bottomSheet = v.findViewById(R.id.standard_bottom_sheet);
        behavior = BottomSheetBehavior.from(v.findViewById(R.id.standard_bottom_sheet));
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setHideable(false);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public int show(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        return super.show(transaction, tag);
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView)
    {
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

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog =super.onCreateDialog(savedInstanceState);
        EdgeToEdgeUtils.applyEdgeToEdge(dialog.getWindow(), true);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}
