package com.bookies.bookkeeper;

/**
 * Handles sqlite database activites - including:
 *  - creating new sqlite databse
 *  - adding book
 *  - getting book information
 *  - getting all books
 *  - update book details
 *  - deleting book
 * 
 * Created by Heather on 1/13/2015.
 */

//import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
    //all Static variables
    //Logcat tag
    private static final String LOG = "DatabaseHelper";

    //DB version
    private static final int DATABASE_VERSION = 1;

    //DB name
    private static final String DATABASE_NAME = "BookDB";

    //table name
    private static final String TABLE_BOOKS = "books";

    //Library Table Column names
    private static final String KEY_ID = "id";
    private static final String KEY_ISBN = "isbn";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_STATUS = "status";  
    private static final String KEY_RATING = "rating";  
    private static final String KEY_DATE_READ = "dateRead";
    private static final String KEY_COMMENTS = "comments";
    private static final String KEY_OWNED = "owned";  

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Build query to create books table
        String CREATE_BOOK_TABLE = "CREATE TABLE books ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_ISBN + " TEXT, "+
                KEY_TITLE + " TEXT, "+
                KEY_AUTHOR + " TEXT, " +
                KEY_STATUS + " INTEGER, "+
                KEY_RATING + " INTEGER, " +
                KEY_DATE_READ + " TEXT, " +
                KEY_COMMENTS + " TEXT, " + 
                KEY_OWNED + " INTEGER) ";
        //create table
        db.execSQL(CREATE_BOOK_TABLE);
    }

    //Upgrading Table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);

        //create tables again
        onCreate(db);
    }

    //Adding new book
    public void addBook(Book book){
        Log.d("addBook", book.toString());
        //get writeable database
        SQLiteDatabase db = this.getWritableDatabase();
        //Create ContentValues and add items
        ContentValues values = new ContentValues();
        values.put(KEY_ISBN, book.getIsbn());
        values.put(KEY_TITLE, book.getTitle());
        values.put(KEY_AUTHOR, book.getAuthor());
        values.put(KEY_STATUS, book.getStatus());
        values.put(KEY_RATING, book.getRating());
        values.put(KEY_DATE_READ, book.getDateRead());
        values.put(KEY_COMMENTS, book.getComments());
        values.put(KEY_OWNED, book.getOwned());
        //insert into table
        db.insert(TABLE_BOOKS, null, values);
        //close database
        db.close();
    }

    //Getting single book
    public Book getBook(int id){
        //get readable database
        SQLiteDatabase db = this.getReadableDatabase();
        //query
        String selectQuery = "SELECT * FROM " + TABLE_BOOKS + " WHERE "
                + KEY_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery,  null);

        //if we get results, start at the beginning
        if (c != null){
            c.moveToFirst();
        }

        //build & return book
        Book b= new Book();
        b.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        b.setIsbn(c.getString(c.getColumnIndex(KEY_ISBN)));
        b.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
        b.setAuthor(c.getString(c.getColumnIndex(KEY_AUTHOR)));
        b.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
        b.setRating(c.getInt(c.getColumnIndex(KEY_RATING)));
        b.setDateRead(c.getString(c.getColumnIndex(KEY_DATE_READ)));
        b.setComments(c.getString(c.getColumnIndex(KEY_COMMENTS)));
        b.setOwned(c.getInt(c.getColumnIndex(KEY_OWNED)));

        return b;
    }

    //Getting all books
    public List<Book> getAllBooks(){
        List<Book> books = new LinkedList<Book>();
        //build query
        String selectQuery = "SELECT * FROM " + TABLE_BOOKS;
        //get writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery,  null);

        //looping through all rows and adding to list
        Book book = null;
        if(c.moveToFirst()){
            do{
                book = new Book();
                book.setId(Integer.parseInt(c.getString(0)));
                book.setIsbn(c.getString(1));
                book.setTitle(c.getString(2));
                book.setAuthor(c.getString(3));
                book.setStatus(Integer.parseInt(c.getString(4)));
                book.setRating(Integer.parseInt(c.getString(5)));
                book.setDateRead(c.getString(6));
                book.setComments(c.getString(7));
                book.setOwned(Integer.parseInt(c.getString(8)));

                //adding to book list
                books.add(book);
            }while(c.moveToNext());
        }
        return books;
    }

    //Updating single book
    public int updateBook(int id, Book book){
        //get writeable database
        SQLiteDatabase db = this.getWritableDatabase();

        //build values and add values
        ContentValues values = new ContentValues();
        values.put(KEY_ISBN, book.getIsbn());
        values.put(KEY_TITLE, book.getTitle());
        values.put(KEY_AUTHOR, book.getAuthor());
        values.put(KEY_STATUS, book.getStatus());
        values.put(KEY_RATING, book.getRating());
        values.put(KEY_DATE_READ, book.getDateRead());
        values.put(KEY_COMMENTS, book.getComments());
        values.put(KEY_OWNED, book.getOwned());

        //convert id to String
        String ID = Integer.toString(id);

        //update database
        int i =  db.update(TABLE_BOOKS, values, KEY_ID + " = ?",
                new String[] { ID });
        //close database & return
        db.close();

        return i;
    }

    //Deleting single book
    public void detletBook(int id){
        //get writeable database
        SQLiteDatabase db = this.getWritableDatabase();

        //convert id to String
        String ID = Integer.toString(id);

        //delete book
        db.delete(TABLE_BOOKS, KEY_ID + " = ?",
                new String[] { ID });

        //close db
        db.close();
    }
}

