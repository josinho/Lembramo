package gal.xieiro.lembramo.ui;

import android.os.Bundle;
import android.util.Log;

import gal.xieiro.lembramo.R;
import gal.xieiro.lembramo.ui.component.SwipeButton;
import gal.xieiro.lembramo.ui.component.SwipeButtonCustomItems;

public class AlarmActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeButton mSwipeButton = (SwipeButton) findViewById(R.id.cancel_alarm_button);

        SwipeButtonCustomItems swipeButtonSettings = new SwipeButtonCustomItems() {
            @Override
            public void onSwipeConfirm() {
                Log.d("SwipeButton", "New swipe confirm callback");
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
}
