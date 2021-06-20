package com.mohyeddin.passwordmanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.mohyeddin.passwordmanager.BuildConfig;
import com.mohyeddin.passwordmanager.databinding.AboutFragmentBinding;

public class AboutFragment extends Fragment {
    private AboutFragmentBinding binding;
    public static AboutFragment newInstance() {
        Bundle args = new Bundle();
        AboutFragment aboutFragment = new AboutFragment();
        aboutFragment.setArguments(args);
        return aboutFragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AboutFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity()!=null) {
            final AppCompatActivity activity= (AppCompatActivity) getActivity();
            activity.setSupportActionBar(binding.toolbar);
            ActionBar actionBar=activity.getSupportActionBar();
            if (actionBar!=null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setTitle(null);
            }
            binding.toolbar.setNavigationOnClickListener(v -> activity.onBackPressed());
        }
        binding.versionCode.setText(BuildConfig.VERSION_NAME);
        setHasOptionsMenu(true);
    }
}
