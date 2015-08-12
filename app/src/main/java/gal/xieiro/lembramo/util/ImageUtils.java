package gal.xieiro.lembramo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageUtils {

    private static final String TAG = "ImageUtils";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";


    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File imageDirectory = getImageDirectory(context);

        return File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, imageDirectory);
    }

    public static File getImageDirectory(Context context) {
        File storageDir = null;

        // comprobar que el almacenamiento externo está disponible
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            // si queremos guardar en la parte privada de la app
            storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            // getExternalFilesDir() crea el directorio si no existiese

            /*
            // Si queremos guardar en la parte pública:
            storageDir = Environment.getExternalStorageDirectory();
            // faltaría indicar un subdirectorio
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d(TAG, "Failed to create directory");
                        return null;
                    }
                }
            }
            */

        } else {
            Log.v(TAG, "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    /**
     * Devuelve un bitmap escalado según un ancho y una altura especificados en pixels
     *
     * @param imagePath    La ruta de origen de la imagen
     * @param targetWidth  Ancho objetivo para la imagen en pixels
     * @param targetHeight Altura objetivo para la imagen en pixels
     */
    public static Bitmap scaleImage(String imagePath, int targetWidth, int targetHeight) {

		/* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetWidth > 0) || (targetHeight > 0)) {
            scaleFactor = Math.min(photoW / targetWidth, photoH / targetHeight);
        }

        //int scaleFactor = calculateInSampleSize(bmOptions, targetWidth, targetHeight);
        /*String msg = "targetWidth = " + targetWidth;
        msg += " | targetHeight = " + targetHeight;
        msg += " | imageWidth = " + photoW;
        msg += " | imageHeight = " + photoH;
        msg += " --> scaleFactor = " + scaleFactor;
        Log.v(TAG, msg);
        */

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        /* Decode the JPEG file into a Bitmap */
        return BitmapFactory.decodeFile(imagePath, bmOptions);
    }


    /**
     * Calculate an inSampleSize for use in a {@link android.graphics.BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link android.graphics.BitmapFactory}. This implementation calculates
     * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
     * having a width and height equal to or larger than the requested width and height.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap getSquareBitmap(Bitmap bitmap) {
        int dimension;

        dimension = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getWidth();
        return ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);
    }
}
