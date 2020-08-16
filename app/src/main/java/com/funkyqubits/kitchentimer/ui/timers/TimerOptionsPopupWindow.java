package com.funkyqubits.kitchentimer.ui.timers;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.funkyqubits.kitchentimer.BR;
import com.funkyqubits.kitchentimer.R;
import com.funkyqubits.kitchentimer.databinding.TimerListItemOptionsBinding;
import com.funkyqubits.kitchentimer.models.AlarmTimer;
import com.funkyqubits.kitchentimer.util.UtilMethods;


public class TimerOptionsPopupWindow extends PopupWindow {
    public TimerOptionsPopupWindow(@NonNull Context context) {
        super(context);
    }

    public TimerOptionsPopupWindow(View parent, AlarmTimer timer) {
        //ViewDataBinding popUpBinding = DataBindingUtil.bind(parent);
        //popUpBinding.setLifecycleOwner(holder.Fragment); // Necessary for LiveData and MutableLiveData to work

        // Inflate the popup layout
        //LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View layout = layoutInflater.inflate(R.layout.timer_list_item_options, null);
        //TimerListItemOptionsBinding layout = TimerListItemOptionsBinding.inflate(LayoutInflater.from(parent.getContext()), (ViewGroup) popUpBinding.getRoot(), false);
        TimerListItemOptionsBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.timer_list_item_options, null, false);

        // Creating the PopupWindow
        PopupWindow popupWindow = new PopupWindow(parent.getContext());
        //popupWindow.setContentView(layout);
        popupWindow.setContentView(binding.getRoot());
        popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        // Bind model to view
        binding.setVariable(BR.alarmTimer, timer);
        binding.executePendingBindings();

        // Get location of clicked view
        //Rect viewLocation = UtilMethods.locateView(v);

        //Clear the default translucent background
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location
        //popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, viewLocation.left, viewLocation.bottom);
        popupWindow.showAsDropDown(parent);
    }

}
