package gal.xieiro.lembramo;

import android.app.Application;

import gal.xieiro.lembramo.util.ImageCacheManager;
import gal.xieiro.lembramo.util.RequestManager;

public class LembramoApp extends Application {

    private final static int IMAGECACHE_SIZE = 1024*1024*10; // 10MB

    @Override
    public void onCreate() {
        super.onCreate();
        //init();
    }

    private void init() {
        //inicializar la librería de Volley
        RequestManager.init(this);

        //inicializar la caché de imágenes
        ImageCacheManager.getInstance().init(IMAGECACHE_SIZE);
    }
}
