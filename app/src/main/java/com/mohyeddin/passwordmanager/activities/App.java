package com.mohyeddin.passwordmanager.activities;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.fragments.SettingsFragment;
import com.mohyeddin.passwordmanager.utils.ThemeHelper;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class App extends Application {
    public static final String FRAGMENT_TAG="fragment";

    @Override
    public void onCreate() {
        super.onCreate();
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Paeez.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))

                .build());
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        boolean night=preferences.getBoolean(SettingsFragment.NIGHT_MODE_KEY,false);
        if (night){
            ThemeHelper.applyTheme(ThemeHelper.DARK_MODE);
        }else {
            ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE);
        }
    }
}
