package gal.xieiro.lembramo.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Class containing some static utility methods.
 */
public class Utils {

    private Utils() {
    }

    public static final long NO_ID = -1;
    private static final String TAG = "ImageUtils";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private static final String CAMERA_DIR = "/dcim/";


    public static File createImageFile(String albumName) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + JPEG_FILE_SUFFIX;  //+ "_";
        File imageDirectory = getImageDirectory(albumName);

        //return File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, imageDirectory);
        return new File(imageDirectory, imageFileName);
    }

    public static File getImageDirectory(String albumName) {
        File storageDir = null;

        // comprobar que el almacenamiento externo está disponible
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            // si queremos guardar en la parte privada de la app
            //storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            // getExternalFilesDir() crea el directorio si no existiese

            // Si queremos guardar en la parte pública:
            storageDir = new File(Environment.getExternalStorageDirectory() + CAMERA_DIR + albumName);
            // faltaría indicar un subdirectorio
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d(TAG, "Failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v(TAG, "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    public static Bitmap getSquareBitmap(Bitmap bitmap) {
        int dimension;

        dimension = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getWidth();
        return ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }


    public static int getHour(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[1]));
    }

    public static Calendar parseTime(String time) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            c.setTime(sdf.parse(time));
            return c;
        } catch (ParseException e) {
            Log.e(TAG, "Time parsing error: " + e);
        }
        return null;
    }

    public static Calendar getCalendarTimeFromString(String time) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, getHour(time));
        c.set(Calendar.MINUTE, getMinute(time));
        return c;
    }

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action  The Intent action to check for availability.
     * @return True if an Intent with the specified action can be sent and
     * responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }
}
