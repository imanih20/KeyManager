package com.mohyeddin.passwordmanager.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.mohyeddin.passwordmanager.databinding.EditTitleDialogLayoutBinding;
import com.mohyeddin.passwordmanager.models.PasswordModel;
import com.mohyeddin.passwordmanager.utils.PasswordDbHelper;

public class EditTitleDialog extends AppCompatDialogFragment {
    private final PasswordDbHelper dbHelper;
    private EditTitleDialogLayoutBinding binding;
    private final PasswordModel model;
    public EditTitleDialog ( PasswordDbHelper dbHelper,PasswordModel model){
        this.dbHelper=dbHelper;
        this.model=model;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = EditTitleDialogLayoutBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {

            if (getDialog()!=null) {
                if (getDialog().getWindow()!=null)
                    getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
        binding.cancelButton.setOnClickListener(v -> {
            if (getDialog()!=null){
                getDialog().dismiss();
            }
        });
        binding.saveBtn.setOnClickListener(v -> saveTitle());
    }
    private void saveTitle(){
        if (binding.newTitleEditTxt.getText()!=null){
            String title = binding.newTitleEditTxt.getText().toString();
            if (!title.isEmpty()){
                dbHelper.updateTitle(model.getId(),title);
            }
        }
    }
}
