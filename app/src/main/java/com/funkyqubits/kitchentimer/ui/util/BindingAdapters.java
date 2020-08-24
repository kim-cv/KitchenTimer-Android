package com.funkyqubits.kitchentimer.ui.util;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class BindingAdapters {

    @BindingAdapter("btnClickable")
    public static void btnClickable(Button button, boolean enable) {
        int color;
        if (enable) {
            color = button.getResources().getColor(R.color.colorMain);
        } else {
            color = button.getResources().getColor(R.color.colorSecondary);
        }
        button.setBackgroundColor(color);
    }

    @BindingAdapter("errorText")
    public static void setErrorMessage(TextInputLayout textInputLayout, String error) {
        textInputLayout.setError(error);
    }

    @BindingAdapter(value = {"numberPickerMinValue", "numberPickerMaxValue"}, requireAll = true)
    public static void numberPickerMinMaxValue(NumberPicker numberPicker, int min, int max) {
        numberPicker.setMinValue(min);
        numberPicker.setMaxValue(max);
    }

    @BindingAdapter("timerStateButton")
    public static void timerStateButton(LinearLayout linearLayout, AlarmTimer.ALARMTIMER_STATE alarmtimerState) {
        int colorResource;

        MaterialButton button = linearLayout.findViewWithTag("btn");
        TextView txt = linearLayout.findViewWithTag("txt");

        switch (button.getId()) {
            /*
            case R.id.btn_timer_start: {
                colorResource = btn_start_state(button, alarmtimerState);
                break;
            }
            case R.id.btn_timer_pause: {
                colorResource = btn_pause_state(button, alarmtimerState);
                break;
            }
            */
            case R.id.btn_timer_reset: {
                colorResource = btn_reset_state(alarmtimerState);
                break;
            }
            case R.id.btn_timer_edit: {
                colorResource = btn_edit_state(alarmtimerState);
                break;
            }
            case R.id.btn_timer_delete: {
                colorResource = btn_delete_state(alarmtimerState);
                break;
            }
            default: {
                colorResource = R.color.colorInactive;
            }
        }

        // Set button NOT clickable if color is inactive
        boolean clickable = !(colorResource == R.color.colorInactive);
        linearLayout.setClickable(clickable);
        button.setClickable(clickable);
        txt.setClickable(clickable);

        // Set color
        int color = button.getResources().getColor(colorResource);
        ColorStateList colorState = ColorStateList.valueOf(color);
        button.setIconTint(colorState);
    }

    private static int btn_start_state(MaterialButton button, AlarmTimer.ALARMTIMER_STATE alarmtimerState) {
        if (alarmtimerState != null) {
            switch (alarmtimerState) {
                case NOT_RUNNING: {
                    button.setVisibility(View.VISIBLE);
                    return R.color.colorSuccess;
                }
                case PAUSED: {
                    button.setVisibility(View.VISIBLE);
                    return R.color.colorSuccess;
                }
                case RUNNING: {
                    button.setVisibility(View.GONE);
                    return R.color.colorInactive;
                }
                case COMPLETED: {
                    button.setVisibility(View.VISIBLE);
                    return R.color.colorInactive;
                }
                default: {
                    button.setVisibility(View.GONE);
                    return R.color.colorInactive;
                }
            }
        }
        button.setVisibility(View.GONE);
        return R.color.colorInactive;
    }

    private static int btn_pause_state(MaterialButton button, AlarmTimer.ALARMTIMER_STATE alarmtimerState) {
        if (alarmtimerState != null) {
            switch (alarmtimerState) {
                case NOT_RUNNING: {
                    button.setVisibility(View.GONE);
                    return R.color.colorInactive;
                }
                case PAUSED: {
                    button.setVisibility(View.GONE);
                    return R.color.colorInactive;
                }
                case RUNNING: {
                    button.setVisibility(View.VISIBLE);
                    return R.color.timerPaused;
                }
                case COMPLETED: {
                    button.setVisibility(View.GONE);
                    return R.color.colorInactive;
                }
                default: {
                    button.setVisibility(View.GONE);
                    return R.color.colorInactive;
                }
            }
        }
        button.setVisibility(View.GONE);
        return R.color.colorInactive;
    }

    private static int btn_reset_state(AlarmTimer.ALARMTIMER_STATE alarmtimerState) {
        if (alarmtimerState != null) {
            switch (alarmtimerState) {
                case NOT_RUNNING: {
                    return R.color.colorInactive;
                }
                case PAUSED: {
                    return R.color.colorPrimary;
                }
                case RUNNING: {
                    return R.color.colorInactive;
                }
                case COMPLETED: {
                    return R.color.colorPrimary;
                }
                default: {
                    return R.color.colorInactive;
                }
            }
        }
        return R.color.colorInactive;
    }

    private static int btn_edit_state(AlarmTimer.ALARMTIMER_STATE alarmtimerState) {
        if (alarmtimerState != null) {
            switch (alarmtimerState) {
                case NOT_RUNNING: {
                    return R.color.colorPrimary;
                }
                case PAUSED: {
                    return R.color.colorInactive;
                }
                case RUNNING: {
                    return R.color.colorInactive;
                }
                case COMPLETED: {
                    return R.color.colorInactive;
                }
                default: {
                    return R.color.colorInactive;
                }
            }
        }
        return R.color.colorInactive;
    }

    private static int btn_delete_state(AlarmTimer.ALARMTIMER_STATE alarmtimerState) {
        if (alarmtimerState != null) {
            switch (alarmtimerState) {
                case NOT_RUNNING: {
                    return R.color.colorDanger;
                }
                case PAUSED: {
                    return R.color.colorDanger;
                }
                case RUNNING: {
                    return R.color.colorInactive;
                }
                case COMPLETED: {
                    return R.color.colorInactive;
                }
                default: {
                    return R.color.colorInactive;
                }
            }
        }
        return R.color.colorInactive;
    }

    @BindingAdapter("timerStateLeftBorder")
    public static void timerStateLeftBorder(LinearLayout view, AlarmTimer.ALARMTIMER_STATE alarmtimerState) {
        LayerDrawable layerDrawable = (LayerDrawable) view.getBackground();

        GradientDrawable leftBorder = (GradientDrawable) layerDrawable.getDrawable(0);
        GradientDrawable leftBorderMutated = (GradientDrawable) leftBorder.mutate();

        int color;
        if (alarmtimerState != null) {
            switch (alarmtimerState) {
                case NOT_RUNNING: {
                    color = view.getResources().getColor(R.color.timerNotRunning);
                    leftBorderMutated.setColor(color);
                    break;
                }
                case PAUSED: {
                    color = view.getResources().getColor(R.color.timerPaused);
                    leftBorderMutated.setColor(color);
                    break;
                }
                case RUNNING: {
                    color = view.getResources().getColor(R.color.timerRunning);
                    leftBorderMutated.setColor(color);
                    break;
                }
                case COMPLETED: {
                    color = view.getResources().getColor(R.color.timerComplete);
                    leftBorderMutated.setColor(color);
                    break;
                }
            }
        }
    }
}