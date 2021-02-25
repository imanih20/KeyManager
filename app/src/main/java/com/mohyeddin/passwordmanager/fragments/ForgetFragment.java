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
import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.models.LoginPasswordModel;
import com.mohyeddin.passwordmanager.utils.LoginDbHelper;

public class ForgetFragment extends Fragment implements View.OnClickListener {
    private AppCompatTextView questionTV;
    private AppCompatEditText answerET;
    private AppCompatButton checkBtn;
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
        View view=inflater.inflate(R.layout.fragment_forget,container,false );
        questionTV=view.findViewById(R.id.question_tv);
        answerET=view.findViewById(R.id.answer_edit_txt);
        checkBtn=view.findViewById(R.id.check_btn);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoginDbHelper dbHelper = new LoginDbHelper(getContext());
        model= dbHelper.getLoginPassword();
        questionTV.setText(model.getForgetPassQuestion());
        checkBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (answerET.getText()!=null&&!answerET.getText().toString().trim().isEmpty()){
            if (model.getForgetPassAnswer().equals(answerET.getText().toString())){
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.louncher_container,SignFragment.newInstance(false,model.getPassword()))
                        .commit();
            }else {
                answerET.setError(getResources().getString(R.string.answer_doesnt_match));
            }
        }else {
            answerET.setError(getResources().getString(R.string.answer_edit_text_error));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (answerET.getText() != null) {
            outState.putString("answer",answerET.getText().toString());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            answerET.setText(savedInstanceState.getString("answer"));
        }
    }
}
