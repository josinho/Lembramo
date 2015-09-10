package gal.xieiro.lembramo.ui;


import android.text.Editable;
import android.text.TextWatcher;

class MinMaxTextWatcher implements TextWatcher {

    private int mMin;
    private int mMax;
    private int mDefault;

    public MinMaxTextWatcher(int min, int defaultInt, int max) {
        mMin = min;
        mMax = max;
        mDefault = defaultInt;
    }

    @Override
    public void afterTextChanged(Editable s) {

        boolean updated = false;
        int value;
        try {
            value = Integer.parseInt(s.toString());
        } catch (NumberFormatException e) {
            value = mDefault;
        }

        if (value < mMin) {
            value = mMin;
            updated = true;
        } else if (value > mMax) {
            updated = true;
            value = mMax;
        }

        // Update UI
        if (updated) {
            s.clear();
            s.append(Integer.toString(value));
        }

        onChange(value);
    }

    /**
     * Override to be called after each key stroke
     */
    void onChange(int value) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}