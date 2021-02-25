package com.mohyeddin.passwordmanager.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.models.PasswordModel;
import com.mohyeddin.passwordmanager.utils.PasswordDbHelper;

public class EditTitleDialog extends AppCompatDialogFragment {
    private Context context;
    private PasswordDbHelper dbHelper;
    private AppCompatEditText editTitle;
    private PasswordModel model;
    private AppCompatTextView titleTv;
    public EditTitleDialog (Context context, PasswordDbHelper dbHelper,PasswordModel model){
        this.context=context;
        this.dbHelper=dbHelper;
        this.model=model;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.edit_title_dialog_layout,null,false);
        initContent(view);
        builder.setView(view);
        builder.setNegativeButton(R.string.cancel_btn_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.save_btn_title, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editTitle.getText()!=null){
                    if (dbHelper.updateTitle(model.getId(),editTitle.getText().toString())){
                        dialog.dismiss();
                        ColoredToast.success(context,getResources().getString(R.string.save_message),ColoredToast.LENGTH_SHORT).show();
                        titleTv.setText((editTitle.getText().toString().isEmpty())?"<بدون عنوان>":editTitle.getText());
                    }else {
                        ColoredToast.alert(context,getResources().getString(R.string.upload_file_failure_message),ColoredToast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        return builder.show();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            AlertDialog alertDialog= (AlertDialog) getDialog();
            if (alertDialog!=null) {
                if (alertDialog.getWindow()!=null)
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_btn_bg2));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackground(ContextCompat.getDrawable(context, R.drawable.dialog_btn_bg));
            }
        }catch (NullPointerException e){
            Toast.makeText(context,"wrong",Toast.LENGTH_SHORT).show();
        }
    }
    private void initContent(View view) {
        editTitle=view.findViewById(R.id.new_title_edit_txt);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (editTitle.getText() != null) {
            outState.putString("title",editTitle.getText().toString());
        }
    }
    public void setTitleTvToEdit(AppCompatTextView titleTv){
        this.titleTv=titleTv;
    }
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            editTitle.setText(savedInstanceState.getString("title"));
        }
    }
}
