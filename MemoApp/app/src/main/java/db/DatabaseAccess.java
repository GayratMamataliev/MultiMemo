package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gayrat.memoapp.memo;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {//Class for working with db
    private SQLiteDatabase database;
    private DatabaseOpenHelper openHelper;
    private static volatile DatabaseAccess instance;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static synchronized DatabaseAccess getInstance(Context context) {
        //Check whether database exist or not. If not, create new
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        //Function to pen db to work with
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        //function to close db
        if (database != null) {
            this.database.close();
        }
    }

    public void save(memo mem) {
        //Get all data from memo class and save them into db
        ContentValues values = new ContentValues();
        values.put("date", mem.getTime());
        values.put("memo", mem.getText());
        values.put("imagename", mem.getImageName());
        database.insert(DatabaseOpenHelper.TABLE, null, values);
    }

    public void update(memo mem) {
        //Update data
        ContentValues values = new ContentValues();
        values.put("date", mem.getTime());
        values.put("memo", mem.getText());
        values.put("imagename", mem.getImageName());
        String date = Long.toString(mem.getOldTime());
        database.update(DatabaseOpenHelper.TABLE, values, "date = ?", new String[]{date});
    }

    public void delete(memo mem) {
        //delete data
        String date = Long.toString(mem.getTime());
        database.delete(DatabaseOpenHelper.TABLE, "date = ?", new String[]{date});
    }

    public List getAllMemos() {
        //Get all data from db to display
        List memos = new ArrayList<> ();
        Cursor cursor = database.rawQuery("SELECT * From memo ORDER BY date DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long time = cursor.getLong(0);
            String text = cursor.getString(1);
            String imageName = cursor.getString(2);
            memos.add(new memo(time, text, imageName));
            cursor.moveToNext();
        }
        cursor.close();
        return memos;
    }
}