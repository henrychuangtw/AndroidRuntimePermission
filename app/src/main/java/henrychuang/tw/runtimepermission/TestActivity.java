package henrychuang.tw.runtimepermission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TestActivity extends Activity {
    private Button mButtonContact, mButtonCommon;
    private TextView mTextViewResult;
    private String sPermission = Manifest.permission.READ_CONTACTS;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    private final int REQUEST_PERMISSION_SETTING = 125;
    private int mRequestCode_buttonCommon = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.permission_test);

        mTextViewResult = (TextView) findViewById(R.id.txt_result);

        mButtonContact = (Button) findViewById(R.id.btn_contact);
        mButtonContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextViewResult.setText("");

                queryContact();
            }
        });

        mButtonCommon = (Button)findViewById(R.id.btn_common);
        mButtonCommon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = RequestPermissionActivity.newStartIntent(TestActivity.this, 1);
                startActivityForResult(intent, mRequestCode_buttonCommon);
            }
        });

    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void queryContact() {
        int hasPermission = ContextCompat.checkSelfPermission(TestActivity.this, sPermission);
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(TestActivity.this, sPermission)) {
                RuntimePermissionUtils.showRationale(TestActivity.this,
                        Manifest.permission.READ_CONTACTS,
                        REQUEST_CODE_ASK_PERMISSIONS,
                        "You need to allow access to Contact");

            } else {
                ActivityCompat.requestPermissions(TestActivity.this,
                        new String[]{sPermission},
                        REQUEST_CODE_ASK_PERMISSIONS);

            }

            return;
        }

        showContactsCount();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    queryContact();

                } else {
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(TestActivity.this, sPermission);
                    if (!showRationale) {
                        RuntimePermissionUtils.showRationale(TestActivity.this,
                                Manifest.permission.READ_CONTACTS,
                                REQUEST_PERMISSION_SETTING,
                                "You need to allow permission manually from app setting",
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
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            int hasPermission = ContextCompat.checkSelfPermission(TestActivity.this, sPermission);
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                queryContact();
            }

        }else if(requestCode == mRequestCode_buttonCommon && resultCode == Activity.RESULT_OK){
            if(ContextCompat.checkSelfPermission(TestActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                Intent intentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:16462574500"));
                startActivity(intentDial);
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showContactsCount() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        int iCount = cursor.getCount();

        if (cursor != null)
            cursor.close();

        mTextViewResult.append("total contacts : " + iCount + "\n");
    }

}
