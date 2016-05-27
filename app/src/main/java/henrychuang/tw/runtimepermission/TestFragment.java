package henrychuang.tw.runtimepermission;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class TestFragment extends Fragment {
    Button btnContact;

    String sPermission =  Manifest.permission.READ_CONTACTS;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 100;
    final private int REQUEST_PERMISSION_SETTING = 125;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.permission_test, container, false);
        // you can use findViewById() using the above 'view'

        btnContact = (Button) view.findViewById(R.id.btn_contact);
        btnContact.setOnClickListener(listener_btnContact);

        return view;
    }


    View.OnClickListener listener_btnContact = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            queryContact();


        }
    };

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void queryContact(){
        int hasPermission = ContextCompat.checkSelfPermission(getActivity(), sPermission);
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale( sPermission)) {
                RuntimePermissionUtils.showRationale(getActivity(),
                        TestFragment.this,
                        Manifest.permission.READ_CONTACTS,
                        REQUEST_CODE_ASK_PERMISSIONS,
                        "You need to allow access to Contact");
            }else{
                requestPermissions(new String[]{sPermission},
                        REQUEST_CODE_ASK_PERMISSIONS);

            }

            return;
        }


        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        int iCount = cursor.getCount();

        if(cursor != null)
            cursor.close();

        Toast.makeText(getActivity(), "total contacts : " + iCount, Toast.LENGTH_LONG).show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    queryContact();

                } else {
                    boolean showRationale =  shouldShowRequestPermissionRationale( sPermission);
                    if (! showRationale) {
                        RuntimePermissionUtils.showRationale(getActivity(),
                                TestFragment.this,
                                Manifest.permission.READ_CONTACTS,
                                REQUEST_PERMISSION_SETTING,
                                "You need to allow permission",
                                true);

                    } else if (sPermission.equals(permissions[0])) {
                        RuntimePermissionUtils.showRationale(getActivity(),
                                TestFragment.this,
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(requestCode == REQUEST_PERMISSION_SETTING){
                int hasPermission = ContextCompat.checkSelfPermission(getActivity(), sPermission);
                if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                    queryContact();
                }

            }else {
                super.onActivityResult(requestCode, resultCode, data);
            }

    }
}
