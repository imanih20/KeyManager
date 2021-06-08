package com.mohyeddin.passwordmanager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.activities.MainActivity;
import com.mohyeddin.passwordmanager.utils.LoginDbHelper;

public class SignFragment extends Fragment implements  View.OnClickListener {
    private TextInputEditText passwordET;
    private TextInputEditText confirmET;
    private AppCompatButton signBtn;
    private LoginDbHelper dbHelper;

    public static SignFragment newInstance(boolean isSigned,String password) {
        Bundle args = new Bundle();
        args.putBoolean("sign",isSigned);
        args.putString("password",password);
        SignFragment fragment = new SignFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign,container,false);
        passwordET=view.findViewById(R.id.sign_password);
        confirmET=view.findViewById(R.id.confirm_password);
        signBtn=view.findViewById(R.id.sign_btn);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbHelper=new LoginDbHelper(getContext());
        signBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (passwordET.getText() != null && confirmET.getText() != null) {
            if (passwordET.getText().length()>=8&&confirmET.getText().length()>=8){
                if (passwordET.getText()!=null)
                    if (passwordET.getText().toString().equals(confirmET.getText().toString())){
                        if (getArguments()!=null) {
                            if (!getArguments().getBoolean("sign")) {
                                dbHelper.updatePassword(passwordET.getText().toString(),getArguments().getString("password"));
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                if (getActivity() != null) getActivity().finish();
                            } else {
                                getParentFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.louncher_container, ChooseFragment.newInstance(passwordET.getText().toString()))
                                        .commit();
                            }
                        }
                    }
                    else {
                        confirmET.setError(getResources().getString(R.string.login_error_1));
                    }
            }else {
                passwordET.setError(getResources().getString(R.string.minimum_password_size_error));
                confirmET.setError(getResources().getString(R.string.minimum_password_size_error));
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (passwordET.getText() != null && confirmET.getText() != null) {
            outState.putString("password",passwordET.getText().toString());
            outState.putString("confirm",confirmET.getText().toString());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            passwordET.setText(savedInstanceState.getString("password"));
            confirmET.setText(savedInstanceState.getString("confirm"));
        }
    }
}
