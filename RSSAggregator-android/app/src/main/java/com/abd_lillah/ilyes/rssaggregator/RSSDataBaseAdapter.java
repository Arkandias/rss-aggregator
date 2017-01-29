package com.abd_lillah.ilyes.rssaggregator;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RSSDataBaseAdapter
{
    static final String DATABASE_NAME = "rss.db";
    static final int DATABASE_VERSION = 1;
    public static final int NAME_COLUMN = 1;
    // TODO: Create public field for each column in your table.
    // SQL Statement to create a new database.
    static final String DATABASE_CREATE = "create table "+ "RSS"+
            "( " +"ID"+" integer primary key autoincrement,"+ "LINK  text,TITLE text, USER_ID integer); ";
    // Variable to hold the database instance
    public  SQLiteDatabase db;
    // Context of the application using the database.
    private final Context context;
    // Database open/upgrade helper
    private DataBaseHelper dbHelper;
    public RSSDataBaseAdapter(Context _context)
    {
        context = _context;
        dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public RSSDataBaseAdapter open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }
    public void close()
    {
        db.close();
    }

    public  SQLiteDatabase getDatabaseInstance()
    {
        return db;
    }

    public void insertEntry(String link,String title, int user_id)
    {
        Log.v("Cred", link + " " + title);
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("USER_ID", user_id);
        newValues.put("LINK", link);
        newValues.put("TITLE",title);

        // Insert the row into your table
        db.insert("RSS", null, newValues);
        ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
    }
    public int deleteEntry(String link)
    {
        //String id=String.valueOf(ID);
        String where="LINK=?";
        int numberOFEntriesDeleted= db.delete("RSS", where, new String[]{link}) ;
        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        return numberOFEntriesDeleted;
    }
    public String getSingleEntry(String link)
    {
        Cursor cursor=db.query("RSS", null, " LINK=?", new String[]{link}, null, null, null);
        if(cursor.getCount()<1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String title= cursor.getString(cursor.getColumnIndex("TITLE"));
        cursor.close();
        return title;
    }

    public List<String> getTitles(int userID) {
        Cursor cursor = db.query("RSS", null, " userID=?", new String[]{String.valueOf(userID)}, null, null, null);

        List<String> titles = new ArrayList<>();
        if (cursor.moveToFirst()) {
            titles.add(cursor.getString(cursor.getColumnIndex("TITLE")));
            while (cursor.moveToNext()) {
                titles.add(cursor.getString(cursor.getColumnIndex("TITLE")));
            }
        }
        return titles;
    }

    public List<String> getLinks(int userID) {
        Cursor cursor = db.rawQuery("select * from RSS where USER_ID=" + userID, null);

        List<String> links = new ArrayList<>();
        if (cursor.moveToFirst()) {
            links.add(cursor.getString(cursor.getColumnIndex("LINK")));
            while (cursor.moveToNext()) {
                links.add(cursor.getString(cursor.getColumnIndex("LINK")));
            }
        }
        return links;
    }

    public void  updateEntry(String link,String title)
    {
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put("LINK", link);
        updatedValues.put("TITLE",title);

        String where="TITLE = ?";
        db.update("LINK",updatedValues, where, new String[]{link});
    }
}