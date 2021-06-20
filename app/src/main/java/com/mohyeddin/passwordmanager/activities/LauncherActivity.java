package com.mohyeddin.passwordmanager.activities;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import com.mohyeddin.passwordmanager.BuildConfig;
import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.databinding.ActivityLouncherBinding;
import com.mohyeddin.passwordmanager.fragments.LoginFragment;
import com.mohyeddin.passwordmanager.fragments.SignFragment;
import com.mohyeddin.passwordmanager.utils.LoginDbHelper;
import com.mohyeddin.passwordmanager.utils.ShowFragmentUtils;

public class LauncherActivity extends MyActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLouncherBinding binding = ActivityLouncherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.buildVersionName.setText(BuildConfig.VERSION_NAME);
        LoginDbHelper dbHelper=new LoginDbHelper(this);
        Fragment fragment;
        if (dbHelper.getLoginPassword()==null){
            fragment = SignFragment.newInstance(true,"");
        }else {
            fragment = LoginFragment.newInstance();
        }
        ShowFragmentUtils.showFragment(getSupportFragmentManager(),binding.louncherContainer.getId(),fragment);
    }
}