package com.mohyeddin.passwordmanager.views;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.databinding.ChangeLoginLayoutBinding;
import com.mohyeddin.passwordmanager.models.LoginPasswordModel;
import com.mohyeddin.passwordmanager.utils.LoginDbHelper;

public class ChangeLoginDialog extends AppCompatDialogFragment {
    private ChangeLoginLayoutBinding binding;
    private final Context context;
    private final LoginDbHelper dbHelper;

    public ChangeLoginDialog(Context context){
        this.context=context;
        dbHelper=new LoginDbHelper(context);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = ChangeLoginLayoutBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.changeForgetQuestCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> enableQuesView(isChecked));
        binding.changePassCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> enablePassViews(isChecked));
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(context,R.array.spinner_values,R.layout.spinner_list);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        binding.quesSpinner.setAdapter(adapter);
        enablePassViews(binding.changePassCheckBox.isChecked());
        enableQuesView(binding.changeForgetQuestCheckbox.isChecked());
        binding.saveBtn.setOnClickListener(v -> changeLoginContents());
        binding.cancelButton.setOnClickListener(v -> {
            if (getDialog()!=null){
                getDialog().dismiss();
            }
        });
        try {
            if (getDialog()!=null) {
                if (getDialog().getWindow() != null)
                    getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void changeLoginContents() {
        ContentValues values=new ContentValues();
        if (binding.prevPass.getText()==null||binding.prevPass.getText().toString().isEmpty()){
            ColoredToast.alert(context,getResources().getString(R.string.change_pass_error1),ColoredToast.LENGTH_SHORT).show();
        }else {
           String pass= binding.prevPass.getText().toString();
           if(pass.equals(dbHelper.getPassword())){
               if (binding.changePassCheckBox.isChecked() && binding.changeForgetQuestCheckbox.isChecked()) {
                   if (binding.newAnswerEt.getText()==null||binding.newAnswerEt.getText().toString().isEmpty()||binding.newPass.getText()==null||binding.newPass.getText().toString().isEmpty()){
                       return;
                   }
                   values.put(LoginPasswordModel.PASSWORD_KEY,binding.newPass.getText().toString());
                   values.put(LoginPasswordModel.ANSWER_KEY,binding.newAnswerEt.getText().toString());
                   values.put(LoginPasswordModel.QUESTION_KEY, (String) binding.quesSpinner.getSelectedItem());
               }else if (binding.changeForgetQuestCheckbox.isChecked()&&!binding.changeForgetQuestCheckbox.isChecked()){
                   if (binding.newAnswerEt.getText() == null || binding.newAnswerEt.getText().toString().isEmpty()) {
                       return;
                   }
                   values.put(LoginPasswordModel.ANSWER_KEY,binding.newAnswerEt.getText().toString());
                   values.put(LoginPasswordModel.QUESTION_KEY, (String) binding.quesSpinner.getSelectedItem());
               }else if (binding.changePassCheckBox.isChecked()&&!binding.changeForgetQuestCheckbox.isChecked()&&binding.newPass.getText()!=null){
                   values.put(LoginPasswordModel.PASSWORD_KEY,binding.newPass.getText().toString());
               }else {
                   ColoredToast.alert(context,getResources().getString(R.string.change_pass_error2),ColoredToast.LENGTH_SHORT).show();
                   return;
               }
               dbHelper.updateContents(pass,values);
               ColoredToast.success(context,getResources().getString(R.string.save_message),ColoredToast.LENGTH_SHORT).show();
           }
        }
    }
    private void enablePassViews(boolean enable){
        binding.newPass.setEnabled(enable);
        binding.confirmPass.setEnabled(enable);
    }
    private void enableQuesView(boolean enable){
        binding.quesSpinner.setEnabled(enable);
        binding.newAnswerEt.setEnabled(enable);
    }
}
