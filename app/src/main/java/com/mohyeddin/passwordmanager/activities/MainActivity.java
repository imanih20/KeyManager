package com.mohyeddin.passwordmanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.adpaters.PasswordsListAdapter;
import com.mohyeddin.passwordmanager.models.PasswordModel;
import com.mohyeddin.passwordmanager.utils.PasswordDbHelper;
import com.mohyeddin.passwordmanager.views.AddDialog;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MyActivity {
    private PasswordsListAdapter adapter;
    private PasswordDbHelper dbHelper;
    private List<PasswordModel> passwords;

    //helper variables
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init drawer nav
        BottomNavigationView navigationView = findViewById(R.id.bottom_nav_view);
        navigationView.setOnNavigationItemSelectedListener(this::onOptionsItemSelected);
        navigationView.bringToFront();
        //init dbHelper
        dbHelper=new PasswordDbHelper(this);
        passwords=dbHelper.getAllPasswords();
        //init faButton
        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar()!=null;
        getSupportActionBar().setTitle(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.updateData((ArrayList<PasswordModel>) dbHelper.getAllPasswords());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent=new Intent(MainActivity.this,SecondActivity.class);
        final int options_nav = R.id.options_nav;
        final int about_nav = R.id.about_nav;
        switch (item.getItemId()){
            case options_nav:
                intent.putExtra(App.FRAGMENT_TAG,0);
                startActivity(intent);
                break;
            case about_nav:
                intent.putExtra(App.FRAGMENT_TAG,2);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
    }
}