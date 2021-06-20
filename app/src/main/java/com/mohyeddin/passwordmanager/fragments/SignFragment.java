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
import com.mohyeddin.passwordmanager.databinding.FragmentSignBinding;
import com.mohyeddin.passwordmanager.utils.LoginDbHelper;

public class SignFragment extends Fragment{
    private FragmentSignBinding binding;
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
        binding = FragmentSignBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper=new LoginDbHelper(getContext());
        binding.signBtn.setOnClickListener(v -> {
            if (binding.signPassword.getText() != null && binding.confirmPassword.getText() != null) {
                if (binding.signPassword.getText().length()>=8&&binding.confirmPassword.getText().length()>=8){
                    if (binding.signPassword.getText()!=null)
                        if (binding.signPassword.getText().toString().equals(binding.confirmPassword.getText().toString())){
                            if (getArguments()!=null) {
                                if (!getArguments().getBoolean("sign")) {
                                    dbHelper.updatePassword(binding.signPassword.getText().toString(),getArguments().getString("password"));
                                    startActivity(new Intent(getActivity(), MainActivity.class));
                                    if (getActivity() != null) getActivity().finish();
                                } else {
                                    getParentFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.louncher_container, ChooseFragment.newInstance(binding.signNoticeTv.getText().toString()))
                                            .commit();
                                }
                            }
                        }
                        else {
                            binding.confirmPassword.setError(getResources().getString(R.string.login_error_1));
                        }
                }else {
                    binding.signPassword.setError(getResources().getString(R.string.minimum_password_size_error));
                    binding.confirmPassword.setError(getResources().getString(R.string.minimum_password_size_error));
                }
            }
        });
    }
}
