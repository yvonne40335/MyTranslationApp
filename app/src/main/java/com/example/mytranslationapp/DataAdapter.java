package com.example.mytranslationapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yvonne40335 on 2017/10/5.
 */

public class DataAdapter {
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb, mDb2;
    private Database mDbHelper;
    public static final String TABLE_NAME = "favorites";

    public DataAdapter(Context context)
    {
        this.mContext = context;
        mDbHelper = new Database(mContext);
    }

    public DataAdapter createDatabase() throws SQLException
    {
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DataAdapter open() throws SQLException
    {
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close()
    {
        mDbHelper.close();
    }

    public Cursor getTestData(String lookup)
    {
        try
        {
            char first = lookup.toLowerCase().charAt(0);
            String table_name = "alphabat_"+first;
            String sql =" SELECT * FROM '"+table_name+"' WHERE vocabulary LIKE '"+lookup+"'";
            //String sql =" SELECT * FROM word";
            Cursor mCur = mDb.rawQuery(sql, null);

            if (mCur!=null)
            {
                mCur.moveToNext();
            }
            return mCur;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public void addToFavorites(String word)
    {
        mDb2 = mDbHelper.getReadableDatabase();
        String query = String.format("INSERT INTO favorites (name) VALUES ('%s');",word);
        Log.v("insert",query);
        mDb2.execSQL(query);
        //ContentValues contentValues = new ContentValues();
        //contentValues.put("name",word);
        //long result = mDb2.insert(TABLE_NAME,null,contentValues);
        //if(result==-1)
         //   Log.v("insert","fail");
        //else
         //   Log.v("insert","good");
        mDb2.close();
    }

    public void removeFromFavorites(String word)
    {
        mDb2 = mDbHelper.getWritableDatabase();
        String query = String.format("DELETE FROM favorites WHERE name='%s';",word);
        mDb2.execSQL(query);
        mDb2.close();
    }

    public boolean isFavorite(String word)
    {
        String query = String.format("SELECT * FROM favorites WHERE name LIKE '%s';",word);
        Cursor cursor = mDb.rawQuery(query,null);
        if (cursor.getCount() <= 0)
        {
            cursor.close();
            Log.v("select","false");
            return false;
        }
        cursor.close();
        Log.v("select","true");
        return true;
    }

    /*public List<Vocabulary> getAllData() {
        List<Vocabulary> list = new ArrayList<>();
        mDb = mDbHelper.getReadableDatabase();
        String query = String.format("SELECT * FROM favorites ORDER BY word;");
        Cursor cursor = mDb.rawQuery(query, null);
        while (cursor.moveToNext()) {
            // int index2 = cursor.getColumnIndex(DataBaseHelper.NAME);
            String name = cursor.getString(1);
            Vocabulary word = new Vocabulary();
            list.add(word);
        }
        return list;
    }*/

    public Cursor getFavData()
    {
        try
        {
            String query = String.format("SELECT * FROM favorites ORDER BY name;");
            Cursor cursor = mDb.rawQuery(query, null);
            return cursor;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

}
