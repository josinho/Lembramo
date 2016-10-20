package gal.xieiro.lembramo;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import gal.xieiro.lembramo.alarm.BootReceiver;
import gal.xieiro.lembramo.alarm.ScheduleReceiver;
import gal.xieiro.lembramo.util.Utils;

public class LembramoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initUniversalImageLoader();
        BootReceiver.initScheduleAlarm(this);
    }

    private void initUniversalImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.no_image)
                .showImageOnFail(R.drawable.no_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .preProcessor(new SquareProcessor())
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheSize(10) //10% de la memoria de la app para la LruMemoryCache
                .defaultDisplayImageOptions(options)
                .build();

        ImageLoader.getInstance().init(config);
    }

    public class SquareProcessor implements BitmapProcessor {
        @Override
        public Bitmap process(Bitmap bitmap) {
            Bitmap result;

            result = Utils.getSquareBitmap(bitmap);
            if(result != bitmap) bitmap.recycle();
            return result;
        }
    }
}
