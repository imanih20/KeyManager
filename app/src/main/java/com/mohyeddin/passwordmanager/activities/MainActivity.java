package com.mohyeddin.passwordmanager.activities;

import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;
import android.os.Bundle;
import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.databinding.ActivityMainBinding;
import com.mohyeddin.passwordmanager.fragments.AboutFragment;
import com.mohyeddin.passwordmanager.fragments.AddFragment;
import com.mohyeddin.passwordmanager.fragments.ListFragment;
import com.mohyeddin.passwordmanager.fragments.SettingsFragment;
import com.mohyeddin.passwordmanager.utils.ShowFragmentUtils;

public class MainActivity extends MyActivity {
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment;
            final int options_nav = R.id.options_nav;
            final int about_nav = R.id.info_nav;
            final int add_nav = R.id.add_nav;
            switch (item.getItemId()){
                case options_nav:
                    fragment = SettingsFragment.newInstance();
                    break;
                case about_nav:
                    fragment = AboutFragment.newInstance();
                    break;
                case add_nav:
                    fragment = AddFragment.newInstance();
                    break;
                default:
                    fragment = ListFragment.newInstance();

            }
            ShowFragmentUtils.showFragment(getSupportFragmentManager(),R.id.main_fragment_container,fragment);
            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}