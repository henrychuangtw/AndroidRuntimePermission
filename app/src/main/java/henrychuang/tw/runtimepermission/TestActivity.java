package henrychuang.tw.runtimepermission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class TestActivity extends Activity {
    Button btnContact;

    String sPermission =  Manifest.permission.READ_CONTACTS;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 100;
    final private int REQUEST_PERMISSION_SETTING = 125;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.permission_test);

        btnContact = (Button) findViewById(R.id.btn_contact);
        btnContact.setOnClickListener(listener_btnContact);

    }

    View.OnClickListener listener_btnContact = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            queryContact();

        }
    };

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void queryContact(){
        int hasPermission = ContextCompat.checkSelfPermission(TestActivity.this, sPermission);
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(TestActivity.this, sPermission)) {
                RuntimePermissionUtils.showRationale(TestActivity.this,
                        Manifest.permission.READ_CONTACTS,
                        REQUEST_CODE_ASK_PERMISSIONS,
                        "You need to allow access to Contact");

            }else{
                ActivityCompat.requestPermissions(TestActivity.this,
                        new String[]{sPermission},
                        REQUEST_CODE_ASK_PERMISSIONS);

            }

            return;
        }

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        int iCount = cursor.getCount();

        if(cursor != null)
            cursor.close();

        Toast.makeText(TestActivity.this, "total contacts : " + iCount, Toast.LENGTH_LONG).show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    queryContact();

                } else {
                    boolean showRationale =  ActivityCompat.shouldShowRequestPermissionRationale(TestActivity.this, sPermission);
                    if (! showRationale) {
                        RuntimePermissionUtils.showRationale(TestActivity.this,
                                Manifest.permission.READ_CONTACTS,
                                REQUEST_PERMISSION_SETTING,
                                "You need to allow permission",
                                true);

                    } else if (sPermission.equals(permissions[0])) {
                        RuntimePermissionUtils.showRationale(TestActivity.this,
                                Manifest.permission.READ_CONTACTS,
                                REQUEST_CODE_ASK_PERMISSIONS,
                                "You need to allow access to Contact");
                    }

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_PERMISSION_SETTING){
            int hasPermission = ContextCompat.checkSelfPermission(TestActivity.this, sPermission);
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                queryContact();
            }

        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
