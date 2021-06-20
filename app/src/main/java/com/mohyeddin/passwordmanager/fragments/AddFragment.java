package com.mohyeddin.passwordmanager.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mohyeddin.passwordmanager.databinding.AddFragmentBinding;

public class AddFragment extends Fragment {
    private AddFragmentBinding binding;
    public static AddFragment newInstance() {
        Bundle args = new Bundle();
        AddFragment fragment = new AddFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = AddFragmentBinding.inflate(inflater);
        return binding.getRoot();
    }
}
