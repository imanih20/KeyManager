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
import com.mohyeddin.passwordmanager.databinding.FragmentChooseBinding;
import com.mohyeddin.passwordmanager.models.LoginPasswordModel;
import com.mohyeddin.passwordmanager.utils.LoginDbHelper;

public class ChooseFragment extends Fragment {
    private FragmentChooseBinding binding;
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
        binding = FragmentChooseBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper=new LoginDbHelper(getContext());
        assert getContext()!=null;
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getContext(),R.array.spinner_values,R.layout.spinner_list);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        binding.chooseSpinner.setAdapter(adapter);
        binding.fragmentChooseBtn.setOnClickListener(v -> {
            if (binding.answer.getText() != null&&!binding.answer.getText().toString().trim().isEmpty()) {
                LoginPasswordModel model=new LoginPasswordModel();
                model.setForgetPassAnswer(binding.answer.getText().toString());
                model.setForgetPassQuestion(binding.chooseSpinner.getSelectedItem()+":");
                if (getArguments()!=null){
                    model.setPassword(getArguments().getString("password"));
                }
                if (dbHelper.insertLoginPassword(model)){
                    Intent intent=new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    if (getActivity()!=null)getActivity().finish();
                }
            } else {
                binding.answer.setError(getResources().getString(R.string.answer_edit_text_error));
            }
        });
    }
}
