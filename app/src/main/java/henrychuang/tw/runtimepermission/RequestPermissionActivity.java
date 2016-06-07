package henrychuang.tw.runtimepermission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by henry.chuang on 2016/6/7.
 */
public class RequestPermissionActivity extends Activity {
    public static String OPTION = "PermissionActivity.OPTION";
    private int mOption = 0;
    private String mPermission = "";
    private String mRationalePermission = "";
    private String mRationaleAppSetting = "";
    private final int mRequestCodePermission = 15;
    private int mRequestCodeAppSetting = 16;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            finish();
            return;
        }

        mOption = bundle.getInt(OPTION, 0);

        switch (mOption){
            case 1:
                mPermission = Manifest.permission.CALL_PHONE;
                mRationalePermission = "You need to allow access to CALL_PHONE";
                mRationaleAppSetting = "You need to allow permission manually from app setting";
                break;

            default:
                finish();
                return;
        }

        checkPermissions();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case mRequestCodePermission:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(RequestPermissionActivity.this, mPermission);
                    if (!showRationale) {
                        RuntimePermissionUtils.showRationale(RequestPermissionActivity.this,
                                mPermission,
                                mRequestCodeAppSetting,
                                mRationaleAppSetting,
                                true,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                    } else if (mPermission.equals(permissions[0])) {
                        RuntimePermissionUtils.showRationale(RequestPermissionActivity.this,
                                mPermission,
                                mRequestCodePermission,
                                mRationalePermission,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                    }else {
                        finish();
                    }

                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                finish();
                break;
        }
    }

    private void checkPermissions(){
        int hasPermission = ContextCompat.checkSelfPermission(RequestPermissionActivity.this, mPermission);
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RequestPermissionActivity.this, mPermission)) {
                RuntimePermissionUtils.showRationale(RequestPermissionActivity.this,
                        mPermission,
                        mRequestCodePermission,
                        mRationalePermission,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

            } else {
                ActivityCompat.requestPermissions(RequestPermissionActivity.this,
                        new String[]{mPermission},
                        mRequestCodePermission);

            }

        }else {
            setResult(RESULT_OK);
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mRequestCodeAppSetting) {
            int hasPermission = ContextCompat.checkSelfPermission(RequestPermissionActivity.this, mPermission);
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                setResult(RESULT_OK);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

       finish();
    }

    /**
     *
     * @param context
     * @param option  1 : CALL_PHONE
     * @return
     */
    public static Intent newStartIntent(Context context, int option){
        Intent intent = new Intent(context, RequestPermissionActivity.class);
        intent.putExtra(OPTION, option);

        return intent;
    }

}
