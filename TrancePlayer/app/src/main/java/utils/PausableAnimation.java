package utils;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;

/**
 * Implement a pausable animation class.
 * Created by duc.t.pham on 9/22/15.
 */
public class PausableAnimation extends RotateAnimation {
    private long mElapsedAtPause = 0;
    private boolean mPaused = false;

    public PausableAnimation(float fromDegrees, float toDegrees, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        super(fromDegrees, toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue);
    }


    @Override
    public boolean getTransformation(long currentTime, Transformation outTransformation) {
        if (mPaused && mElapsedAtPause == 0) {
            mElapsedAtPause = currentTime - getStartTime();
        }
        if (mPaused)
            setStartTime(currentTime - mElapsedAtPause);
        return super.getTransformation(currentTime, outTransformation);
    }

    public void pause() {
        mElapsedAtPause = 0;
        mPaused = true;
    }

    public void resume() {
        mPaused = false;
    }
}
