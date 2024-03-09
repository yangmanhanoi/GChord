package tunermusic.metronome.chords;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class CustomLinearLayoutManager extends LinearLayoutManager {
    private static final float MILLISECONDS_PER_INCH = 5f;
    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CustomLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        final LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()){
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }

            @Nullable
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return super.computeScrollVectorForPosition(targetPosition);
            }
        };
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}
