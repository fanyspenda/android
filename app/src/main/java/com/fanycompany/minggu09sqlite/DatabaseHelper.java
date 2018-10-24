package com.fanycompany.minggu09sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "user_database";
    private static final String TABLE_USER = "users";
    private static final String TABLE_USER_HOBBY = "users_hobby";
    private static final String TABLE_USER_CITY = "users_city";
    private static final String KEY_ID = "id";
    private static final String KEY_FIRSTNAME = "name";
    private static final String KEY_HOBBY = "hobby";
    private static final String KEY_CITY = "city";

    private static final String CREATE_TABLE_STUDENTS = "CREATE TABLE " +TABLE_USER+
            "(" +KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +KEY_FIRSTNAME+" TEXT)";

    private static final String CREATE_TABLE_USER_HOBBY = "CREATE TABLE " +TABLE_USER_HOBBY+
            " (" +KEY_ID+" INTEGER, "+KEY_HOBBY+" TEXT)";

    private static final String CREATE_TABLE_USER_CITY = "CREATE TABLE " +TABLE_USER_CITY+
            "(" +KEY_ID+" INTEGER, "+KEY_CITY+" TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("table", CREATE_TABLE_STUDENTS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STUDENTS);
        db.execSQL(CREATE_TABLE_USER_CITY);
        db.execSQL(CREATE_TABLE_USER_HOBBY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS '" +TABLE_USER+"'");
        db.execSQL("DROP TABLE IF EXISTS '" +CREATE_TABLE_USER_HOBBY+"'");
        db.execSQL("DROP TABLE IF EXISTS '" +CREATE_TABLE_USER_CITY+"'");
        onCreate(db);
    }

    public void addUser(String name, String hobby, String city){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_FIRSTNAME, name);
        long id = db.insertWithOnConflict(TABLE_USER, null, values,
                SQLiteDatabase.CONFLICT_IGNORE);

        //adding user hobby in users_hobby table
        ContentValues valuesHobby = new ContentValues();
        valuesHobby.put(KEY_ID, id);
        valuesHobby.put(KEY_HOBBY, hobby);
        db.insert(TABLE_USER_HOBBY, null, valuesHobby);

        //adding user city in users_city table
        ContentValues valuesCity = new ContentValues();
        valuesCity.put(KEY_ID, id);
        valuesCity.put(KEY_CITY, city);
        db.insert(TABLE_USER_CITY, null, valuesCity);
    }

    public ArrayList<UserModel> getAllUsers() {
        ArrayList<UserModel> userModelArrayList = new ArrayList<>();

        String selectQuery = "SELECT * FROM "+TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()){
            do{
                UserModel userModel = new UserModel();
                userModel.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                userModel.setName(c.getString(c.getColumnIndex(KEY_FIRSTNAME)));

                String selectHobbyQuery = "SELECT * FROM "+TABLE_USER_HOBBY+" WHERE "+
                        KEY_ID+" = "+userModel.getId();
                Log.d("oppp", selectHobbyQuery);

                Cursor cHobby = db.rawQuery(selectHobbyQuery, null);

                if(cHobby.moveToFirst()){
                    do {
                        userModel.setHobby(cHobby.getString(cHobby.getColumnIndex(KEY_HOBBY)));
                    } while (cHobby.moveToNext());
                }

                //getting user city where id = id from user_city table
                String selectCityQuery = "SELECT * FROM "+TABLE_USER_CITY+" WHERE "+KEY_ID+" = "
                        +userModel.getId();
                Cursor cCity = db.rawQuery(selectCityQuery, null);
                if(cCity.moveToFirst()){
                    do {
                        userModel.setCity(cCity.getString(cCity.getColumnIndex(KEY_CITY)));
                    }while (cCity.moveToNext());
                }
                userModelArrayList.add(userModel);
            }while (c.moveToNext());
        }
        return userModelArrayList;
    }

    public void updateUser(int id, String name, String hobby, String city){
        SQLiteDatabase db = this.getWritableDatabase();

        //updating name in users table
        ContentValues values = new ContentValues();
        values.put(KEY_FIRSTNAME, name);

        db.update(TABLE_USER, values, KEY_ID +" = ?", new String[]{String.valueOf(id)});

        //updating hobby in users_hobby table
        ContentValues valuesHobby = new ContentValues();
        valuesHobby.put(KEY_HOBBY, hobby);
        db.update(TABLE_USER_HOBBY, valuesHobby, KEY_ID+"= ?", new String[]{String.valueOf(id)});

        //updating city in users_city table
        ContentValues valuesCity = new ContentValues();
        valuesCity.put(KEY_CITY, city);
        db.update(TABLE_USER_CITY, valuesCity, KEY_ID + " = ?", new String[]{String.valueOf(id)});

    }

    public void deleteUser(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        //deleting from user table
        db.delete(TABLE_USER, KEY_ID+" = ?", new String[]{String.valueOf(id)});

        //deleting from hobby table
        db.delete(TABLE_USER_HOBBY, KEY_ID+" = ?", new String[]{String.valueOf(id)});

        //deleting from city tablee
        db.delete(TABLE_USER_CITY, KEY_ID+" = ?", new String[]{String.valueOf(id)});
    }
}
