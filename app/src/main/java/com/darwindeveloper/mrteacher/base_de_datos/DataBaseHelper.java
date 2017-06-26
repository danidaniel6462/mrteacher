package com.darwindeveloper.mrteacher.base_de_datos;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by DARWIN on 20/1/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private Context mycontext;

    private String DB_PATH;
    public static String DB_NAME = "MrTeacher.db";//the extension may be .sqlite or .db
    private static String ASSETS_DB_NAME = "mr_teacherv2.sqlite";
    private SQLiteDatabase myDataBase;
    private static int data_base_version = 1;


    public DataBaseHelper(Context context) throws IOException {
        super(context, DB_NAME, null, data_base_version);
        this.mycontext = context;


        DB_PATH = "/data/data/" + mycontext.getApplicationContext().getPackageName() + "/databases/";
        boolean dbexist = checkdatabase();
        if (dbexist) {
            //System.out.println("Database exists");
            opendatabase();
        } else {
            System.out.println("Database doesn't exist");
            createdatabase();
        }
    }

    private void createdatabase() throws IOException {
        boolean dbexist = checkdatabase();
        if (dbexist) {
            //System.out.println(" Database exists.");
        } else {
            this.getReadableDatabase();
            copydatabase();
        }
    }

    private boolean checkdatabase() {
        //SQLiteDatabase checkdb = null;
        boolean checkdb = false;
        try {
            String myPath = DB_PATH + DB_NAME;
            File dbfile = new File(myPath);
            //checkdb = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
            checkdb = dbfile.exists();
            if (checkdb) {
                System.out.println("database yes");
            }
        } catch (SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    private void copydatabase() {

        try {
            //Open your local db as the input stream
            InputStream myinput = mycontext.getAssets().open(ASSETS_DB_NAME);

            // Path to the just created empty db
            String outfilename = DB_PATH + DB_NAME;

            //Open the empty db as the output stream
            OutputStream myoutput = new FileOutputStream(outfilename);

            // transfer byte to inputfile to outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myinput.read(buffer)) > 0) {
                myoutput.write(buffer, 0, length);
            }

            //Close the streams
            myoutput.flush();
            myoutput.close();
            myinput.close();

        } catch (IOException e) {
            Log.e("ERROR COPY DB ", e.getMessage());
        } catch (Exception e) {
            Log.e("ERROR COPY DB ", e.getMessage());
        }


    }

    public void opendatabase() throws SQLException {
        //Open the database
        String mypath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close() {
        if (myDataBase != null) {
            myDataBase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    }
}