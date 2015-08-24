package gal.xieiro.lembramo.ui;

import android.content.Intent;
import android.os.Bundle;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;

import gal.xieiro.lembramo.R;

public class TouchImageActivity extends BaseActivity {
    TouchImageView mTouchImageView;
    ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String uri = intent.getStringExtra("image");
        if (uri == null) finish();

        mTouchImageView = (TouchImageView) findViewById(R.id.image);
        mImageLoader = ImageLoader.getInstance();
        File imageFile = mImageLoader.getDiskCache().get(uri);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        MemoryCacheUtils.removeFromCache(uri, mImageLoader.getMemoryCache());
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .build();

        mImageLoader.displayImage(uri, mTouchImageView, options);
    }

    public int getLayoutResource(){
        return R.layout.activity_show_image;
    }
}
