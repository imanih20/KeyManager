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
    public TextView notice;
    private DrawerLayout drawerLayout;
    private FloatingActionButton faButton;
    private AddDialog dialog;
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
        NavigationView navigationView = findViewById(R.id.drawer_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
        drawerLayout=findViewById(R.id.drawer_layout);
        AppCompatImageView drawerBtn = findViewById(R.id.drawer_btn);
        navigationView.bringToFront();
        drawerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        //init dbHelper
        dbHelper=new PasswordDbHelper(this);
        passwords=dbHelper.getAllPasswords();
        //init searchBar
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.setPasswordList(dbHelper.searchInPasswords(newText));
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        //init faButton
        faButton=findViewById(R.id.floatingActionButton);
        faButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog=new AddDialog(MainActivity.this,dbHelper,adapter);
                dialog.show(getSupportFragmentManager(),"dialog");
                dialog.setCancelable(false);
            }
        });
        //init nested scroll view
        ScrollView scrollView=findViewById(R.id.nested_scroll_view);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY>scrollX){
                        faButton.hide();
                    }else {
                        faButton.show();
                    }
                }
            });
        }
        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar()!=null;
        getSupportActionBar().setTitle(null);
        //init recyclerView password list
        RecyclerView passwordsList = findViewById(R.id.passwords_list);
        passwordsList.setLayoutManager(new LinearLayoutManager(this));
        passwordsList.setItemAnimator(new DefaultItemAnimator());
        adapter=new PasswordsListAdapter(this,passwords,dbHelper);
        passwordsList.setAdapter(adapter);
        //init notice
        notice=findViewById(R.id.notice);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.updateData((ArrayList<PasswordModel>) dbHelper.getAllPasswords());
        if (passwords.isEmpty()){
            notice.setVisibility(View.VISIBLE);
        }else {
            notice.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent=new Intent(MainActivity.this,SecondActivity.class);
        switch (item.getItemId()){
            case R.id.options_nav:
                intent.putExtra(App.FRAGMENT_TAG,0);
                startActivity(intent);
                break;
            case R.id.about_nav:
                intent.putExtra(App.FRAGMENT_TAG,2);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dialog!=null)
            if (dialog.isVisible()){
                outState.putBoolean("isShow",true);
                dialog.onSaveInstanceState(outState);
            }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean("isShow")) {
            dialog.show(getSupportFragmentManager(),"dialog");
            dialog.onViewStateRestored(savedInstanceState);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
}