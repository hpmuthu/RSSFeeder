package com.example.vittal.rssfeeder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vittal on 5/3/17.
 */

public class MySQLiteHelper extends SQLiteOpenHelper{

    private Context context;

    public static final String TABLE_USERS              = "users";
    public static final String COLUMN_ID                = "_id";
    public static final String COLUMN_OAUTH_PROVIDER    = "oauth_provider";
    public static final String COLUMN_OAUTH_UID         = "oauth_uid";
    public static final String COLUMN_NAME              = "name";
    public static final String COLUMN_FIRST_NAME        = "first_name";
    public static final String COLUMN_LAST_NAME         = "last_name";
    public static final String COLUMN_EMAIL             = "email";
    public static final String COLUMN_PHOTO_URL         = "photo_url";

    public static final String TABLE_WEBSITES           = "websites";
    public static final String COLUMN_WEBSITE_ID        = "_id";
    public static final String COLUMN_USER_ID           = "user_id";
    public static final String COLUMN_TITLE             = "title";
    public static final String COLUMN_LINK              = "link";
    public static final String COLUMN_RSS_LINK          = "rss_link";
    public static final String COLUMN_DESCRIPTION       = "description";

    private static final String DATABASE_NAME = "rssfeeder.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String TABLE_CREATE_USERS = "create table "
            + TABLE_USERS + "( " + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_OAUTH_PROVIDER
            + " varchar(15) not null, " + COLUMN_OAUTH_UID
            + " varchar(100) not null, " + COLUMN_NAME
            + " varchar(100) not null, " + COLUMN_FIRST_NAME
            + " varchar(100) not null, " + COLUMN_LAST_NAME
            + " varchar(100) not null, " + COLUMN_EMAIL
            + " text not null, " + COLUMN_PHOTO_URL
            + " text not null);";

    // Database creation sql statement
    private static final String TABLE_CREATE_WEBSITES = "create table "
            + TABLE_WEBSITES + "( " + COLUMN_WEBSITE_ID
            + " integer primary key autoincrement, " + COLUMN_USER_ID
            + " integer not null, " + COLUMN_TITLE
            + " text not null, " + COLUMN_LINK
            + " text not null, " + COLUMN_RSS_LINK
            + " text not null, " + COLUMN_DESCRIPTION
            + " text not null);";

