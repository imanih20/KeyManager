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
import android.text.method.ScrollingMovementMethod;
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
import com.mohyeddin.passwordmanager.activities.MainActivity;
import com.mohyeddin.passwordmanager.adpaters.PasswordsListAdapter;
import com.mohyeddin.passwordmanager.models.PasswordModel;
import com.mohyeddin.passwordmanager.utils.PasswordDbHelper;
import com.mohyeddin.passwordmanager.utils.PasswordGenerator;
import java.util.ArrayList;

public class AddDialog extends AppCompatDialogFragment implements CompoundButton.OnCheckedChangeListener {
    private AppCompatCheckBox lowerAlphabet,upperAlphabet,numbers,specialChars,customChoice;
    private ValueSelector valueSelector;
    private AppCompatTextView passwordTv;
    private AppCompatImageButton refreshButton;
    private AppCompatEditText titleEditText,customPasswordEditText;
    private PasswordGenerator generator;
    private Context context;
    private PasswordDbHelper dbHelper;
    private PasswordsListAdapter adapter;
    private AppCompatTextView noticeTv;
    private int checkedOptions;
    public AddDialog(Context context, PasswordDbHelper dbHelper, PasswordsListAdapter adapter){
        this.context=context;
        this.dbHelper=dbHelper;
        this.adapter=adapter;
        noticeTv= (AppCompatTextView) ((MainActivity)context).notice;
        generator=new PasswordGenerator();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //init password
        String passwordText;
        passwordText=generator.generatePassword(true,false,true,false,8);
        checkedOptions=2;
        final androidx.appcompat.app.AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.add_dialog_layout,null,false);
        initializeLayoutContents(view);
        passwordTv.setText(passwordText);
        dialog.setView(view);
        dialog.setPositiveButton(R.string.save_btn_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int which) {
                savePassword(d);
            }
        });

        dialog.setNegativeButton(R.string.cancel_btn_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return dialog.show();
    }

    private void savePassword(DialogInterface d) {
        PasswordModel model=new PasswordModel();
        if (titleEditText.getText()!=null)
            model.setTitle(titleEditText.getText().toString());
        if (customChoice.isChecked()){
            if (customPasswordEditText.getText()!=null){
                model.setPassWord(customPasswordEditText.getText().toString());
            }
        }else {
            model.setPassWord(passwordTv.getText().toString());
        }
        if (!model.getTitle().isEmpty()){
            if (!model.getPassWord().isEmpty()){
                dbHelper.insertPassword(model);
                d.dismiss();
                ColoredToast.success(context,getResources().getString(R.string.save_message),ColoredToast.LENGTH_SHORT).show();
            }else {
                d.dismiss();
                ColoredToast.alert(context,getResources().getString(R.string.password_error),ColoredToast.LENGTH_SHORT).show();
            }
        }else {
            if (!model.getPassWord().isEmpty()){
                dbHelper.insertPassword(model);
                d.dismiss();
                ColoredToast.warning(context,getResources().getString(R.string.title_warning),ColoredToast.LENGTH_SHORT).show();
            }else {
                d.dismiss();
                ColoredToast.alert(context,getResources().getString(R.string.password_error),ColoredToast.LENGTH_SHORT).show();
            }
        }
        adapter.updateData((ArrayList<PasswordModel>) dbHelper.getAllPasswords());
        noticeTv.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            if (getDialog()!=null&&getDialog().getWindow()!=null)
                getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            AlertDialog alertDialog= (AlertDialog) getDialog();
            if (alertDialog!=null) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_btn_bg2));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_btn_bg));
            }
        }catch (NullPointerException ignored){
        }
    }

    private void initializeLayoutContents(View view) {
        //init dialog views
        lowerAlphabet=view.findViewById(R.id.lower_alphabet);
        upperAlphabet=view.findViewById(R.id.upper_alphabet);
        numbers=view.findViewById(R.id.numbers);
        specialChars=view.findViewById(R.id.special_chars);
        valueSelector=view.findViewById(R.id.value_selector);
        titleEditText=view.findViewById(R.id.title_edit_text);
        passwordTv=view.findViewById(R.id.password_view);
        customChoice=view.findViewById(R.id.custom_choice);
        customPasswordEditText=view.findViewById(R.id.custom_password);
        passwordTv.setMovementMethod(new ScrollingMovementMethod());
        refreshButton=view.findViewById(R.id.refresh_btn);
        valueSelector.setMaxValue(64);
        valueSelector.setMinValue(4);
        valueSelector.setValue(8);
        //set dialog views action
        passwordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager= (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data=ClipData.newPlainText("key",passwordTv.getText());
                manager.setPrimaryClip(data);
                Toast.makeText(context,R.string.copy_massage,Toast.LENGTH_SHORT).show();
            }
        });
        valueSelector.valueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordTv.setText(generatePassword());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        customChoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableViews(!isChecked);
            }
        });
        upperAlphabet.setOnCheckedChangeListener(this);
        lowerAlphabet.setOnCheckedChangeListener(this);
        numbers.setOnCheckedChangeListener(this);
        specialChars.setOnCheckedChangeListener(this);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordTv.setText(generatePassword());
            }
        });
    }

    private void enableViews(boolean enabled){
        passwordTv.setEnabled(enabled);
        refreshButton.setEnabled(enabled);
        lowerAlphabet.setEnabled(enabled);
        upperAlphabet.setEnabled(enabled);
        numbers.setEnabled(enabled);
        specialChars.setEnabled(enabled);
        valueSelector.setEnabled(enabled);
        customPasswordEditText.setEnabled(!enabled);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        passwordTv.setText(generatePassword());
        if (isChecked){
            checkedOptions++;
        }else {
            checkedOptions--;
        }
        if (checkedOptions==1){
            if (numbers.isChecked()){
                numbers.setEnabled(false);
            }else if (lowerAlphabet.isChecked()){
                lowerAlphabet.setEnabled(false);
            } else if (upperAlphabet.isChecked()) {
                upperAlphabet.setEnabled(false);
            } else if (specialChars.isChecked()) {
                specialChars.setEnabled(false);
            }
        }else {
            numbers.setEnabled(true);
            lowerAlphabet.setEnabled(true);
            upperAlphabet.setEnabled(true);
            specialChars.setEnabled(true);
        }
    }

    private String generatePassword() {
        return generator.generatePassword(lowerAlphabet.isChecked(),upperAlphabet.isChecked(),numbers.isChecked(),specialChars.isChecked(),valueSelector.getValue());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("password",passwordTv.getText().toString());
        outState.putBoolean("lowerAlphabet",lowerAlphabet.isChecked());
        outState.putBoolean("upperAlphabet", upperAlphabet.isChecked());
        outState.putBoolean("numbers",numbers.isChecked());
        outState.putBoolean("special",specialChars.isChecked());
        outState.putBoolean("custom",customChoice.isChecked());
        try {
            if (customChoice.isChecked()&&customPasswordEditText.getText()!=null){
                outState.putString("customPassword",customPasswordEditText.getText().toString());

            }
            if (titleEditText.getText()!=null)
                outState.putString("title",titleEditText.getText().toString());
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState!=null) {
            passwordTv.setText(savedInstanceState.getString("password"));
            lowerAlphabet.setChecked(savedInstanceState.getBoolean("lowerAlphabet"));
            upperAlphabet.setChecked(savedInstanceState.getBoolean("upperAlphabet"));
            numbers.setChecked(savedInstanceState.getBoolean("numbers"));
            specialChars.setChecked(savedInstanceState.getBoolean("special"));
            customChoice.setChecked(savedInstanceState.getBoolean("custom"));
            if (customChoice.isChecked()){
                enableViews(false);
                customPasswordEditText.setText(savedInstanceState.getString("customPassword"));
            }else {
                enableViews(true);
            }
            titleEditText.setText(savedInstanceState.getString("title"));
        }
    }
}
