package com.mohyeddin.passwordmanager.views;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
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
import com.mohyeddin.passwordmanager.models.PasswordModel;
import com.mohyeddin.passwordmanager.utils.PasswordDbHelper;
import com.mohyeddin.passwordmanager.utils.PasswordGenerator;

public class EditPasswordDialog extends AppCompatDialogFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private Context context;
    private PasswordDbHelper dbHelper;
    private AppCompatCheckBox number,lowerAlphabet,upperAlphabet,specials,customChoice;
    private AppCompatImageButton refreshBtn;
    private AppCompatTextView edtPasswordTv,passwordTv;
    private AppCompatEditText customPass;
    private PasswordGenerator generator;
    private ValueSelector valueSelector;
    private PasswordModel model;
    private int checkedOptions;
    public EditPasswordDialog(Context context, PasswordDbHelper dbHelper, PasswordModel model){
        this.context=context;
        this.dbHelper=dbHelper;
        this.model=model;
        checkedOptions=2;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(context);
        generator=new PasswordGenerator();
        View view= LayoutInflater.from(context).inflate(R.layout.edit_password_dialog_layout,null,false);
        initContent(view);
        dialogBuilder.setView(view);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(R.string.save_btn_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updatePassword();
                dialog.dismiss();
                ColoredToast.success(context,getResources().getString(R.string.save_message),ColoredToast.LENGTH_SHORT).show();
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
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
    public void setPasswordTv(AppCompatTextView passwordTv){
        this.passwordTv=passwordTv;
    }

    private void updatePassword(){
        String pass="";
        if (customChoice.isChecked()){
            if (customPass.getText() != null) {
                pass=customPass.getText().toString();
            }
        }else {
            pass=edtPasswordTv.getText().toString();
        }
        if (!pass.isEmpty()){
            dbHelper.updatePassword(model.getId(),pass);
        }
        passwordTv.setText(pass);
    }

    private void initContent(View view) {
        number=view.findViewById(R.id.e_numbers);
        lowerAlphabet=view.findViewById(R.id.e_lower_alphabet);
        upperAlphabet=view.findViewById(R.id.e_upper_alphabet);
        specials=view.findViewById(R.id.e_special_chars);
        customChoice=view.findViewById(R.id.e_custom_choice);
        refreshBtn=view.findViewById(R.id.e_refresh_btn);
        edtPasswordTv=view.findViewById(R.id.e_password_view);
        customPass=view.findViewById(R.id.e_custom_password);
        valueSelector=view.findViewById(R.id.e_value_selector);
        lowerAlphabet.setOnCheckedChangeListener(this);
        number.setOnCheckedChangeListener(this);
        upperAlphabet.setOnCheckedChangeListener(this);
        specials.setOnCheckedChangeListener(this);
        refreshBtn.setOnClickListener(this);
        customChoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableViews(!isChecked);
            }
        });
        edtPasswordTv.setText(model.getPassWord());
        edtPasswordTv.setOnClickListener(this);
        valueSelector.setMaxValue(64);
        valueSelector.setMinValue(4);
        valueSelector.setValue(model.getPassWord().length());
        valueSelector.valueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtPasswordTv.setText(generatePassword());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        edtPasswordTv.setText(generatePassword());
        if (isChecked){
            checkedOptions++;
        }else {
            checkedOptions--;
        }
        if (checkedOptions==1){
            if (number.isChecked()){
                number.setEnabled(false);
            }else if (lowerAlphabet.isChecked()){
                lowerAlphabet.setEnabled(false);
            } else if (upperAlphabet.isChecked()) {
                upperAlphabet.setEnabled(false);
            } else if (specials.isChecked()) {
                specials.setEnabled(false);
            }
        }else {
            number.setEnabled(true);
            lowerAlphabet.setEnabled(true);
            upperAlphabet.setEnabled(true);
            specials.setEnabled(true);
        }
    }

    private String generatePassword() {
        return generator.generatePassword(lowerAlphabet.isChecked(),upperAlphabet.isChecked(),number.isChecked(),specials.isChecked(),valueSelector.getValue());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("password",edtPasswordTv.getText().toString());
        outState.putBoolean("lowerAlphabet",lowerAlphabet.isChecked());
        outState.putBoolean("upperAlphabet", upperAlphabet.isChecked());
        outState.putBoolean("numbers",number.isChecked());
        outState.putBoolean("special",specials.isChecked());
        outState.putBoolean("custom",customChoice.isChecked());
        try {
            if (customChoice.isChecked()&&customPass.getText()!=null){
                outState.putString("customPassword",customPass.getText().toString());

            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
    private void enableViews(boolean enabled){
        edtPasswordTv.setEnabled(enabled);
        refreshBtn.setEnabled(enabled);
        lowerAlphabet.setEnabled(enabled);
        upperAlphabet.setEnabled(enabled);
        number.setEnabled(enabled);
        specials.setEnabled(enabled);
        valueSelector.setEnabled(enabled);
        customPass.setEnabled(!enabled);
    }
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState!=null) {
            edtPasswordTv.setText(savedInstanceState.getString("password"));
            lowerAlphabet.setChecked(savedInstanceState.getBoolean("lowerAlphabet"));
            upperAlphabet.setChecked(savedInstanceState.getBoolean("upperAlphabet"));
            number.setChecked(savedInstanceState.getBoolean("numbers"));
            specials.setChecked(savedInstanceState.getBoolean("special"));
            customChoice.setChecked(savedInstanceState.getBoolean("custom"));
            if (customChoice.isChecked()){
                enableViews(false);
                customPass.setText(savedInstanceState.getString("customPassword"));
            }else {
                enableViews(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.e_refresh_btn:
                edtPasswordTv.setText(generatePassword());
                break;
            case R.id.e_password_view:
                ClipboardManager manager= (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data=ClipData.newPlainText("key",edtPasswordTv.getText());
                manager.setPrimaryClip(data);
                Toast.makeText(context,R.string.copy_massage,Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
