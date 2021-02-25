package com.mohyeddin.passwordmanager.adpaters;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.service.quicksettings.Tile;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.activities.App;
import com.mohyeddin.passwordmanager.activities.MainActivity;
import com.mohyeddin.passwordmanager.activities.SecondActivity;
import com.mohyeddin.passwordmanager.models.PasswordModel;
import com.mohyeddin.passwordmanager.utils.PasswordDbHelper;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class PasswordsListAdapter extends RecyclerView.Adapter<PasswordsListAdapter.ViewHolder>{
    private List<PasswordModel> passwordModels;
    private Context context;
    private PasswordDbHelper dbHelper;
    private int height;
    public PasswordsListAdapter(Context context, List<PasswordModel> passwordModels, PasswordDbHelper dbHelper){
        this.context=context;
        this.passwordModels=passwordModels;
        this.dbHelper=dbHelper;
    }
    public PasswordsListAdapter(Context context,PasswordDbHelper dbHelper){
        this.context=context;
        this.passwordModels=new ArrayList<>();
        this.dbHelper=dbHelper;
    }
    public void setPasswordList(List<PasswordModel> list){
        this.passwordModels=list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.passwords_list_row_layout,
                parent,false);
        height=view.getHeight();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final String password=passwordModels.get(position).getPassWord();
        final int id=passwordModels.get(position).getId();
        final String title=passwordModels.get(position).getTitle();
        holder.titleTv.setText((title.isEmpty())?"<بدون عنوان>": title);
        holder.passwordTV.setText(password);
        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(password);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(passwordModels.get(position));
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, SecondActivity.class);
                intent.putExtra(App.FRAGMENT_TAG,1);
                intent.putExtra(PasswordModel.ID_KEY,id);
                intent.putExtra(PasswordModel.TITLE_KEY,title);
                intent.putExtra(PasswordModel.PASSWORD_KEY,password);
                context.startActivity(intent);
            }
        });
    }

    private void showDialog(final PasswordModel model) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setMessage(R.string.delete_dialog_message);
        dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deletePassword(model.getId());
                passwordModels.remove(model);
                notifyDataSetChanged();
                if (passwordModels.isEmpty()){
                    ((MainActivity)context).notice.setVisibility(View.VISIBLE);
                }
            }
        });
        dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public int getHeight(){
        return height*getItemCount();
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
        AppCompatTextView titleTv,passwordTV;
        AppCompatImageButton copy,delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv=itemView.findViewById(R.id.title_tv);
            passwordTV=itemView.findViewById(R.id.password_tv);
            delete=itemView.findViewById(R.id.delete_btn);
            copy=itemView.findViewById(R.id.copy_btn);
        }
    }
}
