package tunermusic.metronome.chords;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import tunermusic.metronome.chords.R;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

public class DiaologBackWithSave extends DialogFragment implements View.OnClickListener{
    private ImageButton btn_save;
    private ImageView btn_x;
    private ListenDialog listenDialog;
    private NativeAd mNativeAd;
    private Context context;
    public CardView cardView;
    private NativeAd ad;
    private NativeAdView adView;
    public DiaologBackWithSave(NativeAd adView, ListenDialog listenDialog)
    {
        this.listenDialog = listenDialog;
        this.ad = adView;
    }
    Animation animation;

    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if(getActivity() != null)
        {
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
        int width = displayMetrics.widthPixels;
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = (int) (width * 0.8);
            layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            if(ad != null)
            {
                layoutParams.gravity = Gravity.BOTTOM;
                layoutParams.y += width * 0.1;
            }
            else layoutParams.gravity = Gravity.CENTER;
            window.setAttributes(layoutParams);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_exit_and_back, container, false);
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.blink);
        hideNavBar();
        btn_save = v.findViewById(R.id.dialog_save);
        context = getContext();
        cardView = v.findViewById(R.id.cardViewNativeAds);
        btn_x = v.findViewById(R.id.btn_x);
        btn_x.setOnClickListener(this);
        v.findViewById(R.id.dialog_close).setOnClickListener(this);
        v.findViewById(R.id.dialog_save).setOnClickListener(this);
        if(ad != null)
        {
            adView = (NativeAdView) getLayoutInflater().inflate(R.layout.native_ad, null);
            populateNativeAdView(ad, adView);
            showAd(adView);
        }
        return v;

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        listenDialog.onCancel();
    }

    public void showAd(NativeAdView adView)
    {
        cardView.removeAllViews();
        cardView.addView(adView);
    }
    private void hideNavBar() {
        if(getDialog() != null && getDialog().getWindow() != null)
        {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
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

    @Override
    public void onClick(View view) {
        view.startAnimation(animation);
        int id = view.getId();
        if (id == R.id.dialog_close) {
            listenDialog.onClose();
            dismiss();
        } else if (id == R.id.dialog_save) {
            listenDialog.onSave();
            dismiss();
        }
        else if(id == R.id.btn_x)
        {
            listenDialog.onCancel();
            dismiss();
        }
    }
    public interface ListenDialog {
        void onClose();
        void onSave();
        void onCancel();
    }
}
