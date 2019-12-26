package com.funkyqubits.kitchentimer.ui.util;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import androidx.databinding.BindingAdapter;

import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.google.android.material.button.MaterialButton;

public class BindingAdapters {

    @BindingAdapter(value = {"numberPickerMinValue", "numberPickerMaxValue"}, requireAll = true)
    public static void numberPickerMinMaxValue(NumberPicker numberPicker, int min, int max) {
        numberPicker.setMinValue(min);
        numberPicker.setMaxValue(max);
    }

    @BindingAdapter("timerStateButton")
    public static void timerStateButton(MaterialButton button, AlarmTimer.ALARMTIMER_STATE alarmtimerState) {
        int color;
        ColorStateList colorState;

        switch (button.getId()) {
            case R.id.btn_timer_start: {
                color = btn_start_state(button, alarmtimerState);
                break;
            }
            case R.id.btn_timer_pause: {
                color = btn_pause_state(button, alarmtimerState);
                break;
            }
            case R.id.btn_timer_reset: {
                color = btn_reset_state(button, alarmtimerState);
                break;
            }
            case R.id.btn_timer_edit: {
                color = btn_edit_state(button, alarmtimerState);
                break;
            }
            case R.id.btn_timer_delete: {
                color = btn_delete_state(button, alarmtimerState);
                break;
            }
            default: {
                color = button.getResources().getColor(R.color.colorInactive);
            }
        }
        colorState = ColorStateList.valueOf(color);
        button.setIconTint(colorState);
    }

    private static int btn_start_state(MaterialButton button, AlarmTimer.ALARMTIMER_STATE alarmtimerState) {
        if (alarmtimerState != null) {
            switch (alarmtimerState) {
                case NOT_RUNNING: {
                    button.setVisibility(View.VISIBLE);
                    return button.getResources().getColor(R.color.colorSuccess);
                }
                case PAUSED: {
                    button.setVisibility(View.VISIBLE);
                    return button.getResources().getColor(R.color.colorSuccess);
                }
                case RUNNING: {
                    button.setVisibility(View.GONE);
                    return button.getResources().getColor(R.color.colorInactive);
                }
                case COMPLETED: {
                    button.setVisibility(View.VISIBLE);
                    return button.getResources().getColor(R.color.colorInactive);
                }
                default: {
                    button.setVisibility(View.GONE);
                    return button.getResources().getColor(R.color.colorInactive);
                }
            }
        }
        button.setVisibility(View.GONE);
        return button.getResources().getColor(R.color.colorInactive);
    }

    private static int btn_pause_state(MaterialButton button, AlarmTimer.ALARMTIMER_STATE alarmtimerState) {
        if (alarmtimerState != null) {
            switch (alarmtimerState) {
                case NOT_RUNNING: {
                    button.setVisibility(View.GONE);
                    return button.getResources().getColor(R.color.colorInactive);
                }
                case PAUSED: {
                    button.setVisibility(View.GONE);
                    return button.getResources().getColor(R.color.colorInactive);
                }
                case RUNNING: {
                    button.setVisibility(View.VISIBLE);
                    return button.getResources().getColor(R.color.colorPrimary);
                }
                case COMPLETED: {
                    button.setVisibility(View.GONE);
                    return button.getResources().getColor(R.color.colorInactive);
                }
                default: {
                    button.setVisibility(View.GONE);
                    return button.getResources().getColor(R.color.colorInactive);
                }
            }
        }
        button.setVisibility(View.GONE);
        return button.getResources().getColor(R.color.colorInactive);
    }

    private static int btn_reset_state(MaterialButton button, AlarmTimer.ALARMTIMER_STATE alarmtimerState) {
        if (alarmtimerState != null) {
            switch (alarmtimerState) {
                case NOT_RUNNING: {
                    return button.getResources().getColor(R.color.colorInactive);
                }
                case PAUSED: {
                    return button.getResources().getColor(R.color.colorPrimary);
                }
                case RUNNING: {
                    return button.getResources().getColor(R.color.colorInactive);
                }
                case COMPLETED: {
                    return button.getResources().getColor(R.color.colorPrimary);
                }
                default: {
                    return button.getResources().getColor(R.color.colorInactive);
                }
            }
        }
        return button.getResources().getColor(R.color.colorInactive);
    }

    private static int btn_edit_state(MaterialButton button, AlarmTimer.ALARMTIMER_STATE alarmtimerState) {
        if (alarmtimerState != null) {
            switch (alarmtimerState) {
                case NOT_RUNNING: {
                    return button.getResources().getColor(R.color.colorPrimary);
                }
                case PAUSED: {
                    return button.getResources().getColor(R.color.colorInactive);
                }
                case RUNNING: {
                    return button.getResources().getColor(R.color.colorInactive);
                }
                case COMPLETED: {
                    return button.getResources().getColor(R.color.colorInactive);
                }
                default: {
                    return button.getResources().getColor(R.color.colorInactive);
                }
            }
        }
        return button.getResources().getColor(R.color.colorInactive);
    }

    private static int btn_delete_state(MaterialButton button, AlarmTimer.ALARMTIMER_STATE alarmtimerState) {
        if (alarmtimerState != null) {
            switch (alarmtimerState) {
                case NOT_RUNNING: {
                    return button.getResources().getColor(R.color.colorDanger);
                }
                case PAUSED: {
                    return button.getResources().getColor(R.color.colorDanger);
                }
                case RUNNING: {
                    return button.getResources().getColor(R.color.colorInactive);
                }
                case COMPLETED: {
                    return button.getResources().getColor(R.color.colorInactive);
                }
                default: {
                    return button.getResources().getColor(R.color.colorInactive);
                }
            }
        }
        return button.getResources().getColor(R.color.colorInactive);
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