package com.mohyeddin.passwordmanager.activities;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.os.Bundle;
import com.mohyeddin.passwordmanager.BuildConfig;
import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.fragments.LoginFragment;
import com.mohyeddin.passwordmanager.fragments.SignFragment;
import com.mohyeddin.passwordmanager.utils.LoginDbHelper;

public class LouncherActivity extends MyActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_louncher);
        AppCompatTextView buildVersion = findViewById(R.id.build_version_name);
        buildVersion.setText(BuildConfig.VERSION_NAME);
        LoginDbHelper dbHelper=new LoginDbHelper(this);
        if (dbHelper.getLoginPassword()==null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.louncher_container, SignFragment.newInstance(true,""))
            .commit();
        }else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.louncher_container, LoginFragment.newInstance())
                    .commit();
        }

    }
}