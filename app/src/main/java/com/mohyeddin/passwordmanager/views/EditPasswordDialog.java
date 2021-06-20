package com.mohyeddin.passwordmanager.views;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Binder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.databinding.EditPasswordDialogLayoutBinding;
import com.mohyeddin.passwordmanager.models.PasswordModel;
import com.mohyeddin.passwordmanager.utils.PasswordDbHelper;
import com.mohyeddin.passwordmanager.utils.PasswordGenerator;

public class EditPasswordDialog extends AppCompatDialogFragment implements CompoundButton.OnCheckedChangeListener {
    private final PasswordDbHelper dbHelper;
    private PasswordGenerator generator;
    private EditPasswordDialogLayoutBinding binding;
    private final PasswordModel model;
    private int checkedOptions;
    public EditPasswordDialog( PasswordDbHelper dbHelper, PasswordModel model){
        this.dbHelper=dbHelper;
        this.model=model;
        checkedOptions=2;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = EditPasswordDialogLayoutBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        generator=new PasswordGenerator();
        try {
            if (getDialog()!=null) {
                if (getDialog().getWindow() != null)
                    getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        binding.eLowerAlphabet.setOnCheckedChangeListener(this);
        binding.eNumbers.setOnCheckedChangeListener(this);
        binding.eUpperAlphabet.setOnCheckedChangeListener(this);
        binding.eSpecialChars.setOnCheckedChangeListener(this);
        binding.saveBtn.setOnClickListener(v -> {
            updatePassword();
            if (getDialog()!=null){
                getDialog().dismiss();
            }
        });
        binding.eRefreshBtn.setOnClickListener(v -> binding.ePasswordView.setText(generatePassword()));
        binding.eCustomChoice.setOnCheckedChangeListener((buttonView, isChecked) -> enableViews(!isChecked));
        binding.ePasswordView.setText(model.getPassWord());
        binding.ePasswordView.setOnClickListener(v -> {
            ClipboardManager manager= (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData data=ClipData.newPlainText("key",binding.ePasswordView.getText());
            manager.setPrimaryClip(data);
            Toast.makeText(requireContext(),R.string.copy_massage,Toast.LENGTH_SHORT).show();
        });
        binding.eValueSelector.setMaxValue(64);
        binding.eValueSelector.setMinValue(4);
        binding.eValueSelector.setValue(model.getPassWord().length());
        binding.eValueSelector.valueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.ePasswordView.setText(generatePassword());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updatePassword(){
        String pass="";
        if (binding.eCustomChoice.isChecked()){
            if (binding.eCustomPassword.getText() != null) {
                pass=binding.eCustomPassword.getText().toString();
            }
        }else {
            pass=binding.ePasswordView.getText().toString();
        }
        if (!pass.isEmpty()){
            dbHelper.updatePassword(model.getId(),pass);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        binding.ePasswordView.setText(generatePassword());
        if (isChecked){
            checkedOptions++;
        }else {
            checkedOptions--;
        }
        if (checkedOptions==1){
            if (binding.eNumbers.isChecked()){
                binding.eNumbers.setEnabled(false);
            }else if (binding.eLowerAlphabet.isChecked()){
                binding.eLowerAlphabet.setEnabled(false);
            } else if (binding.eUpperAlphabet.isChecked()) {
                binding.eUpperAlphabet.setEnabled(false);
            } else if (binding.eSpecialChars.isChecked()) {
                binding.eSpecialChars.setEnabled(false);
            }
        }else {
            binding.eNumbers.setEnabled(true);
            binding.eLowerAlphabet.setEnabled(true);
            binding.eUpperAlphabet.setEnabled(true);
            binding.eSpecialChars.setEnabled(true);
        }
    }

    private String generatePassword() {
        return generator.generatePassword(
                binding.eLowerAlphabet.isChecked(),
                binding.eUpperAlphabet.isChecked(),
                binding.eNumbers.isChecked(),
                binding.eSpecialChars.isChecked(),
                binding.eValueSelector.getValue()
        );
    }

    private void enableViews(boolean enabled){
        binding.ePasswordView.setEnabled(enabled);
        binding.eRefreshBtn.setEnabled(enabled);
        binding.eLowerAlphabet.setEnabled(enabled);
        binding.eUpperAlphabet.setEnabled(enabled);
        binding.eNumbers.setEnabled(enabled);
        binding.eSpecialChars.setEnabled(enabled);
        binding.eValueSelector.setEnabled(enabled);
        binding.eCustomPassword.setEnabled(!enabled);
    }

}
