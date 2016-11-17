package gal.xieiro.lembramo.util;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import gal.xieiro.lembramo.model.IntakeHelper;

public class IntakeUtils {
    private static String TAG = "IntakeUtils";

    public IntakeUtils() {
    }

    public static List<IntakeHelper> parseDailyIntakes(String intakesRule) {
        ArrayList<IntakeHelper> intakes = new ArrayList<>();

        if (!TextUtils.isEmpty(intakesRule)) {
            String[] intakeStrings = intakesRule.split(";");
            for (String intakeString : intakeStrings) {
                String[] intake = intakeString.split(",");
                IntakeHelper medicineIntake = new IntakeHelper(TimeUtils.parseTime(intake[0]));
                medicineIntake.setDose(Double.valueOf(intake[1]));
                medicineIntake.setChecked(true);
                intakes.add(medicineIntake);
            }
        }
        return intakes;
    }
}
