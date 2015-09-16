package gal.xieiro.lembramo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    static final String TAG = "DBAdapter";

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public DBAdapter(Context context) {
        mDBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DBContract.DBInfo.DBNAME, null, DBContract.DBInfo.DBVERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DBContract.Medicamentos.CREATE_TABLE);
                Log.v(TAG, "Base de datos creada con éxito");
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG, "Error creando base de datos");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Actualizando Base de datos desde version " + oldVersion + " a "
                    + newVersion + ", lo que destruirá todos los datos");
            db.execSQL(DBContract.Medicamentos.DELETE_TABLE);
            onCreate(db);
        }
    }

    // abrir base de datos
    public DBAdapter open() throws SQLException {
        mSQLiteDatabase = mDBHelper.getWritableDatabase();
        return this;
    }

    // cerrar base de datos
    public void close() {
        mDBHelper.close();
    }

    public SQLiteDatabase getWritableDatabase() throws SQLException {
        open();
        return mSQLiteDatabase;
    }
}
