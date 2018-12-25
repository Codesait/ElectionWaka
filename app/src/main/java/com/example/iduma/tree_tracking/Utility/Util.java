package com.example.iduma.tree_tracking.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Patterns;

import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.NumberFormat;
import java.util.Locale;

public class Util {

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectMgr;
        connectMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectMgr.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void toastMessage(Context context, String msg) {
        MDToast.makeText(context,msg,
                MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
    }

    public boolean isValidEmail(Context context, CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    public boolean isValidPhoneNumber(Context context, String mobile) {
        String regEx = "^[0-9]{11}$";
        return mobile.matches(regEx);
    }

    public String getFormatedAmount(double amount){
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }
}
