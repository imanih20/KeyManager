package com.mohyeddin.passwordmanager.utils;
import android.app.UiModeManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {
    private final UiModeManager modeManager;
    public ThemeHelper(Context context){
        modeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
    }

    public void switchMode(){
        if (modeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES){
            modeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);
        }else if (modeManager.getNightMode() == UiModeManager.MODE_NIGHT_NO){
            modeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
        }
    }
    public int getMode(){
        return modeManager.getNightMode();
    }
}
