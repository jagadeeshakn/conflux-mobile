package com.mifos.objects.noncore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by conflux37 on 9/28/2015.
 */
public class DisplayAlert {
    public static void DisplayAlertMessageWithOkButton(Context context,String msg,String title,int icon,String buttonName)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.stat_sys_warning)
                .setPositiveButton(buttonName, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
