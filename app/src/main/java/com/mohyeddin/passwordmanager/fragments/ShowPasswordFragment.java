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
import androidx.fragment.app.Fragment;
import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.databinding.FragmentShowBinding;
import com.mohyeddin.passwordmanager.models.PasswordModel;
import com.mohyeddin.passwordmanager.utils.PasswordDbHelper;
import com.mohyeddin.passwordmanager.views.EditPasswordDialog;
import com.mohyeddin.passwordmanager.views.EditTitleDialog;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ShowPasswordFragment extends Fragment {
    private FragmentShowBinding binding;
    private PasswordModel passwordModel;
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
        binding = FragmentShowBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper=new PasswordDbHelper(getContext());
        if (getArguments()!=null) {
            passwordModel=new PasswordModel();
            passwordModel.setId(getArguments().getInt(PasswordModel.ID_KEY));
            passwordModel.setPassWord(getArguments().getString(PasswordModel.PASSWORD_KEY));
            passwordModel.setTitle(getArguments().getString(PasswordModel.TITLE_KEY));
        }
        if (passwordModel!=null){
            binding.titleShow.setText((passwordModel.getTitle().isEmpty())?"<بدون عنوان>":passwordModel.getTitle());
            binding.passwordShow.setText(passwordModel.getPassWord());
            binding.titleEditBtn.setOnClickListener(v -> showEditTitleDialog());
            binding.passwordEditBtn.setOnClickListener(v -> showEditPassDialog());
            binding.deleteBtn.setOnClickListener(v -> {
                AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                dialog.setMessage(R.string.delete_dialog_message);
                dialog.setPositiveButton(R.string.yes, (dialog1, which) -> {
                    dbHelper.deletePassword(passwordModel.getId());
                    if (getActivity()!=null)getActivity().finish();
                });
                dialog.setNegativeButton(R.string.no, (dialog12, which) -> dialog12.dismiss());
                dialog.show();
            });
            binding.copyBtn.setOnClickListener(v -> {
                if (getContext()!=null) {
                    ClipboardManager manager = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                    ClipData data = ClipData.newPlainText("key", binding.passwordShow.getText());
                    manager.setPrimaryClip(data);
                    Toast.makeText(getContext(), R.string.copy_massage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showEditTitleDialog() {
        editTitleDialog=new EditTitleDialog(dbHelper,passwordModel);
        editTitleDialog.show(getParentFragmentManager(),"titleDialog");
        editTitleDialog.onDismiss(new DialogInterface() {
            @Override
            public void cancel() {

            }

            @Override
            public void dismiss() {
                requireActivity().recreate();
            }
        });
        editTitleDialog.setCancelable(false);
    }

    private void showEditPassDialog(){
        editPasswordDialog=new EditPasswordDialog(dbHelper,passwordModel);
        editPasswordDialog.show(getParentFragmentManager(),"passwordDialog");
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
