package gal.xieiro.lembramo.util;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gal.xieiro.lembramo.model.MedicineIntake;

public class IntakeUtils {
    private static String TAG = "IntakeUtils";

    public IntakeUtils() {
    }


    public static List<MedicineIntake> setInitialIntakes() {
        ArrayList<MedicineIntake> intakes = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            Calendar hour = Calendar.getInstance();
            hour.set(Calendar.HOUR_OF_DAY, i);
            hour.set(Calendar.MINUTE, 0);
            Calendar halfHour = Calendar.getInstance();
            halfHour.set(Calendar.HOUR_OF_DAY, i);
            halfHour.set(Calendar.MINUTE, 30);

            intakes.add(new MedicineIntake(hour));
            intakes.add(new MedicineIntake(halfHour));
        }
        return intakes;
    }

    public static List<MedicineIntake> parseDailyIntakes(String intakesRule) {
        ArrayList<MedicineIntake> intakes = new ArrayList<>();

        if (!TextUtils.isEmpty(intakesRule)) {
            String[] intakeStrings = intakesRule.split(";");
            for (String intakeString : intakeStrings) {
                String[] intake = intakeString.split(",");
                Calendar hour = TimeUtils.parseTime(intake[0]);
                MedicineIntake medicineIntake = new MedicineIntake(hour);
                medicineIntake.setDose(Double.valueOf(intake[1]));
                medicineIntake.setChecked(true);
                intakes.add(medicineIntake);
            }
        }
        return intakes;
    }
}
