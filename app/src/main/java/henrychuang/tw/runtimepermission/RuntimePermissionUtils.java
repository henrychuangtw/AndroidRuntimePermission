package henrychuang.tw.runtimepermission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import java.util.List;

/**
 * Created by henry.chuang on 2016/5/27.
 */
public class RuntimePermissionUtils {
    public static void showRationale(Context context, String permission, int requestCode, String message){
        showRationale(context,
                null,
                permission,
                requestCode,
                message);
    }
    public static void showRationale(Context context, String permission, int requestCode, String message, DialogInterface.OnClickListener cancelListener){
        showRationale(context,
                null,
                new String[]{permission},
                requestCode,
                message,
                false,
                cancelListener);
    }
    public static void showRationale(Context context, String[] permissions, int requestCode, String message){
        showRationale(context,
                null,
                permissions,
                requestCode,
                message);
    }
    public static void showRationale(Context context, String permission, int requestCode, String message, boolean showAppSetting){
        showRationale(context,
                null,
                permission,
                requestCode,
                message,
                showAppSetting);
    }
    public static void showRationale(Context context, String permission, int requestCode, String message, boolean showAppSetting, DialogInterface.OnClickListener cancelListener){
        showRationale(context,
                null,
                new String[]{permission},
                requestCode,
                message,
                showAppSetting,
                cancelListener);
    }
    public static void  showRationale(Context context, String[] permissions, int requestCode, String message, boolean showAppSetting){
        showRationale(context,
                null,
                permissions,
                requestCode,
                message,
                showAppSetting);
    }
    public static void showRationale(Context context, Fragment fragment, String permission, int requestCode, String message){
        showRationale(context,
                fragment,
                permission,
                requestCode,
                message,
                false);
    }
    public static void showRationale(Context context, Fragment fragment, String[] permissions, int requestCode, String message){
        showRationale(context,
                fragment,
                permissions,
                requestCode,
                message,
                false);
    }
    public static void showRationale(Context context, Fragment fragment, String permission, int requestCode, String message, boolean showAppSetting){
        showRationale(context,
                fragment,
                new String[]{permission},
                requestCode,
                message,
                showAppSetting);
    }
    public static void showRationale(Context context, Fragment fragment, String[] permissions, int requestCode, String message, boolean showAppSetting){
        showRationale(context,
                fragment,
                permissions,
                requestCode,
                message,
                showAppSetting,
                null);
    }
    public static void showRationale(final Context context, final Fragment fragment, final String[] permissions,
                                     final int requestCode, String message, final boolean showAppSetting, final DialogInterface.OnClickListener cancelListener){
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
                                ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);
                            }

                        }else{
                            if(showAppSetting){
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                intent.setData(uri);
                                fragment.startActivityForResult(intent, requestCode);
                            }else{
                                fragment.requestPermissions(permissions, requestCode);
                            }

                        }

                    }
                })
                .setNegativeButton("Cancel", cancelListener)
                .setCancelable(false)
                .create()
                .show();
    }

    public static boolean addPermission(Context context, List<String> permissionsList, String permission){
        return addPermission(context,
                    null,
                    permissionsList,
                    permission);
    }
    public static boolean addPermission(Context context, Fragment fragment, List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);

            if(fragment == null){
                if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission))
                    return false;

            }else{
                if(fragment.shouldShowRequestPermissionRationale(permission))
                    return false;

            }

        }
        return true;
    }

}
