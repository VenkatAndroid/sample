package com.mobi.bright.minerva.sql_lite;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import com.mobi.bright.util.LogUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SqlLiteHelper extends SQLiteOpenHelper {

  private static final String TAG = LogUtil.createTag(SqlLiteHelper.class);
  private static final int DATABASE_VERSION = 3;
  //The Android's default system path of your application database.
  static String DATABASE_PATH = null;
  private static String databaseName = "";
  private static Context myContext;
  private static long DATA_SIZE_FLOOR = 40;
  private SQLiteDatabase myDataBase;

  public SqlLiteHelper(Context context,
                       String dataBaseNameIn) {

    super(context,
          dataBaseNameIn,
          null,
          DATABASE_VERSION);

    databaseName = dataBaseNameIn;

    DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";

    Log.d(TAG, "DATABASE_PATH=" + DATABASE_PATH);
    Log.d(TAG, "databaseName=" + databaseName );

    myContext = context;
  }

  public static SQLiteDatabase openDatabase() {

    try {
      return SQLiteDatabase.openDatabase(DATABASE_PATH + databaseName,
                                         null,
                                         SQLiteDatabase.OPEN_READWRITE |
                                         SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }
    catch (Throwable t) {
      Log.e(TAG, "Cant find or open DB file= " + DATABASE_PATH + databaseName, t);
      throw new RuntimeException("Cant find or open DB file= " + DATABASE_PATH + databaseName, t);
    }
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {

  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase,
                        int i,
                        int i1) {

  }

  private long getDataMegsAvailable() {
    StatFs stat = new StatFs(Environment.getDataDirectory()
                                        .getPath());
    long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
    long megAvailable = bytesAvailable / 1048576;

    return megAvailable;
  }

  @Override
  public synchronized void close() {
    if (myDataBase != null) {
      myDataBase.close();
    }

    super.close();
  }

  /**
   * Creates a empty database on the system and rewrites it with your own database.
   */
  public void createDataBase(Activity activity)
          throws
          IOException, DataSizeException {
    boolean dbExist = checkDataBase(activity);

    if (dbExist) {
      //do nothing - database already exist
    } else {
      long dataMegs = getDataMegsAvailable();

      if (dataMegs < DATA_SIZE_FLOOR) {
        throw new DataSizeException("Application requires " + DATA_SIZE_FLOOR +
                                    " megabytes for the database. You only have " +
                                    dataMegs + " megabytes available");
      }

      //By calling this method and empty database will be created into the default system path
      //of your application so we are gonna be able to overwrite that database with our database.
      SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

      try {
        copyDataBase();
      }
      catch (IOException e) {
        e.printStackTrace();
        throw new Error("Error copying database");
      } finally {
        sqLiteDatabase.close();
      }
    }
  }

  /**
   * Copies your database from your local assets-folder to the just created empty database in the
   * system folder, from where it can be accessed and handled.
   * This is done by transfering bytestream.
   */
  private void copyDataBase() throws IOException {

    Log.d("Mobi",
          "copying file + " + databaseName);

    //Open your local db as the input stream
    InputStream myInput = myContext.getAssets()
                                   .open(databaseName);

    // Path to the just created empty db
    String outFileName = DATABASE_PATH + databaseName;

    //Open the empty db as the output stream
    OutputStream myOutput = new FileOutputStream(outFileName);

    //transfer bytes from the inputfile to the outputfile
    byte[] buffer = new byte[1024];
    int length;
    while ((length = myInput.read(buffer)) > 0) {
      myOutput.write(buffer,
                     0,
                     length);
    }

    //Close the streams
    myOutput.flush();
    myOutput.close();
    myInput.close();

    Log.d("Mobi",
          "Done copying file + " + databaseName);
  }



  /**
   * Check if the database already exist to avoid re-copying the file each time you open the
   * application.
   *
   * @return true if it exists, false if it doesn't
   */
  private boolean checkDataBase(Activity activity) {
    SQLiteDatabase checkDB = null;

    try {
      String myPath = DATABASE_PATH + databaseName;

      Log.d("Mobi",
            "Db=" + myPath);

      checkDB = SQLiteDatabase.openDatabase(myPath,
                                            null,
                                            SQLiteDatabase.NO_LOCALIZED_COLLATORS |
                                            SQLiteDatabase.OPEN_READWRITE);
    }
    catch (Throwable e) {
      Log.d("Mobi",
            "Check Db caught" + e.getClass()
                                 .getName());

    }

    if (checkDB != null) {
      checkDB.close();
    }

    return checkDB != null ?
           true :
           false;
  }
}
