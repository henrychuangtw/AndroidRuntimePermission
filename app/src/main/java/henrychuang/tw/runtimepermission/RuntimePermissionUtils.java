package henrychuang.tw.runtimepermission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by henry.chuang on 2016/5/27.
 */
public class RuntimePermissionUtils {
    public static void showRationale(Context context, String permission, int requestCode, String message){
        showRationale(context, null, permission, requestCode, message);
    }
    public static void showRationale(Context context, String permission, int requestCode, String message, boolean showAppSetting){
        showRationale(context, null, permission, requestCode, message, showAppSetting);
    }
    public static void showRationale(Context context, Fragment fragment, String permission, int requestCode, String message){
        showRationale(context, fragment, permission, requestCode, message, false);
    }
    public static void showRationale(final Context context, final Fragment fragment, final String permission, final int requestCode, String message, final boolean showAppSetting){
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(fragment == null) {
                            if(showAppSetting){
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                intent.setData(uri);
                                ((Activity) context).startActivityForResult(intent, requestCode);
                            }else{
                                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
                            }

                        }else{
                            if(showAppSetting){
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                intent.setData(uri);
                                fragment.startActivityForResult(intent, requestCode);
                            }else{
                                fragment.requestPermissions(new String[]{permission}, requestCode);
                            }

                        }

                    }
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(false)
                .create()
                .show();

    }

}
