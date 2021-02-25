package com.mohyeddin.passwordmanager.views;



import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mohyeddin.passwordmanager.R;

public class ColoredToast {

    private static final int RED = 0xfff44336;
    private static final int ORANGE = 0xffffc107;
    private static final int GREEN = 0xff4caf50;

    private static final int IC_ALERT = R.drawable.ic_alert;
    private static final int IC_WARNING = R.drawable.ic_warning;
    private static final int IC_SUCCESS = R.drawable.ic_success;
    public static final int LENGTH_SHORT = 0;



    public View view;
    Toast toast;
    public ColoredToast(Context context){
        this.toast = new Toast(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.view = inflater.inflate(R.layout.colored_toast_layout, null);
        toast.setView(view);
    }

    public View getView(){
        return view;
    }
    public void setText(String text){
        if(view==null)
            return;
        ((TextView) view.findViewById(R.id.toast_msg)).setText(text);
    }

    public void setIcon(int iconResId){
        if(view==null)
            return;
        ((ImageView) view.findViewById(R.id.toast_icon)).setImageResource(iconResId);
    }
    public Toast getToast(){
        return toast;
    }


    public static Toast alert(Context context, String text, int duration){
        ColoredToast coloredToast = new ColoredToast(context);
        coloredToast.setText(text);
        coloredToast.getToast().setDuration(duration);
        coloredToast.getToast().setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 50);
        coloredToast.setIcon(IC_ALERT);
        coloredToast.getView().setBackgroundColor(Color.TRANSPARENT);
        coloredToast.getView().findViewById(R.id.layout).setBackgroundColor(RED);
        return coloredToast.getToast();
    }

    public static Toast warning(Context context, String text, int duration){
        ColoredToast coloredToast = new ColoredToast(context);
        coloredToast.setText(text);
        coloredToast.getToast().setDuration(duration);
        coloredToast.getToast().setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 50);
        coloredToast.setIcon(IC_WARNING);
        coloredToast.getView().setBackgroundColor(Color.TRANSPARENT);
        coloredToast.getView().findViewById(R.id.layout).setBackgroundColor(ORANGE);
        return coloredToast.getToast();
    }
    public static Toast success(Context context, String text, int duration){
        ColoredToast coloredToast = new ColoredToast(context);
        coloredToast.setText(text);
        coloredToast.getToast().setDuration(duration);
        coloredToast.getToast().setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 50);
        coloredToast.setIcon(IC_SUCCESS);
        coloredToast.getView().setBackgroundColor(Color.TRANSPARENT);
        coloredToast.getView().findViewById(R.id.layout).setBackgroundColor(GREEN);
        return coloredToast.getToast();
    }
}
