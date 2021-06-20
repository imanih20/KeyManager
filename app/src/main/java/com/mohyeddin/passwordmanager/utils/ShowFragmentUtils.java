package com.mohyeddin.passwordmanager.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class ShowFragmentUtils {
    public static void showFragment(FragmentManager manager, int container, Fragment fragment){
        manager.beginTransaction().replace(container,fragment).commit();
    }
}
