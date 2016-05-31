package henrychuang.tw.runtimepermission;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private String LogTag = "henrytest";
    private Button mButtonGoActivity, mButtonGoFragment, mButtonSetRingtone, mButtonMultiplePermissions;
    private TextView mTextViewResult;
    private int RequestCode_Ringtone = 1200;
    private int RequestCode_Ringtone_Permission = 1300;
    private final int REQUEST_CODE_MULTIPLE_PERMISSIONS = 130;
    private final int REQUEST_PERMISSION_SETTING_MULTIPLE_PERMISSIONS = 135;
    private LocationManager mLocationManager;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewResult = (TextView) findViewById(R.id.txt_result);
        mButtonGoActivity = (Button)findViewById(R.id.btn_goActivity);
        mButtonGoFragment = (Button)findViewById(R.id.btn_goFragment);
        mButtonSetRingtone = (Button)findViewById(R.id.btn_setRingtone);
        mButtonMultiplePermissions = (Button) findViewById(R.id.btn_multiplePermission);

        mButtonGoActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });

        mButtonGoFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestFragmentActivity.class);
                startActivity(intent);

            }
        });

        mButtonSetRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!canWrite(MainActivity.this)){
                    requestPermission(RequestCode_Ringtone_Permission);
                    return;
                }

                nowHavePermission();
            }
        });

        mButtonMultiplePermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextViewResult.setText("");

                queryContactGetLocationGetDeviceId();
            }
        });

        mContext = MainActivity.this;
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_MULTIPLE_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    queryContactGetLocationGetDeviceId();
                } else {
                    boolean showRationale_ACCESS_FINE_LOCATION = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                    boolean showRationale_READ_CONTACTS = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS);
                    boolean showRationale_READ_PHONE_STATE =  ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_PHONE_STATE) ;

                    if(showRationale_ACCESS_FINE_LOCATION || showRationale_READ_CONTACTS || showRationale_READ_PHONE_STATE){
                        queryContactGetLocationGetDeviceId();

                    }else{
                        String message = "You need to allow permission : ";

                        if(perms.get(Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
                            message += showRationale_ACCESS_FINE_LOCATION ? "" : "GPS, ";

                        if(perms.get(Manifest.permission.READ_CONTACTS)  != PackageManager.PERMISSION_GRANTED )
                            message += showRationale_READ_CONTACTS ? "" : "Read Contacts, ";

                        if(perms.get(Manifest.permission.READ_PHONE_STATE)  != PackageManager.PERMISSION_GRANTED )
                            message += showRationale_READ_PHONE_STATE ? "" : "Read PhoneState";

                        RuntimePermissionUtils.showRationale(MainActivity.this,
                                "",
                                REQUEST_PERMISSION_SETTING_MULTIPLE_PERMISSIONS,
                                message,
                                true);
                    }

                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RequestCode_Ringtone && resultCode == RESULT_OK){
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            Log.d(LogTag, "" +  uri);

            if (uri != null) {
                 RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM, uri);

            }
        }else if (requestCode == RequestCode_Ringtone_Permission) {
            if(!canWrite(MainActivity.this)){
                needPermissionDialog(requestCode);
            }else{
               nowHavePermission();
            }
        }

    }

    private void nowHavePermission(){
        final Uri currentTone= RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_ALARM);

        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "set Ringtone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
        startActivityForResult(intent, RequestCode_Ringtone);
    }

    private void requestPermission(int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    private void needPermissionDialog(final int requestCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("You need to allow permission");
        builder.setPositiveButton("OK",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        requestPermission(requestCode);
                    }
                });
        builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private boolean canWrite(Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }else{
            return Settings.System.canWrite(context);
        }

    }

    private void queryContactGetLocationGetDeviceId() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (!RuntimePermissionUtils.addPermission(mContext, permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!RuntimePermissionUtils.addPermission(mContext, permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Read Contacts");
        if (!RuntimePermissionUtils.addPermission(mContext, permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("Read PhoneState");


        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);

                RuntimePermissionUtils.showRationale(mContext,
                        permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_MULTIPLE_PERMISSIONS,
                        message);

                return;
            }

            ActivityCompat.requestPermissions(MainActivity.this,
                    permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_MULTIPLE_PERMISSIONS);

            return;
        }

        showContactsCount();

        Location location = getLocation(mLocationManager);
        if(location != null)
            showLocation(location);

        showDeviceId();
    }

    private void showContactsCount() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        int iCount = cursor.getCount();

        if (cursor != null)
            cursor.close();

        mTextViewResult.append("total contacts : " + iCount + "\n");
    }

    private Location getLocation(LocationManager locationManager) {
        Location location = null;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return location;
        }

        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);

        location = locationManager.getLastKnownLocation(bestProvider);

        if(location == null){
            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        return location;
    }

    private void showLocation(Location location) {
        String str = "";
        if (location != null) {
            str = "Longitude : " + location.getLongitude() + " , Latitude : " + location.getLatitude();
        } else {
            str = "can't get location \n";
        }

        mTextViewResult.append(str + "\n");
    }

    private void showDeviceId(){
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTextViewResult.append("DeviceId : " + telephonyManager.getDeviceId() + "\n");
    }

}
