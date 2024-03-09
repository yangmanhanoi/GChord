package tunermusic.metronome.chords;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogHelp extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

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
            window.setLayout((int) (width * 0.8), FrameLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
        }
    }
}
