package gal.xieiro.lembramo.db;

import android.provider.BaseColumns;


// Contract class para definir los campos de la tabla
public final class DBContract {

    //constructor vacío para impedir que se instancie
    public DBContract() {
    }

    public static abstract class DBInfo {
        public static final String DBNAME = "lembramo.db";
        public static final int DBVERSION = 1;
    }

    public static abstract class DBType {
        public static final String BLOB = " BLOB";
        public static final String INTEGER = " INTEGER";
        public static final String INTEGER_PRIMARY_KEY = " INTEGER PRIMARY KEY";
        public static final String NULL = " NULL";
        public static final String REAL = " REAL";
        public static final String TEXT = " TEXT";
        public static final String COMMA = ", ";
        public static final String PARENTHESIS_OPEN = " (";
        public static final String PARENTHESIS_CLOSE = " )";
        public static final String CREATE_TABLE = "CREATE TABLE ";
        public static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";
    }


    //clase interna que define los contenidos de la tabla
    public static abstract class Medicines implements BaseColumns {
        public static final String TABLE_NAME = "medicines";
        //al implementar BaseColumns tenemos un campo _ID
        public static final String COLUMN_NAME_ALARM = "alarm";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_COMMENT = "comment";
        public static final String COLUMN_NAME_BOXPHOTO = "boxphoto";
        public static final String COLUMN_NAME_MEDPHOTO = "medphoto";

        //información del tratamiento
        public static final String COLUMN_NAME_STARTDATE = "startdate";
        public static final String COLUMN_NAME_RECURRENCE = "recurrence";
        public static final String COLUMN_NAME_SCHEDULE = "shedule";

        public static final String CREATE_TABLE =
                DBType.CREATE_TABLE + TABLE_NAME + DBType.PARENTHESIS_OPEN +
                        _ID + DBType.INTEGER_PRIMARY_KEY + DBType.COMMA +
                        COLUMN_NAME_ALARM + DBType.INTEGER + DBType.COMMA +
                        COLUMN_NAME_NAME + DBType.TEXT + DBType.COMMA +
                        COLUMN_NAME_COMMENT + DBType.TEXT + DBType.COMMA +
                        COLUMN_NAME_BOXPHOTO + DBType.TEXT + DBType.COMMA +
                        COLUMN_NAME_MEDPHOTO + DBType.TEXT + DBType.COMMA +
                        COLUMN_NAME_STARTDATE + DBType.TEXT + DBType.COMMA +
                        COLUMN_NAME_RECURRENCE + DBType.TEXT + DBType.COMMA +
                        COLUMN_NAME_SCHEDULE + DBType.TEXT + DBType.PARENTHESIS_CLOSE;

        public static final String DELETE_TABLE = DBType.DROP_TABLE_IF_EXISTS + TABLE_NAME;
    }

    //clase interna para definir la tabla de toma de medicamentos
    public static abstract class Intakes implements BaseColumns {
        public static final String TABLE_NAME = "intakes";
        //al implementar BasColumns tenemos un campo _ID que representa el ID de la toma
        public static final String COLUMN_NAME_ID_MEDICINE = "idMedicine";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_DOSE = "dose";
        public static final String COLUMN_NAME_INTAKE_DATE = "intakeDate";
        //public static final String COLUMN_NAME_ = "";

        public static final String CREATE_TABLE =
                DBType.CREATE_TABLE + TABLE_NAME + DBType.PARENTHESIS_OPEN +
                        _ID + DBType.INTEGER_PRIMARY_KEY + DBType.COMMA +
                        COLUMN_NAME_ID_MEDICINE + DBType.INTEGER + DBType.COMMA +
                        COLUMN_NAME_DATE + DBType.TEXT + DBType.COMMA +
                        COLUMN_NAME_DOSE + DBType.TEXT + DBType.COMMA +
                        COLUMN_NAME_INTAKE_DATE + DBType.TEXT + DBType.PARENTHESIS_CLOSE;

        public static final String DELETE_TABLE = DBType.DROP_TABLE_IF_EXISTS + TABLE_NAME;
    }
}