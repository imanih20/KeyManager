package com.mohyeddin.passwordmanager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.activities.MainActivity;
import com.mohyeddin.passwordmanager.models.LoginPasswordModel;
import com.mohyeddin.passwordmanager.utils.LoginDbHelper;

public class ChooseFragment extends Fragment {
    private AppCompatSpinner chooseSpinner;
    private TextInputEditText answerET;
    private AppCompatButton signBtn;
    private LoginDbHelper dbHelper;

    public static ChooseFragment newInstance(String password) {
        Bundle args = new Bundle();
        args.putString("password",password);
        ChooseFragment fragment = new ChooseFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_choose,container, false);
        chooseSpinner=view.findViewById(R.id.choose_spinner);
        answerET=view.findViewById(R.id.answer);
        signBtn=view.findViewById(R.id.fragment_choose_btn);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbHelper=new LoginDbHelper(getContext());
        assert getContext()!=null;
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getContext(),R.array.spinner_values,R.layout.spinner_list);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        chooseSpinner.setAdapter(adapter);
        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerET.getText() != null&&!answerET.getText().toString().trim().isEmpty()) {
                    LoginPasswordModel model=new LoginPasswordModel();
                    model.setForgetPassAnswer(answerET.getText().toString());
                    model.setForgetPassQuestion(chooseSpinner.getSelectedItem()+":");
                    if (getArguments()!=null){
                        model.setPassword(getArguments().getString("password"));
                    }
                    if (dbHelper.insertLoginPassword(model)){
                        Intent intent=new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        if (getActivity()!=null)getActivity().finish();
                    }
                } else {
                    answerET.setError(getResources().getString(R.string.answer_edit_text_error));
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (answerET.getText() != null) {
            outState.putInt("quest",chooseSpinner.getSelectedItemPosition());
            outState.putString("answer",answerET.getText().toString());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            chooseSpinner.setSelection(savedInstanceState.getInt("quest"));
            answerET.setText(savedInstanceState.getString("answer"));
        }
    }
}
