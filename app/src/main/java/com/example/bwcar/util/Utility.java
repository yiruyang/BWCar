package com.example.bwcar.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/10/19.
 */

public class Utility {

    public static void showToast(Context context, CharSequence message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public static void showDialog(Context context, String title, String message){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("好的",null)
                .create();
        dialog.show();
    }

    public static void showProgressDialog(Context context, String title, String message){
        ProgressDialog dialog = new ProgressDialog(context);
        if (title != null){
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.show();
        }
    }
}
