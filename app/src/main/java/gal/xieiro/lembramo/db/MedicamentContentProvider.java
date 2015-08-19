package gal.xieiro.lembramo.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MedicamentContentProvider extends ContentProvider {

    public static final Uri CONTENT_URI =
            Uri.parse("content://gal.xieiro.lembramo.provider/medicamentos");
    private static final int ALLROWS = 1;
    private static final int SINGLE_ROW = 2;

    private DBAdapter mDBAdapter;
    private static final UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI("gal.xieiro.lembramo.provider", "medicamentos", ALLROWS);
        mUriMatcher.addURI("gal.xieiro.lembramo.provider", "medicamentos/#", SINGLE_ROW);
    }

    @Override
    public boolean onCreate() {
        mDBAdapter = new DBAdapter(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case ALLROWS:
                return "vnd.android.cursor.dir/vnd.xieiro.medicament";
            case SINGLE_ROW:
                return "vnd.android.cursor.item/vnd.xieiro.medicament";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Open a read-only database.
        SQLiteDatabase db = mDBAdapter.getWritableDatabase();

        // Replace these with valid SQL statements if necessary.
        String groupBy = null;
        String having = null;

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DBContract.Medicamentos.TABLE_NAME);

        // If this is a row query, limit the result set to the passed in row.
        switch (mUriMatcher.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(DBContract.Medicamentos._ID + "=" + rowID);
            default:
                break;
        }

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, groupBy, having, sortOrder);

        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Open a read / write database to support the transaction.
        SQLiteDatabase db = mDBAdapter.getWritableDatabase();

        // If this is a row URI, limit the deletion to the specified row.
        switch (mUriMatcher.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                selection = DBContract.Medicamentos._ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
            default:
                break;
        }

        // To return the number of deleted items, you must specify a where
        // clause. To delete all rows and return a value, pass in "1".
        if (selection == null)
            selection = "1";

        // Execute the deletion.
        int deleteCount = db.delete(DBContract.Medicamentos.TABLE_NAME, selection, selectionArgs);

        // Notify any observers of the change in the data set.
        getContext().getContentResolver().notifyChange(uri, null);

        return deleteCount;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Open a read / write database to support the transaction.
        SQLiteDatabase db = mDBAdapter.getWritableDatabase();

        // To add empty rows to your database by passing in an empty Content Values
        // object, you must use the null column hack parameter to specify the name of
        // the column that can be set to null.
        String nullColumnHack = null;

        // Insert the values into the table
        long id = db.insert(DBContract.Medicamentos.TABLE_NAME,
                nullColumnHack, values);

        if (id > -1) {
            // Construct and return the URI of the newly inserted row.
            Uri insertedId = ContentUris.withAppendedId(CONTENT_URI, id);

            // Notify any observers of the change in the data set.
            getContext().getContentResolver().notifyChange(insertedId, null);

            return insertedId;
        } else
            return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // Open a read / write database to support the transaction.
        SQLiteDatabase db = mDBAdapter.getWritableDatabase();

        // If this is a row URI, limit the deletion to the specified row.
        switch (mUriMatcher.match(uri)) {
            case SINGLE_ROW:
                String rowID = uri.getPathSegments().get(1);
                selection = DBContract.Medicamentos._ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ?
                        " AND (" + selection + ')' : "");
            default:
                break;
        }

        // Perform the update.
        int updateCount = db.update(DBContract.Medicamentos.TABLE_NAME,
                values, selection, selectionArgs);

        // Notify any observers of the change in the data set.
        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }
}
