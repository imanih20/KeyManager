package com.mohyeddin.passwordmanager.fragments;

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
import com.mohyeddin.passwordmanager.databinding.FragmentForgetBinding;
import com.mohyeddin.passwordmanager.models.LoginPasswordModel;
import com.mohyeddin.passwordmanager.utils.LoginDbHelper;

public class ForgetFragment extends Fragment implements View.OnClickListener {
    private FragmentForgetBinding  binding;
    private LoginPasswordModel model;
    public static ForgetFragment newInstance() {
        Bundle args = new Bundle();
        ForgetFragment fragment = new ForgetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForgetBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoginDbHelper dbHelper = new LoginDbHelper(getContext());
        model= dbHelper.getLoginPassword();
        binding.questionTv.setText(model.getForgetPassQuestion());
        binding.checkBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (binding.answerEditTxt.getText()!=null&&!binding.answerEditTxt.getText().toString().trim().isEmpty()){
            if (model.getForgetPassAnswer().equals(binding.answerEditTxt.getText().toString())){
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.louncher_container,SignFragment.newInstance(false,model.getPassword()))
                        .commit();
            }else {
                binding.answerEditTxt.setError(getResources().getString(R.string.answer_doesnt_match));
            }
        }else {
            binding.answerEditTxt.setError(getResources().getString(R.string.answer_edit_text_error));
        }
    }

}
