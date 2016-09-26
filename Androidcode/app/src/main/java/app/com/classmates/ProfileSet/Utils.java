package app.com.classmates.ProfileSet;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.com.classmates.multipleclasses.GlobalConstants;

public class Utils {
    public static Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    public static int getSuitableWidth(Activity active) {
        Display display = active.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        width = (width / 2) + 80;
        return width;
    }

    public static String storeImage(Bitmap imageData, String filename) {
        //get path to external storage (SD card)

        File sdIconStorageDir = new File(GlobalConstants.DIR_PATH);

        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();
        String filePath = "";
        try {
            if (!filename.endsWith(".jpg")) {
                filename = filename + ".jpg";
            }
            filePath = sdIconStorageDir.toString() + "/" + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            imageData.compress(CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());

        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());

        }

        return filePath;
    }

    //___getting the path of the image saved after clicking
    public static String getRealPathFromURI(Uri contentUri, Context c) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = ((Activity) c).managedQuery(contentUri, proj, null, null, null);

        if (cursor == null)
            return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    //_______rounded cornered image
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 5;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    //Circular Bitmap.
    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    public static File getOutputMediaFile(int type, String name) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File Test = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File Test1 = Environment.getExternalStorageDirectory();
        Log.d("Environment", "Environment path " + Test.getAbsolutePath().toString());
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "RateMe");
        Log.d("Environment", "Environment path " + mediaStorageDir.getAbsolutePath().toString());
        if (!mediaStorageDir.exists()) {
                    /*if (! mediaStorageDir.mkdirs()){
                        Log.d("MyCameraApp", "failed to create directory");
			            return null;
			        }*/
            mediaStorageDir.mkdir();
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 20) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + name.toLowerCase() + timeStamp + ".jpg");
        } else
            return null;

        return mediaFile;
    }

    public static String getExtention(String filepath) {
        String Extention = "";

        if (filepath.endsWith(".png") || filepath.endsWith(".PNG")) {
            Extention = ".png";
        }
        if (filepath.endsWith(".jpg") || filepath.endsWith(".JPG")) {
            Extention = ".jpg";
        }
        if (filepath.endsWith(".gif") || filepath.endsWith(".GIF")) {
            Extention = ".gif";

        }
        if (filepath.endsWith(".jpeg") || filepath.endsWith(".JPEG")) {
            Extention = ".jpeg";
        }
        return Extention;
    }


    public Bitmap getImageFromPath(String filePath) {
        // Decode image size
        Log.v("filepath", filePath);
        Matrix matrix = new Matrix();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, o2);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);

        return bitmap;
    }


    public static String formatTime(long millis) {
        String output = "00:00:00";
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 60;

        String secondsD = String.valueOf(seconds);
        String minutesD = String.valueOf(minutes);
        String hoursD = String.valueOf(hours);

        if (seconds < 10)
            secondsD = "0" + seconds;
        if (minutes < 10)
            minutesD = "0" + minutes;
        if (hours < 10)
            hoursD = "" + hours;

        output = hoursD + "," + minutesD + "," + secondsD;
        //Log.i("output",""+output);
        return output;
    }

}
