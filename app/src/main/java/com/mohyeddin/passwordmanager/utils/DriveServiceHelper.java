package com.mohyeddin.passwordmanager.utils;

import android.app.Activity;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.mohyeddin.passwordmanager.R;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private final Executor executor= Executors.newSingleThreadExecutor();
    private final Drive driveService;
    private final Activity activity;

    public DriveServiceHelper(Drive driveService,Activity activity){
        this.driveService=driveService;
        this.activity=activity;
    }

    public Task<String> createFile(){
          return Tasks.call(executor, () -> {
              final File dbFile=new File();
              dbFile.setName(PasswordDbHelper.DB_NAME);
              java.io.File file=new java.io.File(activity.getDatabasePath(PasswordDbHelper.DB_NAME).toString());
              final FileContent fileContent=new FileContent("application/db",file);
              File datafile = null ;
              try {
                  deleteFiles();
                  datafile = driveService.files().create(dbFile, fileContent).execute();
              }catch (Exception e){
                  e.printStackTrace();
              }
              if (datafile==null) {
                  throw new IOException();
              }
              return datafile.getId();
          });
    }
    public Task<File> getFile(){
        return Tasks.call(executor, () -> {
            FileList list;
            list=driveService.files().list().execute();
            File file=null;
            if (list!=null&&!list.isEmpty()) {
                for (File f : list.getFiles()) {
                    file = f;
                }
                return file;
            }else {
                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                builder.setTitle(R.string.error_title);
                builder.setMessage(R.string.no_file_error);
                builder.setPositiveButton(R.string.close_button_txt, (dialog, which) -> dialog.dismiss());
                builder.show();
                return null;
            }
        });
    }
    public void deleteFiles(){
        FileList list;
        try {
            list = driveService.files().list().execute();
            if (list!=null&&!list.isEmpty()) {
                for (File f : list.getFiles()) {
                    driveService.files().delete(f.getId()).execute();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Task<Boolean> readFile(final String fileId){
        final String infileName=activity.getDatabasePath(PasswordDbHelper.DB_NAME).toString();
        return Tasks.call(executor, () -> {
            try {
                InputStream inputStream = driveService.files().get(fileId).executeMediaAsInputStream();
                OutputStream outputStream = new FileOutputStream(infileName);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        });
    }

}
