package translateapp.jason.com;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String TAG = "DatabaseHelper";
    private static final String TABLE_NAME = "history_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "sourceText";
    private static final String COL3 = "targetText";
    private static final ArrayList<String> listData = new ArrayList<>();
    private static final ArrayList<Integer> listIdData = new ArrayList<>();


    public DatabaseHelper(Context context)
    {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2 + " TEXT," + COL3 + " TEXT)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String sourceText, String targetText)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, sourceText);
        contentValues.put(COL3, targetText);

        Log.d(TAG, "addData: Adding " + sourceText + " to " + TABLE_NAME);
        Log.d(TAG, "addData: Adding " + targetText + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor getData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return data;
    }


    public int getItemID(String item)
    {
        int id = 0;
        int passId = 0;
        getItemName();
        for(int i = 0; i < listData.size(); i++)
        {
            if(listData.get(i).equals(item))
            {
               passId = listIdData.get(i);
               listData.clear();
               listIdData.clear();
               break;
            }

            else
            {
                continue;
            }
        }

        return passId;

    }

    public void getItemName()
    {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while(data.moveToNext())
        {
            listData.add(data.getString(1) + "\n" + data.getString(2));
            listIdData.add(data.getInt(0));
        }


    }

    public void deleteData(int id)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        String query="";
        query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + " = '" + id + "'";
        db.execSQL(query);

    }

}
