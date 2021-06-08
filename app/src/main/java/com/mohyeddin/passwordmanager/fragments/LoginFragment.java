package com.mohyeddin.passwordmanager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.activities.MainActivity;
import com.mohyeddin.passwordmanager.utils.LoginDbHelper;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private AppCompatTextView noticeTv;
    private TextInputEditText passwordEditText;
    private AppCompatButton loginBtn;
    private AppCompatTextView forgetPasswordTv;
    private String password;

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_login,container,false);
        noticeTv=view.findViewById(R.id.notice_tv);
        passwordEditText=view.findViewById(R.id.login_password);
        loginBtn=view.findViewById(R.id.login_btn);
        forgetPasswordTv=view.findViewById(R.id.forget_pass_tv);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoginDbHelper dbHelper = new LoginDbHelper(getContext());
        password= dbHelper.getPassword();
        loginBtn.setOnClickListener(this);
        forgetPasswordTv.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:
                if (passwordEditText.getText()!=null){
                    if (passwordEditText.getText().toString().equals(password)){
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        if (getActivity()!=null)getActivity().finish();
                    }else {
                        passwordEditText.setError(getResources().getString(R.string.wrong_password_error));
                    }
                }
                break;
            case R.id.forget_pass_tv:
                getParentFragmentManager().beginTransaction().replace(R.id.louncher_container,ForgetFragment.newInstance()).commit();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (passwordEditText.getText()!=null){
            outState.putString("password",passwordEditText.getText().toString());
            outState.putString("notice",noticeTv.getText().toString());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState!=null) {
            passwordEditText.setText(savedInstanceState.getString("password"));
            noticeTv.setText(savedInstanceState.getString("notice"));
        }
    }
}
