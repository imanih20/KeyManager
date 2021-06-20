package com.mohyeddin.passwordmanager.adpaters;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.activities.App;
import com.mohyeddin.passwordmanager.activities.MainActivity;
import com.mohyeddin.passwordmanager.databinding.PasswordsListRowLayoutBinding;
import com.mohyeddin.passwordmanager.fragments.ShowPasswordFragment;
import com.mohyeddin.passwordmanager.models.PasswordModel;
import com.mohyeddin.passwordmanager.utils.PasswordDbHelper;
import com.mohyeddin.passwordmanager.utils.ShowFragmentUtils;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class PasswordsListAdapter extends RecyclerView.Adapter<PasswordsListAdapter.ViewHolder>{
    private List<PasswordModel> passwordModels;
    private final Context context;
    private final PasswordDbHelper dbHelper;
    public PasswordsListAdapter(Context context, List<PasswordModel> passwordModels, PasswordDbHelper dbHelper){
        this.context=context;
        this.passwordModels=passwordModels;
        this.dbHelper=dbHelper;
    }
    public void setPasswordList(List<PasswordModel> list){
        this.passwordModels=list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PasswordsListRowLayoutBinding binding = PasswordsListRowLayoutBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final String password=passwordModels.get(position).getPassWord();
        final int id=passwordModels.get(position).getId();
        final String title=passwordModels.get(position).getTitle();
        holder.binding.titleTv.setText((title.isEmpty())?"<بدون عنوان>": title);
        holder.binding.passwordTv.setText(password);
        holder.binding.copyBtn.setOnClickListener(v -> copyToClipboard(password));
        holder.binding.deleteBtn.setOnClickListener(v -> showDialog(passwordModels.get(position)));
        holder.itemView.setOnClickListener(v -> ShowFragmentUtils.showFragment(
                ((AppCompatActivity)context).getSupportFragmentManager(),
                R.id.louncher_container,
                ShowPasswordFragment.newInstance(id,password,title)));
    }

    private void showDialog(final PasswordModel model) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setMessage(R.string.delete_dialog_message);
        dialog.setPositiveButton(R.string.yes, (dialog12, which) -> {
            dbHelper.deletePassword(model.getId());
            passwordModels.remove(model);
            notifyDataSetChanged();
        });
        dialog.setNegativeButton(R.string.no, (dialog1, which) -> dialog1.dismiss());
        dialog.show();
    }
    private void copyToClipboard(String text){
        ClipboardManager manager= (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData data=ClipData.newPlainText("key",text);
        manager.setPrimaryClip(data);
        Toast.makeText(context,R.string.copy_massage,Toast.LENGTH_SHORT).show();
    }
    @Override
    public int getItemCount() {
        return passwordModels.size();
    }
    public void updateData(ArrayList<PasswordModel> models) {

        passwordModels.clear();
        passwordModels.addAll(models);
        notifyDataSetChanged();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        PasswordsListRowLayoutBinding binding;
        public ViewHolder(@NonNull PasswordsListRowLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
