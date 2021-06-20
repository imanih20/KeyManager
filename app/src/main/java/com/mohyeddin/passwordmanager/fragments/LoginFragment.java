package com.mohyeddin.passwordmanager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.activities.MainActivity;
import com.mohyeddin.passwordmanager.databinding.FragmentLoginBinding;
import com.mohyeddin.passwordmanager.utils.LoginDbHelper;
import com.mohyeddin.passwordmanager.utils.ShowFragmentUtils;

public class LoginFragment extends Fragment {
    private String password;
    private FragmentLoginBinding binding;

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginDbHelper dbHelper = new LoginDbHelper(getContext());
        password = dbHelper.getPassword();
        binding.loginBtn.setOnClickListener(v -> {
            if (binding.loginPassword.getText() != null) {
                if (binding.loginPassword.getText().toString().equals(password)) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    if (getActivity() != null) getActivity().finish();
                } else {
                    binding.loginPassword.setError(getResources().getString(R.string.wrong_password_error));
                }
            }
        });
        binding.forgetPassTv.setOnClickListener(v -> ShowFragmentUtils.showFragment(getParentFragmentManager(),R.id.louncher_container,ForgetFragment.newInstance()));

    }
}
