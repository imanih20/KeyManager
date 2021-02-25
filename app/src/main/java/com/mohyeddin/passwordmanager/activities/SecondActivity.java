package com.mohyeddin.passwordmanager.activities;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.fragments.AboutFragment;
import com.mohyeddin.passwordmanager.fragments.SettingsFragment;
import com.mohyeddin.passwordmanager.fragments.ShowPasswordFragment;
import com.mohyeddin.passwordmanager.models.PasswordModel;

public class SecondActivity extends MyActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        if (getIntent().getExtras()!=null){
            Fragment currentFragment=new Fragment();
            switch (getIntent().getExtras().getInt(App.FRAGMENT_TAG)){
                case 0:
                    currentFragment=SettingsFragment.newInstance();
                    break;
                case 1:
                    currentFragment= ShowPasswordFragment.newInstance(getIntent().getExtras().getInt(PasswordModel.ID_KEY),
                            getIntent().getExtras().getString(PasswordModel.PASSWORD_KEY),getIntent().getExtras().getString(PasswordModel.TITLE_KEY));
                    break;
                case 2:
                    currentFragment= AboutFragment.newInstance();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.second_fragment_container,currentFragment).commit();
        }
    }
}