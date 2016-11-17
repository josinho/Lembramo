package gal.xieiro.lembramo.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.threeten.bp.LocalDateTime;

import gal.xieiro.lembramo.util.TimeUtils;


public class MedicineIntake implements Parcelable {
    private long id;
    private long medicineId;
    private double dose;
    private LocalDateTime intakeInstant;
    private LocalDateTime realIntakeInstant;

    //////// getter and setter methods

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(long medicineId) {
        this.medicineId = medicineId;
    }

    public LocalDateTime getIntakeInstant() {
        return intakeInstant;
    }

    public void setIntakeInstant(LocalDateTime intakeInstant) {
        this.intakeInstant = intakeInstant;
    }

    public LocalDateTime getRealIntakeInstant() {
        return realIntakeInstant;
    }

    public void setRealIntakeInstant(LocalDateTime realIntakeInstant) {
        this.realIntakeInstant = realIntakeInstant;
    }

    public double getDose() {
        return dose;
    }

    public void setDose(double dose) {
        this.dose = dose;
    }


    public MedicineIntake() {
    }

    private MedicineIntake(Parcel in) {
        id = in.readLong();
        medicineId = in.readLong();
        dose = in.readDouble();
        intakeInstant = TimeUtils.parseDateTime(in.readString());
        //realIntakeInstant = TimeUtils.parseDateTime(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return medicineId + "." + id;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeLong(medicineId);
        out.writeDouble(dose);
        out.writeString(TimeUtils.getStringDateTime(intakeInstant));
        //out.writeString(TimeUtils.getStringDateTime(realIntakeInstant));
    }


    public static final Parcelable.Creator<MedicineIntake> CREATOR =
            new Parcelable.Creator<MedicineIntake>() {
                public MedicineIntake createFromParcel(Parcel in) {
                    return new MedicineIntake(in);
                }

                public MedicineIntake[] newArray(int size) {
                    return new MedicineIntake[size];
                }
            };

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }
}
