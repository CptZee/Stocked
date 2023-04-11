package com.example.myapplication.Data.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myapplication.Data.Credential;

import java.util.ArrayList;
import java.util.List;

public class CredentialHelper extends SQLiteOpenHelper {
    private static CredentialHelper instance;

    public CredentialHelper(@Nullable Context context) {
        super(context, "db_Inventory", null, 1);
    }

    public static CredentialHelper instance(Context context) {
        if (instance == null)
            instance = new CredentialHelper(context);
        return instance;
    }

    private final String TABLENAME = "tbl_Credentials";
    private final SQLiteDatabase db = getWritableDatabase();
    private final SQLiteDatabase dbr = getReadableDatabase();

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME + "(" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "accountID INTEGER," +
                    "username TEXT," +
                    "password TEXT," +
                    "archived INTEGER)");
            Log.i(TABLENAME + " Logger", "Successfully created the table");
        } catch (SQLiteException e) {
            Log.e(TABLENAME + " Logger", "Unable to create the table", e.getCause());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE " + TABLENAME);
            Log.i(TABLENAME + " Logger", "Successfully inserted into the table");
            onCreate(db);
        } catch (SQLiteException e) {
            Log.e("DBHelper", "Unable to upgrade the table", e.getCause());
        }
    }

    public void insert(Credential data) {
        try {
            db.insert(TABLENAME, null, prepareData(data));
            Log.i(TABLENAME + " Logger", "Successfully inserted into the table");
        } catch (SQLiteException e) {
            Log.e(TABLENAME + " Logger", "Unable to insert into the table", e.getCause());
        }
    }

    public List<Credential> get() {
        List<Credential> list = new ArrayList<>();
        try {
            Cursor cursor = dbr.rawQuery("SELECT ID, accountID, username, password FROM " + TABLENAME + " WHERE archived = 0",
                    null);
            while (cursor.moveToNext())
                list.add(prepareData(cursor));
            Log.i(TABLENAME + " Logger", "Successfully retrieved data from the table");
        } catch (SQLiteException e) {
            Log.e(TABLENAME + " Logger", "Unable to retrieved data from the table", e.getCause());
        }
        return list;
    }

    public Credential get(int ID) {
        Credential data = null;
        try {
            Cursor cursor = dbr.rawQuery("SELECT ID, accountID, username, password FROM " + TABLENAME + " WHERE ID = ?",
                    new String[]{String.valueOf(ID)});
            while (cursor.moveToNext())
                data = prepareData(cursor);
            Log.i(TABLENAME + " Logger", "Successfully retrieved data from the table");
        } catch (SQLiteException e) {
            Log.e(TABLENAME + " Logger", "Unable to retrieved data from the table", e.getCause());
        }
        return data;
    }

    public void update(Credential data) {
        try {
            db.update(TABLENAME, prepareData(data), "ID = ?", new String[]{String.valueOf(data.getID())});
            Log.i(TABLENAME + " Logger", "Successfully update data from the table");
        } catch (SQLiteException e) {
            Log.e(TABLENAME + " Logger", "Unable to update data from the table", e.getCause());
        }
    }

    public void remove(Credential data) {
        try {
            ContentValues values = prepareData(data);
            values.put("archived", 1);
            db.update(TABLENAME, values, "ID = ?", new String[]{String.valueOf(data.getID())});
            Log.i(TABLENAME + " Logger", "Successfully removed data from the table");
        } catch (SQLiteException e) {
            Log.e(TABLENAME + " Logger", "Unable to removed data from the table", e.getCause());
        }
    }

    public int getNextID() {
        int ID = 0;
        try {
            Cursor cursor = dbr.rawQuery("SELECT MAX(ID) FROM " + TABLENAME,
                    null);
            while (cursor.moveToNext())
                ID = cursor.getInt(0);
            ID++;
            Log.i(TABLENAME + " Logger", "Successfully retrieved data from the table");
        } catch (SQLiteException e) {
            Log.e(TABLENAME + " Logger", "Unable to retrieved data from the table", e.getCause());
        }
        return ID;
    }

    private ContentValues prepareData(Credential data) {
        ContentValues contentValues = new ContentValues();
        if(data.getAccountID() != 0)
            contentValues.put("accountID", data.getAccountID());
        if (data.getUsername() != null)
            contentValues.put("username", data.getUsername());
        if (data.getPassword() != null)
            contentValues.put("password", data.getPassword());
        contentValues.put("archived", 0);
        return contentValues;
    }

    private Credential prepareData(Cursor cursor) {
        Credential credential = new Credential();
        credential.setID(cursor.getInt(0));
        credential.setAccountID(cursor.getInt(1));
        credential.setUsername(cursor.getString(2));
        credential.setPassword(cursor.getString(3));
        return credential;
    }
}
