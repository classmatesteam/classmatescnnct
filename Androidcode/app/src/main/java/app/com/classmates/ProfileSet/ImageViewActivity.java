package app.com.classmates.ProfileSet;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.io.File;

import app.com.classmates.R;
import app.com.classmates.Settings;


public class ImageViewActivity extends FragmentActivity implements PicModeSelectDialogFragment.IPicModeSelectListener {

    public static final String TAG = "ImageViewActivity";
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;
    String mImagePath = "";
    //GlobalClass globaled;
    private Button mBtnUpdatePic, saveButton;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityimageview);

        mBtnUpdatePic = (Button) findViewById(R.id.btnUpdatePic);
        saveButton = (Button) findViewById(R.id.btnSave);
        mImageView = (ImageView) findViewById(R.id.iv_user_pic);
        // globaled = (GlobalClass)getApplicationContext();
        mBtnUpdatePic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProfilePicDialog();
            }
        });

        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.e("path", mImagePath);
                if (mImagePath.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Choose Image First", Toast.LENGTH_SHORT).show();
                    mImagePath = "";
                } else {
                    Intent intent = new Intent(ImageViewActivity.this, Settings.class);
                    intent.putExtra(Constants.IntentExtras.IMAGE_PATH, mImagePath);
                    startActivity(intent);
                    // setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        showAddProfilePicDialog();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == REQUEST_CODE_UPDATE_PIC) {
            if (resultCode == RESULT_OK) {
                mImagePath = result.getStringExtra(Constants.IntentExtras.IMAGE_PATH);
                //  globaled.setSetupimage(mImagePath);
                Log.e("images", mImagePath);
                showCroppedImage(mImagePath);
            } else if (resultCode == RESULT_CANCELED) {

            } else {
                String errorMsg = result.getStringExtra(ImageCropActivity.ERROR_MSG);
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showCroppedImage(String mImagePath) {
        if (mImagePath != null) {
            Picasso.with(ImageViewActivity.this).load(new File(mImagePath)).transform(new CircleTransform()).into(mImageView);
            //mImageView.setImageBitmap(myBitmap);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAddProfilePicDialog() {
        PicModeSelectDialogFragment dialogFragment = new PicModeSelectDialogFragment();
        dialogFragment.setiPicModeSelectListener(this);
        dialogFragment.show(getFragmentManager(), "picModeSelector");
    }

    private void actionProfilePic(String action) {
        Intent intent = new Intent(ImageViewActivity.this, ImageCropActivity.class);
        intent.putExtra("ACTION", action);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
    }


    @Override
    public void onPicModeSelected(String mode) {
        String action = mode.equalsIgnoreCase(Constants.PicModes.CAMERA) ? Constants.IntentExtras.ACTION_CAMERA : Constants.IntentExtras.ACTION_GALLERY;
        actionProfilePic(action);
    }

}