    public MySQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_USERS);
        db.execSQL(TABLE_CREATE_WEBSITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEBSITES);
        onCreate(db);
    }

    public boolean checkUserExists(String email){
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM "+ TABLE_USERS + " WHERE email = '"+ email +"';";
        Cursor cursor = db.rawQuery(query, null);

        int recordCount = cursor.getCount();

        if(recordCount > 0)
            return true;
        else
            return false;
    }

    /*
     * users table functions
     */
    public void addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_OAUTH_PROVIDER, "google");
        values.put(MySQLiteHelper.COLUMN_OAUTH_UID, user.getoAuthId());
        values.put(MySQLiteHelper.COLUMN_NAME, user.getName());
        values.put(MySQLiteHelper.COLUMN_FIRST_NAME, user.getFirstName());
        values.put(MySQLiteHelper.COLUMN_LAST_NAME, user.getLastName());
        values.put(MySQLiteHelper.COLUMN_EMAIL, user.getEmail());
        values.put(MySQLiteHelper.COLUMN_PHOTO_URL, user.getPhotoUrl());
        SQLiteDatabase db = getWritableDatabase();
        long insertId = db.insert(MySQLiteHelper.TABLE_USERS, null,
                values);

        String str_user_id = "" + insertId;
        SessionManager sessionManager = new SessionManager(context);
        sessionManager.createLoginSession(user.getName(),user.getEmail(),str_user_id);

        db.close();

    }

    public void updateUser(User user) {

        String email = user.getEmail();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_OAUTH_PROVIDER, "google");
        values.put(MySQLiteHelper.COLUMN_OAUTH_UID, user.getoAuthId());
        values.put(MySQLiteHelper.COLUMN_NAME, user.getName());
        values.put(MySQLiteHelper.COLUMN_FIRST_NAME, user.getFirstName());
        values.put(MySQLiteHelper.COLUMN_LAST_NAME, user.getLastName());
        values.put(MySQLiteHelper.COLUMN_EMAIL, user.getEmail());
        values.put(MySQLiteHelper.COLUMN_PHOTO_URL, user.getPhotoUrl());
        SQLiteDatabase db = getWritableDatabase();
        db.update(MySQLiteHelper.TABLE_USERS, values,COLUMN_EMAIL+"='"+email+"'",
                null);

        int user_id = getUserIdFromEmail(email);

        String str_user_id = "" + user_id;
        SessionManager sessionManager = new SessionManager(context);
        sessionManager.createLoginSession(user.getName(),user.getEmail(),str_user_id);
        db.close();

    }

    public int getUserIdFromEmail(String email){
        int user_id = 0;

        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM "+ TABLE_USERS + " WHERE email = '"+ email +"';";
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
           user_id = cursor.getColumnIndex(COLUMN_ID);
        }
        // make sure to close the cursor
        cursor.close();
        return user_id;
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<User>();

        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM "+ TABLE_USERS + ";";
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User user = cursorToUser(cursor);
            users.add(user);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return users;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();

        user.setoAuthId(cursor.getString(cursor.getColumnIndex(COLUMN_OAUTH_UID)));
        user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        user.setFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME)));
        user.setLastName(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_NAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
        user.setPhotoUrl(cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO_URL)));

        return user;
    }

    /*
     * users table functions end
     */

    /*
     * websites table functions
     */

    /**
     * Adding a new website in websites table Function will check if a site
     * already existed in database. If existed will update the old one else
     * creates a new row
     * */
    public void addSite(WebSite site) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, site.getUserId());
        values.put(COLUMN_TITLE, site.getTitle()); // site title
        values.put(COLUMN_LINK, site.getLink()); // site url
        values.put(COLUMN_RSS_LINK, site.getRSSLink()); // rss link url
        values.put(COLUMN_DESCRIPTION, site.getDescription()); // site description

        // Check if row already existed in database
        if (!isSiteExists(db, site.getRSSLink())) {
            // site not existed, create a new row
            db.insert(TABLE_WEBSITES, null, values);
            db.close();
        } else {
            // site already existed update the row
            updateSite(site);
            db.close();
        }
    }

    /**
     * Reading all rows from database
     * */
    public List<WebSite> getAllSites(Integer user_id) {
        List<WebSite> siteList = new ArrayList<WebSite>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_WEBSITES
                + " WHERE USER_ID = "+user_id+" ORDER BY _id DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                WebSite site = new WebSite();
                site.setId(Integer.parseInt(cursor.getString(0)));
                site.setUserId(Integer.parseInt(cursor.getString(1)));
                site.setTitle(cursor.getString(2));
                site.setLink(cursor.getString(3));
                site.setRSSLink(cursor.getString(4));
                site.setDescription(cursor.getString(5));
                // Adding contact to list
                siteList.add(site);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return contact list
        return siteList;
    }

    /**
     * Updating a single row row will be identified by rss link
     * */
    public int updateSite(WebSite site) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, site.getUserId());
        values.put(COLUMN_TITLE, site.getTitle());
        values.put(COLUMN_LINK, site.getLink());
        values.put(COLUMN_RSS_LINK, site.getRSSLink());
        values.put(COLUMN_DESCRIPTION, site.getDescription());

        // updating row return
        int update = db.update(TABLE_WEBSITES, values, COLUMN_RSS_LINK + " = ?",
                new String[] { String.valueOf(site.getRSSLink()) });
        db.close();
        return update;

    }

    /**
     * Reading a row (website) row is identified by row id
     * */
    public WebSite getSite(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_WEBSITES, new String[] { COLUMN_WEBSITE_ID, COLUMN_USER_ID, COLUMN_TITLE,
                        COLUMN_LINK, COLUMN_RSS_LINK, COLUMN_DESCRIPTION }, COLUMN_WEBSITE_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        WebSite site = new WebSite(cursor.getInt(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getString(5));

        site.setId(Integer.parseInt(cursor.getString(0)));
        site.setUserId(Integer.parseInt(cursor.getString(1)));
        site.setTitle(cursor.getString(2));
        site.setLink(cursor.getString(3));
        site.setRSSLink(cursor.getString(4));
        site.setDescription(cursor.getString(5));
        cursor.close();
        db.close();
        return site;
    }

    /**
     * Deleting single row
     * */
    public void deleteSite(WebSite site) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEBSITES, COLUMN_WEBSITE_ID + " = ?",
                new String[] { String.valueOf(site.getId())});
        db.close();
    }

    /**
     * Checking whether a site is already existed check is done by matching rss
     * link
     * */
    public boolean isSiteExists(SQLiteDatabase db, String rss_link) {

        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_WEBSITES
                + " WHERE rss_link = '" + rss_link + "'", new String[] {});
        boolean exists = (cursor.getCount() > 0);
        return exists;
    }

    /*
     * websites table functions end
     */
}
