package com.mohyeddin.passwordmanager.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.models.PasswordModel;
import com.mohyeddin.passwordmanager.utils.PasswordDbHelper;
import com.mohyeddin.passwordmanager.views.EditPasswordDialog;
import com.mohyeddin.passwordmanager.views.EditTitleDialog;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ShowPasswordFragment extends Fragment implements View.OnClickListener {
    private PasswordModel passwordModel;
    private AppCompatTextView titleTv,passwordTv;
    private AppCompatImageButton titleEditBtn,passwordEditBtn,deleteBtn,copyBtn;
    private PasswordDbHelper dbHelper;
    private EditTitleDialog editTitleDialog;
    private EditPasswordDialog editPasswordDialog;
    public static ShowPasswordFragment newInstance(int id,String password,String title) {
        Bundle args = new Bundle();
        args.putInt(PasswordModel.ID_KEY,id);
        args.putString(PasswordModel.PASSWORD_KEY,password);
        args.putString(PasswordModel.TITLE_KEY,title);
        ShowPasswordFragment fragment = new ShowPasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_show,container,false);
        titleTv=view.findViewById(R.id.title_show);
        passwordTv=view.findViewById(R.id.password_show);
        titleEditBtn=view.findViewById(R.id.title_edit_btn);
        passwordEditBtn=view.findViewById(R.id.password_edit_btn);
        deleteBtn=view.findViewById(R.id.delete_btn);
        copyBtn=view.findViewById(R.id.copy_btn);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbHelper=new PasswordDbHelper(getContext());
        if (getArguments()!=null) {
            passwordModel=new PasswordModel();
            passwordModel.setId(getArguments().getInt(PasswordModel.ID_KEY));
            passwordModel.setPassWord(getArguments().getString(PasswordModel.PASSWORD_KEY));
            passwordModel.setTitle(getArguments().getString(PasswordModel.TITLE_KEY));
        }
        if (passwordModel!=null){
            titleTv.setText((passwordModel.getTitle().isEmpty())?"<بدون عنوان>":passwordModel.getTitle());
            passwordTv.setText(passwordModel.getPassWord());
            titleEditBtn.setOnClickListener(this);
            passwordEditBtn.setOnClickListener(this);
            deleteBtn.setOnClickListener(this);
            copyBtn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_edit_btn:
                showEditTitleDialog();
                break;
            case R.id.password_edit_btn:
                showEditPassDialog();
                break;
            case R.id.delete_btn:
                AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                dialog.setMessage(R.string.delete_dialog_message);
                dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deletePassword(passwordModel.getId());
                        if (getActivity()!=null)getActivity().finish();
                    }
                });
                dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.copy_btn:
                if (getContext()!=null) {
                    ClipboardManager manager = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                    ClipData data = ClipData.newPlainText("key", passwordTv.getText());
                    manager.setPrimaryClip(data);
                    Toast.makeText(getContext(), R.string.copy_massage, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void showEditTitleDialog() {
        editTitleDialog=new EditTitleDialog(getContext(),dbHelper,passwordModel);
        editTitleDialog.show(getParentFragmentManager(),"titleDialog");
        editTitleDialog.setTitleTvToEdit(titleTv);
        editTitleDialog.setCancelable(false);
    }

    private void showEditPassDialog(){
        editPasswordDialog=new EditPasswordDialog(getContext(),dbHelper,passwordModel);
        editPasswordDialog.show(getParentFragmentManager(),"passwordDialog");
        editPasswordDialog.setPasswordTv(passwordTv);
        editPasswordDialog.setCancelable(false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (editTitleDialog!=null)
            if (editTitleDialog.isVisible()){
                outState.putBoolean("titleIsShow",true);
                editTitleDialog.onSaveInstanceState(outState);
            }
        if (editPasswordDialog!=null){
            if (editPasswordDialog.isVisible()){
                outState.putBoolean("passIsShow",true);
                editPasswordDialog.onSaveInstanceState(outState);
            }
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState!=null) {
            if (savedInstanceState.getBoolean("titleIsShow")) {
                editTitleDialog.show(getParentFragmentManager(), "titleDialog");
                editTitleDialog.onViewStateRestored(savedInstanceState);
            }
            if (savedInstanceState.getBoolean("passIsShow")){
                editPasswordDialog.show(getParentFragmentManager(),"passwordDialog");
                editPasswordDialog.onViewStateRestored(savedInstanceState);
            }
        }
    }
}
