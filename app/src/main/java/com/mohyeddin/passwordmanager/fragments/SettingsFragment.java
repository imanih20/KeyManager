package com.mohyeddin.passwordmanager.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.mohyeddin.passwordmanager.R;
import com.mohyeddin.passwordmanager.databinding.FragmentSettingBinding;
import com.mohyeddin.passwordmanager.models.PasswordModel;
import com.mohyeddin.passwordmanager.utils.ConnectionHelper;
import com.mohyeddin.passwordmanager.utils.DriveServiceHelper;
import com.mohyeddin.passwordmanager.utils.PasswordDbHelper;
import com.mohyeddin.passwordmanager.utils.ThemeHelper;
import com.mohyeddin.passwordmanager.views.ChangeLoginDialog;
import com.mohyeddin.passwordmanager.views.ColoredToast;

import java.util.Collections;
import java.util.List;

public class SettingsFragment extends Fragment {
    private ActivityResultLauncher<Intent> backUpResultLauncher;
    private ActivityResultLauncher<Intent> restoreResultLauncher;
    private FragmentSettingBinding binding;
    private DriveServiceHelper driveService;
    private GoogleSignInClient signInClient;
    private ThemeHelper themeHelper;
    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backUpResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult()
                , result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        handleSignInResult(result.getData(),true);
                    }
                });
        restoreResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult()
                , result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        handleSignInResult(result.getData(),false);
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater);
        themeHelper = new ThemeHelper(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.nightMode.setOnClickListener(v -> themeHelper.switchMode());
        binding.backup.setOnClickListener(v -> {
            if (getActivity() != null)
                if (ConnectionHelper.isOnline(getActivity())){
                    requestGoogleSignIn(true);
                }else {
                    showConnectDialog(true);
                }
        });
        binding.restore.setOnClickListener(v -> {
            if (getActivity()!=null)
                if (ConnectionHelper.isOnline(getActivity())){
                    requestGoogleSignIn(false);
                }else {
                    showConnectDialog(false);
                }
        });
        binding.passwordReset.setOnClickListener(v -> {
            ChangeLoginDialog changeLoginDialog = new ChangeLoginDialog(getContext());
            changeLoginDialog.show(getParentFragmentManager(),"changeLogin");
            changeLoginDialog.setCancelable(false);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (themeHelper.getMode() == UiModeManager.MODE_NIGHT_YES){
            binding.mode.setText(R.string.night_mode);
        }else {
            binding.mode.setText(R.string.day_mode);
        }
    }


    private void exportDb(){
        final ProgressDialog dialog=new ProgressDialog(getContext());
        dialog.setMessage(getResources().getString(R.string.restore_progress));
        dialog.show();
        final PasswordDbHelper dbHelper=new PasswordDbHelper(getContext());
        final List<PasswordModel> passwordList=dbHelper.getAllPasswords();
        driveService.getFile().addOnSuccessListener(file -> {
            if (file!=null)
                driveService.readFile(file.getId()).addOnSuccessListener(aBoolean -> {
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
                }).addOnFailureListener(e -> {
                    if (getContext() != null) {
                        dialog.dismiss();
                        ColoredToast.alert(getContext(),getResources().getString(R.string.restore_failure_message),ColoredToast.LENGTH_SHORT).show();
                    }
                });
            signInClient.revokeAccess();
        }).addOnFailureListener(e -> {
            if (getContext() != null) {
                dialog.dismiss();
                ColoredToast.alert(getContext(),getResources().getString(R.string.restore_failure_message),ColoredToast.LENGTH_SHORT).show();
            }
        });
    }
    private void showConnectDialog(final boolean isBackup){
        if (getContext()!=null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.connection_dialog_message);
            builder.setPositiveButton(R.string.connection_dialog_wifi_title, (dialog, which) -> {
                if (getContext()!=null)
                    ConnectionHelper.openWifiSettingsScreen(getContext());
                dialog.dismiss();
            });
            builder.setNegativeButton(R.string.connection_dialog_data_connect, (dialog, which) -> {
                if (getContext()!=null)
                ConnectionHelper.openDataUsageScreen(getContext());
                dialog.dismiss();
            });
            builder.setNeutralButton(R.string.connection_dialog_retry, (dialog, which) -> {
                if (getActivity()!=null)
                if (!ConnectionHelper.isOnline(getActivity())) {
                    showConnectDialog(isBackup);
                } else {
                    requestGoogleSignIn(isBackup);
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
           backUpResultLauncher.launch(signInClient.getSignInIntent());
        }else {
            restoreResultLauncher.launch(signInClient.getSignInIntent());
        }
    }
    private void handleSignInResult(Intent result, final boolean isBackup) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleSignInAccount -> {
                    if(getActivity()==null)return;
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    getContext(), Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleSignInAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(),credential)
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
                })
                .addOnFailureListener(e -> {
                    if (getContext()!=null)
                        ColoredToast.alert(getContext(),getResources().getString(R.string.sign_in_failure_message),ColoredToast.LENGTH_SHORT).show();
                });
    }
    private void uploadFile(){
        final ProgressDialog dialog=new ProgressDialog(getContext());
        dialog.setMessage(getResources().getString(R.string.backup_progress));
        dialog.show();
        driveService.createFile().addOnSuccessListener(s -> {
            if (getContext()!=null) {
                ColoredToast.success(getContext(), getResources().getString(R.string.upload_file_success_message), ColoredToast.LENGTH_SHORT).show();
                dialog.dismiss();
                signInClient.revokeAccess();
            }
            System.out.println(s);
        }).addOnFailureListener(e -> {
            if (getContext()!=null)
                ColoredToast.alert(getContext(),getResources().getString(R.string.upload_file_failure_message),ColoredToast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }
}