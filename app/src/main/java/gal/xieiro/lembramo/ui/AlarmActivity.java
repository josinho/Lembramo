package gal.xieiro.lembramo.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.threeten.bp.Instant;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.alarm.AlarmHelper;
import gal.xieiro.lembramo.db.DBContract;
import gal.xieiro.lembramo.db.LembramoContentProvider;
import gal.xieiro.lembramo.model.MedicineIntake;
import gal.xieiro.lembramo.ui.component.SquareImageView;
import gal.xieiro.lembramo.ui.component.SwipeButton;
import gal.xieiro.lembramo.ui.component.SwipeButtonCustomItems;

public class AlarmActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 2;
    private TextView mName, mDose;
    private SquareImageView mBoxImage, mPillImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mName = (TextView) findViewById(R.id.medicineName);
        mDose = (TextView) findViewById(R.id.dose);
        mBoxImage = (SquareImageView) findViewById(R.id.boxImage);
        mPillImage = (SquareImageView) findViewById(R.id.pillImage);

        Intent intent = getIntent();
        final MedicineIntake intake = intent.getParcelableExtra(AlarmHelper.EXTRA_PARAMS);
        if (intake != null) {
            Bundle bundle = new Bundle();
            bundle.putLong(DBContract.Medicines._ID, intake.getMedicineId());
            getSupportLoaderManager().initLoader(LOADER_ID, bundle, this);
            mDose.setText(getResources().getString(R.string.lbl_dose) + ": " + intake.getDose());
        }

        SwipeButton mSwipeButton = (SwipeButton) findViewById(R.id.cancel_alarm_button);
        SwipeButtonCustomItems swipeButtonSettings = new SwipeButtonCustomItems() {
            @Override
            public void onSwipeConfirm() {
                if (intake != null)
                    // TODO AsyncTask
                    intakeDone(intake.getId());
            }
        };

        swipeButtonSettings
                .setButtonPressText(getResources().getString(R.string.swipe_alarm))
                .setGradientColor1(0xFF888888)
                .setGradientColor2(0xFF666666)
                .setGradientColor2Width(60)
                .setGradientColor3(0xFF333333)
                .setPostConfirmationColor(0xFF888888)
                .setActionConfirmDistanceFraction(0.7)
                .setActionConfirmText(getResources().getString(R.string.intake_done));

        if (mSwipeButton != null) {
            mSwipeButton.setSwipeButtonCustomItems(swipeButtonSettings);
        }
    }

    public int getLayoutResource() {
        return R.layout.activity_alarm;
    }


    private void setOnclickView(ImageView imageView, final String imagePath) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagePath != null) {
                    Intent intent = new Intent(AlarmActivity.this, TouchImageActivity.class);
                    intent.putExtra("image", imagePath);
                    startActivity(intent);
                }
            }
        });
    }

    private void intakeDone(long intakeId) {
        ContentValues cv = new ContentValues();
        cv.put(DBContract.Intakes.COLUMN_NAME_INTAKE_DATE, Instant.now().toEpochMilli());

        String uri = LembramoContentProvider.CONTENT_URI_INTAKES.toString() + "/" + intakeId;
        getContentResolver().update(Uri.parse(uri), cv, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long key = args.getLong(DBContract.Medicines._ID);
        String uri = LembramoContentProvider.CONTENT_URI_MEDICINES.toString() + "/" + key;
        return new CursorLoader(this, Uri.parse(uri), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c != null) {
            c.moveToFirst();
            mName.setText(c.getString(c.getColumnIndex(DBContract.Medicines.COLUMN_NAME_NAME)));
            String boxImageUri = c.getString(c.getColumnIndex(DBContract.Medicines.COLUMN_NAME_BOXPHOTO));
            String pillImageUri = c.getString(c.getColumnIndex(DBContract.Medicines.COLUMN_NAME_MEDPHOTO));

            ImageLoader imageLoader = ImageLoader.getInstance();

            if (!TextUtils.isEmpty(boxImageUri)) {
                imageLoader.displayImage(boxImageUri, mBoxImage);
                setOnclickView(mBoxImage, boxImageUri);
            } else {
                mBoxImage.setImageResource(R.drawable.caja);
            }

            if (!TextUtils.isEmpty(pillImageUri)) {
                imageLoader.displayImage(pillImageUri, mPillImage);
                setOnclickView(mPillImage, pillImageUri);
            } else {
                mPillImage.setImageResource(R.drawable.pastilla);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
