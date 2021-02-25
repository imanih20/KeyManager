package com.mohyeddin.passwordmanager.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.models.PasswordModel;
import com.mohyeddin.passwordmanager.utils.ConnectionHelper;
import com.mohyeddin.passwordmanager.utils.DriveServiceHelper;
import com.mohyeddin.passwordmanager.utils.PasswordDbHelper;
import com.mohyeddin.passwordmanager.utils.ThemeHelper;
import com.mohyeddin.passwordmanager.views.ChangeLoginDialog;
import com.mohyeddin.passwordmanager.views.ColoredToast;

import java.util.Collections;
import java.util.List;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    public static final int REQUEST_CODE_SIGN_IN_BACKUP=100;
    public static final int REQUEST_CODE_SIGN_IN_RESTORE=101;
    private CardView nightMode;
    private CardView backupOption;
    private CardView restoreOption;
    private CardView changeOption;
    private AppCompatTextView mode;
    public static final String NIGHT_MODE_KEY="night";
    private DriveServiceHelper driveService;
    private GoogleSignInClient signInClient;

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_setting,container,false);
        nightMode=view.findViewById(R.id.night_mode);
        mode=view.findViewById(R.id.mode);
        backupOption=view.findViewById(R.id.backup);
        restoreOption=view.findViewById(R.id.restore);
        changeOption=view.findViewById(R.id.password_reset);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nightMode.setOnClickListener(this);
        backupOption.setOnClickListener(this);
        restoreOption.setOnClickListener(this);
        changeOption.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getContext()!=null)
            if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(NIGHT_MODE_KEY,false)){
                ThemeHelper.applyTheme(ThemeHelper.DARK_MODE);
                mode.setText(R.string.night_mode);
            }else {
                ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE);
                mode.setText(R.string.day_mode);
            }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQUEST_CODE_SIGN_IN_RESTORE:
                if (resultCode== Activity.RESULT_OK){
                    //backup.connectToDrive(true);
                    handleSignInResult(data,false);
                }
                break;
            case REQUEST_CODE_SIGN_IN_BACKUP:
                if (resultCode== Activity.RESULT_OK){
                    //backup.connectToDrive(true);
                    handleSignInResult(data,true);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if(getActivity()!=null)
            switch (v.getId()){
            case R.id.night_mode:
                if (getContext()!=null)
                    if (!PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(NIGHT_MODE_KEY,false)) {
                        ThemeHelper.applyTheme(ThemeHelper.DARK_MODE);
                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(NIGHT_MODE_KEY, true).apply();
                    } else {
                        ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE);
                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(NIGHT_MODE_KEY, false).apply();
                    }
                break;
            case R.id.backup:
                if (ConnectionHelper.isOnline(getActivity())){
                    requestGoogleSignIn(true);
                }else {
                    showConnectDialog(true);
                }
                break;
            case R.id.restore:
                if (ConnectionHelper.isOnline(getActivity())){
                    requestGoogleSignIn(false);
                }else {
                    showConnectDialog(false);
                }
                break;
            case R.id.password_reset:
                ChangeLoginDialog changeLoginDialog = new ChangeLoginDialog(getContext());
                changeLoginDialog.show(getParentFragmentManager(),"changeLogin");
                changeLoginDialog.setCancelable(false);
        }
    }
    private void exportDb(){
        final ProgressDialog dialog=new ProgressDialog(getContext());
        dialog.setMessage(getResources().getString(R.string.restore_progress));
        dialog.show();
        final PasswordDbHelper dbHelper=new PasswordDbHelper(getContext());
        final List<PasswordModel> passwordList=dbHelper.getAllPasswords();
        driveService.getFile().addOnSuccessListener(new OnSuccessListener<File>() {
            @Override
            public void onSuccess(File file) {
                if (file!=null)
                    driveService.readFile(file.getId()).addOnSuccessListener(new OnSuccessListener<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            if (getContext()!=null){
                                dialog.dismiss();
                                ColoredToast.success(getContext(),getResources().getString(R.string.restore_success_message),ColoredToast.LENGTH_SHORT).show();
                            }
                            if (aBoolean){
                                if (passwordList != null) {
                                    for (int i = 0; i < passwordList.size(); i++) {
                                        PasswordModel model = passwordList.get(i);
                                        dbHelper.insertPassword(model);
                                    }
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (getContext() != null) {
                                dialog.dismiss();
                                ColoredToast.alert(getContext(),getResources().getString(R.string.restore_failure_message),ColoredToast.LENGTH_SHORT).show();
                            }
                        }
                    });
                signInClient.revokeAccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (getContext() != null) {
                    dialog.dismiss();
                    ColoredToast.alert(getContext(),getResources().getString(R.string.restore_failure_message),ColoredToast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showConnectDialog(final boolean isBackup){
        if (getContext()!=null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.connection_dialog_message);
            builder.setPositiveButton(R.string.connection_dialog_wifi_title, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (getContext()!=null)
                        ConnectionHelper.openWifiSettingsScreen(getContext());
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.connection_dialog_data_connect, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (getContext()!=null)
                    ConnectionHelper.openDataUsageScreen(getContext());
                    dialog.dismiss();
                }
            });
            builder.setNeutralButton(R.string.connection_dialog_retry, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (getActivity()!=null)
                    if (!ConnectionHelper.isOnline(getActivity())) {
                        showConnectDialog(isBackup);
                    } else {
                        requestGoogleSignIn(isBackup);
                    }
                }
            });
            builder.show();
        }
    }
    private void requestGoogleSignIn(boolean isBackup){
        GoogleSignInOptions signInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();
        if (getContext()!=null)
            signInClient=GoogleSignIn.getClient(getContext(),signInOptions);
        if (isBackup){
           startActivityForResult(signInClient.getSignInIntent(),REQUEST_CODE_SIGN_IN_BACKUP);
        }else {
            startActivityForResult(signInClient.getSignInIntent(),REQUEST_CODE_SIGN_IN_RESTORE);
        }
    }
    private void handleSignInResult(Intent result, final boolean isBackup) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        if(getActivity()==null)return;
                        GoogleAccountCredential credential =
                                GoogleAccountCredential.usingOAuth2(
                                        getContext(), Collections.singleton(DriveScopes.DRIVE_FILE));
                        credential.setSelectedAccount(googleSignInAccount.getAccount());
                        Drive googleDriveService =
                                new Drive.Builder(
                                        AndroidHttp.newCompatibleTransport(),
                                        JacksonFactory.getDefaultInstance(),
                                        credential)
                                        .setApplicationName("passwordmanager")
                                        .build();
                        driveService=new DriveServiceHelper(googleDriveService,getActivity());
                        if (isBackup){
                            if (ConnectionHelper.isOnline(getActivity())){
                                uploadFile();
                            }else {
                                showConnectDialog(true);
                            }
                        }else {
                            if (ConnectionHelper.isOnline(getActivity())){
                                exportDb();
                            }else {
                                showConnectDialog(true);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (getContext()!=null)
                            ColoredToast.alert(getContext(),getResources().getString(R.string.sign_in_failure_message),ColoredToast.LENGTH_SHORT).show();
                    }
                });
    }
    private void uploadFile(){
        final ProgressDialog dialog=new ProgressDialog(getContext());
        dialog.setMessage(getResources().getString(R.string.backup_progress));
        dialog.show();
        driveService.createFile().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (getContext()!=null) {
                    ColoredToast.success(getContext(), getResources().getString(R.string.upload_file_success_message), ColoredToast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    signInClient.revokeAccess();
                }
                System.out.println(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (getContext()!=null)
                    ColoredToast.alert(getContext(),getResources().getString(R.string.upload_file_failure_message),ColoredToast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}