package gal.xieiro.lembramo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Medicine implements Parcelable {
    private long id;
    private String name;
    private String comment;
    private String pillboxImage;
    private String pillImage;
    private String startDate;
    private long endDate;
    private String recurrenceRule;
    private String schedule;

    public Medicine(long _id) {
        id = _id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getPillboxImage() {
        return pillboxImage;
    }

    public String getPillImage() {
        return pillImage;
    }

    public String getStartDate() {
        return startDate;
    }

    public long  getEndDate() {
        return endDate;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPillboxImage(String pillboxImage) {
        this.pillboxImage = pillboxImage;
    }

    public void setPillImage(String pillImage) {
        this.pillImage = pillImage;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public Medicine(Medicine medicine) {
        id = medicine.getId();
        name = medicine.getName();
        comment = medicine.getComment();
        pillboxImage = medicine.getPillboxImage();
        pillImage = medicine.getPillImage();
        startDate = medicine.getStartDate();
        recurrenceRule = medicine.getRecurrenceRule();
        schedule = medicine.getSchedule();
    }

    private Medicine(Parcel in) {
        id = in.readLong();
        name = in.readString();
        comment = in.readString();
        pillboxImage = in.readString();
        pillImage = in.readString();
        startDate = in.readString();
        endDate = in.readLong();
        recurrenceRule = in.readString();
        schedule = in.readString();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(name);
        out.writeString(comment);
        out.writeString(pillboxImage);
        out.writeString(pillImage);
        out.writeString(startDate);
        out.writeLong(endDate);
        out.writeString(recurrenceRule);
        out.writeString(schedule);
    }

    public static final Parcelable.Creator<Medicine> CREATOR =
            new Parcelable.Creator<Medicine>() {
                public Medicine createFromParcel(Parcel in) {
                    return new Medicine(in);
                }

                public Medicine[] newArray(int size) {
                    return new Medicine[size];
                }
            };


    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return id + ":" + name;
    }
}
