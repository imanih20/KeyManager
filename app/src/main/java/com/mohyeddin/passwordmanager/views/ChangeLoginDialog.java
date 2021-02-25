package com.mohyeddin.passwordmanager.views;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;

import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.models.LoginPasswordModel;
import com.mohyeddin.passwordmanager.utils.LoginDbHelper;

public class ChangeLoginDialog extends AppCompatDialogFragment {
    private AppCompatEditText prevPass,newPass,confirmPass,answer;
    private AppCompatCheckBox changePassCheckBox,changeQuesCheckBox;
    private AppCompatSpinner questionsSpinner;
    private Context context;
    private LoginDbHelper dbHelper;

    public ChangeLoginDialog(Context context){
        this.context=context;
        dbHelper=new LoginDbHelper(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.change_login_layout,null,false);
        initContents(view);
        dialogBuilder.setView(view);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(R.string.save_btn_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeLoginContents();
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel_btn_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return dialogBuilder.show();
    }

    private void changeLoginContents() {
        ContentValues values=new ContentValues();
        if (prevPass.getText()==null||prevPass.getText().toString().isEmpty()){
            ColoredToast.alert(context,getResources().getString(R.string.change_pass_error1),ColoredToast.LENGTH_SHORT).show();
        }else {
           String pass= prevPass.getText().toString();
           if(pass.equals(dbHelper.getPassword())){
               if (changePassCheckBox.isChecked() && changeQuesCheckBox.isChecked()) {
                   if (answer.getText()==null||answer.getText().toString().isEmpty()||newPass.getText()==null||newPass.getText().toString().isEmpty()){
                       return;
                   }
                   values.put(LoginPasswordModel.PASSWORD_KEY,newPass.getText().toString());
                   values.put(LoginPasswordModel.ANSWER_KEY,answer.getText().toString());
                   values.put(LoginPasswordModel.QUESTION_KEY, (String) questionsSpinner.getSelectedItem());
               }else if (changeQuesCheckBox.isChecked()&&!changePassCheckBox.isChecked()){
                   if (answer.getText() == null || answer.getText().toString().isEmpty()) {
                       return;
                   }
                   values.put(LoginPasswordModel.ANSWER_KEY,answer.getText().toString());
                   values.put(LoginPasswordModel.QUESTION_KEY, (String) questionsSpinner.getSelectedItem());
               }else if (changePassCheckBox.isChecked()&&!changeQuesCheckBox.isChecked()&&newPass.getText()!=null){
                   values.put(LoginPasswordModel.PASSWORD_KEY,newPass.getText().toString());
               }else {
                   ColoredToast.alert(context,getResources().getString(R.string.change_pass_error2),ColoredToast.LENGTH_SHORT).show();
                   return;
               }
               dbHelper.updateContents(pass,values);
               ColoredToast.success(context,getResources().getString(R.string.save_message),ColoredToast.LENGTH_SHORT).show();
           }
        }
    }

    private void initContents(View view) {
        prevPass=view.findViewById(R.id.prev_pass);
        newPass=view.findViewById(R.id.new_pass);
        confirmPass=view.findViewById(R.id.confirm_pass);
        answer=view.findViewById(R.id.new_answer_et);
        changePassCheckBox=view.findViewById(R.id.change_pass_check_box);
        changeQuesCheckBox=view.findViewById(R.id.change_forget_quest_checkbox);
        questionsSpinner=view.findViewById(R.id.ques_spinner);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(context,R.array.spinner_values,R.layout.spinner_list);
        adapter.setDropDownViewResource(R.layout.spinner_list);
        questionsSpinner.setAdapter(adapter);
        changeQuesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableQuesView(isChecked);
            }
        });
        changePassCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enablePassViews(isChecked);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        enablePassViews(changePassCheckBox.isChecked());
        enableQuesView(changeQuesCheckBox.isChecked());
        try {
            AlertDialog alertDialog= (AlertDialog) getDialog();
            if (alertDialog!=null) {
                if (alertDialog.getWindow() != null)
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_btn_bg2));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_btn_bg));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void enablePassViews(boolean enable){
        newPass.setEnabled(enable);
        confirmPass.setEnabled(enable);
    }
    private void enableQuesView(boolean enable){
        questionsSpinner.setEnabled(enable);
        answer.setEnabled(enable);
    }
}
