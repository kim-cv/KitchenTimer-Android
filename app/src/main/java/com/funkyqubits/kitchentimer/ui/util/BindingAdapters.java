package com.funkyqubits.kitchentimer.ui.util;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.LinearLayout;

import androidx.databinding.BindingAdapter;

import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.models.AlarmTimer;

public class BindingAdapters {
    @BindingAdapter("timerState")
    public static void timerState(LinearLayout view, AlarmTimer.ALARMTIMER_STATE alarmtimerState) {
        LayerDrawable layerDrawable = (LayerDrawable) view.getBackground();

        GradientDrawable leftBorder = (GradientDrawable) layerDrawable.getDrawable(0);
        GradientDrawable leftBorderMutated = (GradientDrawable) leftBorder.mutate();

        int color;
        if (alarmtimerState != null) {
            switch (alarmtimerState) {
                case NOT_RUNNING: {
                    color = view.getResources().getColor(R.color.colorSecondary);
                    leftBorderMutated.setColor(color);
                    break;
                }
                case PAUSED: {
                    color = view.getResources().getColor(R.color.colorMain);
                    leftBorderMutated.setColor(color);
                    break;
                }
                case RUNNING: {
                    color = view.getResources().getColor(R.color.colorDanger);
                    leftBorderMutated.setColor(color);
                    break;
                }
                case COMPLETED: {
                    color = view.getResources().getColor(R.color.colorSuccess);
                    leftBorderMutated.setColor(color);
                    break;
                }
            }
        }
    }
}