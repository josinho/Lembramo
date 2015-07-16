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

    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DBContract.DBInfo.DBNAME, null, DBContract.DBInfo.DBVERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DBContract.Medicamentos.CREATE_TABLE);
                Log.v(TAG,"Base de datos creada con éxito");
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG,"Error creando base de datos");
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
        db = DBHelper.getWritableDatabase();
        return this;
    }

    // cerrar base de datos
    public void close() {
        DBHelper.close();
    }

    // insertar un medicamento en la base de datos
    public long insertMedicamento(String nombre, String comentario, String fotocaja, String fotomedicamento) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(DBContract.Medicamentos.COLUMN_NAME_NAME, nombre);
        initialValues.put(DBContract.Medicamentos.COLUMN_NAME_COMMENT, comentario);
        initialValues.put(DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO, fotocaja);
        initialValues.put(DBContract.Medicamentos.COLUMN_NAME_MEDPHOTO, fotomedicamento);
        return db.insert(DBContract.Medicamentos.TABLE_NAME, null, initialValues);
    }

    // borrar un medicamento de la base de datos
    public boolean deleteMedicamento(long rowId) {
        return db.delete(DBContract.Medicamentos.TABLE_NAME, DBContract.Medicamentos._ID + " =" + rowId, null) > 0;
    }

    // devolver todos los medicamentos
    public Cursor getAllMedicamentos() {
        return db.query(
                DBContract.Medicamentos.TABLE_NAME,
                new String[]{DBContract.Medicamentos._ID,
                        DBContract.Medicamentos.COLUMN_NAME_NAME,
                        DBContract.Medicamentos.COLUMN_NAME_COMMENT,
                        DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO,
                        DBContract.Medicamentos.COLUMN_NAME_MEDPHOTO},
                null, null, null, null, null);
    }

    // devolver un medicamento concreto
    public Cursor getMedicamento(long rowId) throws SQLException {
        Cursor mCursor =
                db.query(true, DBContract.Medicamentos.TABLE_NAME,
                        new String[]{DBContract.Medicamentos._ID,
                                DBContract.Medicamentos.COLUMN_NAME_NAME,
                                DBContract.Medicamentos.COLUMN_NAME_COMMENT,
                                DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO,
                                DBContract.Medicamentos.COLUMN_NAME_MEDPHOTO},
                        DBContract.Medicamentos._ID + " =" + rowId,
                        null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    // actualizar un medicamento
    public boolean updateMedicamento(long rowId, String nombre, String comentario, String fotocaja, String fotomedicamento) {
        ContentValues args = new ContentValues();
        args.put(DBContract.Medicamentos.COLUMN_NAME_NAME, nombre);
        args.put(DBContract.Medicamentos.COLUMN_NAME_COMMENT, comentario);
        args.put(DBContract.Medicamentos.COLUMN_NAME_BOXPHOTO, fotocaja);
        args.put(DBContract.Medicamentos.COLUMN_NAME_MEDPHOTO, fotomedicamento);
        return db.update(DBContract.Medicamentos.TABLE_NAME, args, DBContract.Medicamentos._ID + " =" + rowId, null) > 0;
    }
}
