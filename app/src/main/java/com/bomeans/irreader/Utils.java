package com.bomeans.irreader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.widget.Toast;

import java.util.List;
import java.util.Stack;

/**
 * Created by ray on 2016/10/1.
 */

public class Utils {


    public static void sendEmail(Context context, String emailAddress, String subject, String body) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.setData(Uri.parse("mailto:" + emailAddress)); // or just "mailto:" for blank
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        context.startActivity(intent);
    }
}
