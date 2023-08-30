package com.mxolisi.resmanagement.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.File;
import java.text.NumberFormat;

public class Utils {
    public static int dpToPx(int dp) {
        //return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dp, Resources.getSystem().getDisplayMetrics());
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    public static int pxToDp(int dp) {
        return (int) (dp / Resources.getSystem().getDisplayMetrics().density);
    }

    public static void tempDisable(@NonNull View view){
        view.setEnabled(false);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> view.setEnabled(true), 1000);
    }
    public static void tempDisable(@NonNull View view, long delayMillis){
        view.setEnabled(false);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> view.setEnabled(true), delayMillis);
    }

    public static void runThisAfter(@NonNull Runnable runnable, float seconds) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed( runnable, (long) (seconds*1000L));
    }
    public static void hideKeyboard(@Nullable Context context, @NonNull View view){
        if (context != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @NonNull
    public static String roundDecimalsTo(double number, int decimals) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(decimals);
        return numberFormat.format(number).replace(',','.');
    }
    @NonNull
    public static String fixDecimalsTo(double number, int decimals) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(decimals);
        numberFormat.setMinimumFractionDigits(decimals);
        return numberFormat.format(number).replace(',','.');
    }
    @NonNull
    public static String toRand(double number) {
        return "R"+fixDecimalsTo(number, 2);
    }
    public static double roundTo(double number, int decimals) {
        return Double.parseDouble(roundDecimalsTo(number, decimals));
    }










    @Nullable
    public static File getFilesDirectory(String folderPath) {
        final File directory;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"Traffic Report");
        else
            directory = new File(Environment.getExternalStorageDirectory(), "Traffic Report/Documents");

        File filesDirectory = new File(directory.getPath()+"/"+folderPath);
        if (filesDirectory.exists() || filesDirectory.mkdirs()) {
            return filesDirectory;
        } else {
            return null;
        }
    }
    @Nullable
    public static File getImagesDirectory() {
        final File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"Traffic Report");
        if (directory.exists() || directory.mkdirs()) {
            return directory;
        } else {
            return null;
        }
    }

    private static boolean makeFolderIfNotExists(String folderPath, String folderName) {
        final File directory = new File(getFilesDirectory(folderPath), folderName);
        return directory.exists() || directory.mkdirs();
    }

    private boolean isFileExists(String folderPath, String fileName, int i) {
        if (i > 20) return false;
        File file = (i == 0) ? new File(getFilesDirectory(folderPath), fileName+".pdf") :
                new File(getFilesDirectory(folderPath), fileName+"("+i+").pdf");
        if (file.exists() && file.length() != 0L) {
            if (file.canRead()){
                return true;
            } else {
                return isFileExists(folderPath, fileName, i+1);
            }
        } else {
            return false;
        }
    }
    private boolean isFileExists(String folderPath, String fileName) {
        return isFileExists(folderPath, fileName, 0);
    }

}
