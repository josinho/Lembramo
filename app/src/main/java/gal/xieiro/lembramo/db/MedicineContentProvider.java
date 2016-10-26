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

public class MedicineContentProvider extends ContentProvider {

    private static final String AUTHORITY = "gal.xieiro.lembramo.provider";
    private static final String PATH_MEDICINES = "medicines";
    private static final String PATH_INTAKES = "intakes";

    public static final Uri CONTENT_URI_MEDICINES =
            Uri.parse("content://" + AUTHORITY + "/" + PATH_MEDICINES);
    public static final Uri CONTENT_URI_INTAKES =
            Uri.parse("content://" + AUTHORITY + "/" + PATH_INTAKES);


    private static final int MEDICINES = 10;
    private static final int MEDICINES_ID = 20;
    private static final int INTAKES = 30;
    private static final int INTAKES_ID = 40;

    private DBAdapter mDBAdapter;
    private static final UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, PATH_MEDICINES, MEDICINES);
        mUriMatcher.addURI("gal.xieiro.lembramo.provider", PATH_MEDICINES + "/#", MEDICINES_ID);
        mUriMatcher.addURI(AUTHORITY, PATH_INTAKES, INTAKES);
        mUriMatcher.addURI("gal.xieiro.lembramo.provider", PATH_INTAKES + "/#", INTAKES_ID);
    }

    @Override
    public boolean onCreate() {
        mDBAdapter = new DBAdapter(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case MEDICINES:
                return "vnd.android.cursor.dir/vnd.xieiro.medicine";
            case MEDICINES_ID:
                return "vnd.android.cursor.item/vnd.xieiro.medicine";
            case INTAKES:
                return "vnd.android.cursor.dir/vnd.xieiro.intake";
            case INTAKES_ID:
                return "vnd.android.cursor.item/vnd.xieiro.intake";
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

        // If this is a row query, limit the result set to the passed in row.
        switch (mUriMatcher.match(uri)) {
            case MEDICINES_ID:
                queryBuilder.appendWhere(DBContract.Medicines._ID + "="
                        + uri.getLastPathSegment());
            case MEDICINES:
                queryBuilder.setTables(DBContract.Medicines.TABLE_NAME);
                break;
            case INTAKES_ID:
                queryBuilder.appendWhere(DBContract.Intakes._ID + "="
                        + uri.getLastPathSegment());
            case INTAKES:
                queryBuilder.setTables(DBContract.Intakes.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, groupBy, having, sortOrder);

        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Open a read / write database to support the transaction.
        SQLiteDatabase db = mDBAdapter.getWritableDatabase();
        String rowID = uri.getLastPathSegment();
        int action = mUriMatcher.match(uri);
        int deleteCount;

        // If this is a row URI, limit the deletion to the specified row.
        switch (action) {
            case MEDICINES_ID:
                selection = DBContract.Medicines._ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                break;
            case INTAKES_ID:
                selection = DBContract.Intakes._ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                break;
            default:
                break;
        }

        // To return the number of deleted items, you must specify a where
        // clause. To delete all rows and return a value, pass in "1".
        if (selection == null)
            selection = "1";

        switch (action) {
            case MEDICINES:
            case MEDICINES_ID:
                deleteCount = db.delete(DBContract.Medicines.TABLE_NAME, selection, selectionArgs);
                break;
            case INTAKES:
            case INTAKES_ID:
                deleteCount = db.delete(DBContract.Intakes.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

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
        long id;
        Uri insertedId;

        switch (mUriMatcher.match(uri)) {
            case MEDICINES_ID:
            case MEDICINES:
                // Insert the values into the table
                id = db.insert(DBContract.Medicines.TABLE_NAME, nullColumnHack, values);
                // Construct and return the URI of the newly inserted row.
                insertedId = ContentUris.withAppendedId(CONTENT_URI_MEDICINES, id);
                break;
            case INTAKES_ID:
            case INTAKES:
                // Insert the values into the table
                id = db.insert(DBContract.Intakes.TABLE_NAME, nullColumnHack, values);
                // Construct and return the URI of the newly inserted row.
                insertedId = ContentUris.withAppendedId(CONTENT_URI_INTAKES, id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (id > -1) {
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
        String rowID = uri.getLastPathSegment();
        int updateCount = 0;

        // If this is a row URI, limit the deletion to the specified row.
        switch (mUriMatcher.match(uri)) {
            case MEDICINES_ID:
                selection = DBContract.Medicines._ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                updateCount = db.update(DBContract.Medicines.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case INTAKES_ID:
                selection = DBContract.Intakes._ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
                updateCount = db.update(DBContract.Intakes.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            default:
                break;
        }

        // Notify any observers of the change in the data set.
        getContext().getContentResolver().notifyChange(uri, null);

        return updateCount;
    }
}
