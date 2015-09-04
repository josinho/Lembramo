package gal.xieiro.lembramo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import gal.xieiro.lembramo.R;


public class DosePicker extends LinearLayout {

    private double mValue = 1;
    private double mMinValue = 0.25;
    private double mMaxValue = 3;
    private double mDelta = 0.25;

    private ImageView mMinus, mPlus;
    private TextView mDose;

    public DosePicker(Context context) {
        this(context, null);
    }

    public DosePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DosePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setOrientation(LinearLayout.HORIZONTAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dose_picker, this, true);

        mMinus = (ImageView) v.findViewById(R.id.minus);
        mPlus = (ImageView) v.findViewById(R.id.plus);
        mDose = (TextView) v.findViewById(R.id.dose);

        mMinus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double res = mValue - mDelta;
                        if(res >= mMinValue) {
                            mValue = res;
                            mDose.setText(new DecimalFormat("0.00").format(res));
                        }
                    }
                }

        );

        mPlus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double res = mValue + mDelta;
                        if(res <= mMaxValue) {
                            mValue = res;
                            mDose.setText(new DecimalFormat("0.00").format(res));
                        }
                    }
                }

        );
    }


    public double getDelta() {
        return mDelta;
    }

    public void setDelta(double delta) {
        mDelta = delta;
    }

    public double getValue() {
        return mValue;
    }

    public void setValue(double value) {
        mValue = value;
    }

    public double getMin() {
        return mMinValue;
    }

    public void setMin(double min) {
        mMinValue = min;
    }

    public double getMax() {
        return mMaxValue;
    }

    public void setMax(double max) {
        mMaxValue = max;
    }
}
