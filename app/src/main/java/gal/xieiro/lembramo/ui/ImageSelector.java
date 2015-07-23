package gal.xieiro.lembramo.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import gal.xieiro.lembramo.R;

public class ImageSelector extends RelativeLayout {
    private ImageView mImage;
    private ImageView mButton;

    public ImageSelector(Context context) {
        this(context, null);
    }

    public ImageSelector(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.image_selector, this, true);

        mImage = (ImageView) findViewById(R.id.imagen);
        mButton = (ImageView) findViewById(R.id.editPhoto);


        mButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(ImageSelector.this.getContext(), "Click en " + ImageSelector.this.getId(), Toast.LENGTH_LONG).show();

                    }
                }
        );
    }

    public ImageSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context);
    }

    public ImageSelector(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context);
    }

    public void setImageResource(int resId) {
        mImage.setImageResource(resId);
    }
}
