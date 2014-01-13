package com.magrabbit.lib;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;

import com.magrabbit.qrcodescan.database.DatabaseTable;

/**
 * This class contains standardized methods for basic CRUD operations on SQLite
 * databases.
 * 
 * @Changes: Add functions to insert list of items.
 */
public class LibSQLiteDBControl implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1185427054095199090L;
	// Declare some references to TDSQLiteOpenHelper
	private LibSQLiteOpenHelper mTDSQLiteOpenHelper;
	/*
	 * private Context mContext; private String mDbName; private int mDbVersion;
	 * private HashMap<String, String> mCreateTableStmts; private
	 * HashMap<String, String> mModifyTableStmts; private HashMap<String,
	 * String> mDropTableStmts;
	 */
	private SQLiteDatabase mDatabase = null;
	private int mStatus = 0; // 0: no error; <> 0: some error has occurred when
	private Context mContext;

	// calling some method

	public int getStatus() {
		return this.mStatus;
	}

	public SQLiteDatabase getDatabase() {
		return this.mDatabase;
	}

	/**
	 * Constructor: Create an TDSQLiteOpenHelper object to create, open, and/or
	 * manage a database. The database is not actually created or opened until
	 * one of {@link #getWritableDatabase} or {@link #getReadableDatabase} is
	 * called.
	 * 
	 * @param context
	 *            The context to use to open or create the database
	 * @param dbName
	 *            The name of the database file, or null for an in-memory
	 *            database
	 * @param dbVersion
	 *            the version number of the database (starting at 1); if the
	 *            database is older, {@link #onUpgrade} will be used to upgrade
	 *            the database; if the database is newer, {@link #onDowngrade}
	 *            will be used to downgrade the database
	 * @param createTableStmts
	 *            list of pairs <tableName, tableStmt> for creating tableName
	 *            using tableStmt
	 * @param modifyTableStmts
	 *            list of pairs <tableName, tableStmt> for modifying tableName
	 *            using tableStmt
	 * @param dropTableStmts
	 *            list of pairs <tableName, tableStmt> for dropping tableName
	 *            using tableStmt
	 */
	public LibSQLiteDBControl(Context context, String dbName, int dbVersion,
			HashMap<String, String> createTableStmts,
			HashMap<String, String> modifyTableStmts,
			HashMap<String, String> dropTableStmts) {
		this.mStatus = -1;
		this.mContext = context;
		mTDSQLiteOpenHelper = new LibSQLiteOpenHelper(context, dbName,
				dbVersion);
		this.checkAndCopyDatabase();
		mTDSQLiteOpenHelper = new LibSQLiteOpenHelper(context, dbName,
				dbVersion, createTableStmts, modifyTableStmts, dropTableStmts);
		if (mTDSQLiteOpenHelper != null) {
			/*
			 * this.mContext = context; this.mDbName = dbName; this.mDbVersion =
			 * dbVersion; this.mCreateTableStmts = createTableStmts;
			 * this.mModifyTableStmts = modifyTableStmts; this.mDropTableStmts =
			 * dropTableStmts;
			 */
			mStatus = 0; // Good shape, ready to call this.open() method to use
			// the database
		} else {
			this.mStatus = -1; // Some error when creating an instance of
			// TDSQLiteOpenHelper
		}
		// appDBControl = this;
	}

	/*
	 * Create and/or open a database that will be used for reading and writing.
	 * The first time this is called, the database will be opened and {@link
	 * #onCreate}, {@link #onUpgrade} and/or {@link #onOpen} will be called.
	 * 
	 * <p>Once opened successfully, the database is cached, so you can call this
	 * method every time you need to write to the database. (Make sure to call
	 * {@link #close} when you no longer need the database.) Errors such as bad
	 * permissions or a full disk may cause this method to fail, but future
	 * attempts may succeed if the problem is fixed.</p>
	 * 
	 * <p class="caution">Database upgrade may take a long time, you should not
	 * call this method from the application main thread, including from {@link
	 * android.content.ContentProvider#onCreate ContentProvider.onCreate()}.
	 * 
	 * @throws SQLiteException if the database cannot be opened for writing
	 * 
	 * @return true if a read/write database object is valid until {@link
	 * #close} is called; false otherwise
	 */
	public boolean open() {
		try {
			this.mDatabase = this.mTDSQLiteOpenHelper.getWritableDatabase();
			if (this.mDatabase != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			return false;
		}
	}

	// close the database
	public void close() {
		try {
			if (this.mDatabase != null && this.mDatabase.isOpen()) {
				this.mDatabase.close();
				this.mDatabase = null;
			}
		} catch (Exception ex) {
			Log.d("CloseDB", ex.getMessage());
		}
	}

	/**
	 * Create a new table in the database created by this control
	 * 
	 * @param createTableStmt
	 *            The SQL statement for creating a table
	 * @return True if a table is created successfully; false otherwise
	 */
	public boolean createTable(String createTableStmt) {
		boolean result = false;
		if ((this.mDatabase == null) || (createTableStmt == null)
				|| (createTableStmt.length() == 0)) {
			return result;
		}
		synchronized (mDatabase) {
			try {
				mDatabase.beginTransaction();
				this.mDatabase.execSQL(createTableStmt);
				result = true;
				mDatabase.setTransactionSuccessful();
			} catch (Exception ex) {
				result = false;
			} finally {
				mDatabase.endTransaction();
			}
		}
		return result;
	}

	/**
	 * Drop a table in the database stored by this control
	 * 
	 * @param dropTableStmt
	 *            The SQL statement for dropping a table
	 * @return True if the table is dropped successfully; false otherwise
	 */
	public boolean dropTable(String dropTableStmt) {
		boolean result = false;
		if ((this.mDatabase == null) || (dropTableStmt == null)
				|| (dropTableStmt.length() == 0)) {
			return result;
		}
		synchronized (mDatabase) {
			try {
				mDatabase.beginTransaction();
				this.mDatabase.execSQL(dropTableStmt);
				result = true;
				mDatabase.setTransactionSuccessful();
			} catch (Exception ex) {
				result = false;
			} finally {
				mDatabase.endTransaction();
			}
		}
		return result;
	}

	/**
	 * Search for the existence of some record in a table by some
	 * where-condition in the database stored in this control
	 * 
	 * @param tableName
	 *            The table to search for some record
	 * @param whereClause
	 *            A filter declaring which records to return, formatted as an
	 *            SQL WHERE clause (excluding the WHERE itself). Passing null
	 *            will return all records for the given table.
	 * @param whereArgs
	 *            To avoid SQL Injection attack, you must include ?s in
	 *            whereClause, which will be replaced by the values from
	 *            whereArgs, in order that they appear in the selection. The
	 *            values will be bound as Strings.
	 * @return True if some record exists in the table by some where-condition;
	 *         false otherwise
	 */
	public boolean doesExist(String tableName, String whereClause,
			String[] whereArgs) {
		boolean result = false;
		if ((this.mDatabase == null) || (tableName == null)
				|| (tableName.length() == 0)) {
			return result;
		}

		// create a cursor to contain the result returned
		Cursor cursor = null;

		// Construct the SQL statement
		String sql = "SELECT count(*) FROM " + tableName;
		if ((whereClause != null) && (whereClause.length() > 0)) {
			sql += " WHERE " + whereClause;
		}

		// Execute the query
		synchronized (mDatabase) {
			try {
				this.mDatabase.beginTransaction();
				cursor = this.mDatabase.rawQuery(sql, whereArgs);
				if ((cursor != null) && cursor.moveToFirst()) {
					if (cursor.getInt(0) > 0) {
						result = true;
					}
				}
				this.mDatabase.setTransactionSuccessful();
			} catch (Exception ex) {
				result = false;
			} finally {
				if (cursor != null)
					cursor.close(); // close the cursor
				this.mDatabase.endTransaction();
			}
		}
		return result;
	}

	/**
	 * Convenience method for inserting a record into the database (no checking
	 * on the duplication).
	 * 
	 * @param tableName
	 *            the table to insert the record into
	 * @param values
	 *            this map contains the initial column values for the record.
	 *            The keys should be the column names and the values the column
	 *            values
	 * @return 1 if a new record is successfully inserted, or 0 if an error
	 *         occurred
	 */
	public int insert(String tableName, ContentValues values) {
		int result = 0;
		if ((this.mDatabase == null) || (tableName == null)
				|| (tableName.length() == 0) || (values == null)
				|| (values.size() == 0)) {
			return result;
		}
		synchronized (mDatabase) {
			try {
				this.mDatabase.beginTransaction();

				if (this.mDatabase.insert(tableName, null, values) != -1) {
					result = 1;
				} else {
					result = 0;
				}
				this.mDatabase.setTransactionSuccessful();
			} catch (Exception ex) {
				Log.d("Database", "Cannot insert into the table " + tableName);
				result = 0;
			} finally { // close the connection from database
				this.mDatabase.endTransaction();
			}
		}
		return result;
	}

	/**
	 * Convenience method for inserting list of records into the database (no
	 * checking on the duplication).
	 * 
	 * @param tableName
	 *            the table to insert the record into
	 * @param listValues
	 *            each item of list is a map that contains the initial column
	 *            values for records. The keys should be the column names and
	 *            the values the column values
	 * @return number records that have been inserted.
	 */
	public int insert(String tableName, ArrayList<ContentValues> listValues) {
		if ((this.mDatabase == null) || TextUtils.isEmpty(tableName)
				|| (listValues == null) || (listValues.size() == 0)) {
			return 0;
		}
		int numberRecords = 0;
		synchronized (mDatabase) {
			try {
				mDatabase.beginTransaction();
				for (int i = 0; i < listValues.size(); i++) {
					if (mDatabase.insert(tableName, null, listValues.get(i)) != -1) {
						numberRecords++;
					}
				}
				mDatabase.setTransactionSuccessful();
			} catch (Exception ex) {
				Log.d("Database", "Cannot insert into the table " + tableName);
			} finally { // close the connection from database
				this.mDatabase.endTransaction();
			}
		}
		return numberRecords;
	}

	/**
	 * Convenience method for inserting a record into the database without
	 * duplication.
	 * 
	 * @param tableName
	 *            the table to insert the record into
	 * @param values
	 *            this map contains the initial column values for the record.
	 *            The keys should be the column names and the values the column
	 *            values
	 * @param whereClause
	 *            A filter declaring the duplication condition (need to avoid
	 *            the insertion), formatted as an SQL WHERE clause (excluding
	 *            the WHERE itself). Passing null will result no insertion since
	 *            it needs some condition to avoid duplication.
	 * @param whereArgs
	 *            To avoid SQL Injection attack, you must include ?s in
	 *            whereClause, which will be replaced by the values from
	 *            whereArgs, in order that they appear in the selection. The
	 *            values will be bound as Strings.
	 * @return 1 if a new record is successfully inserted, or 0 if an error
	 *         occurred
	 */
	public int insertNotDuplicate(String tableName, ContentValues values,
			String whereClause, String[] whereArgs) {
		int result = 0;
		if ((this.mDatabase == null) || (tableName == null)
				|| (tableName.length() == 0) || (values == null)
				|| (values.size() == 0)) {
			return result;
		}

		// create a cursor to contain the result returned
		Cursor cursor = null;

		// Construct the SQL statement
		String sql = "SELECT count(*) FROM " + tableName;
		if ((whereClause != null) && (whereClause.length() > 0)) {
			sql += " WHERE " + whereClause;
		}

		// Execute the query
		synchronized (mDatabase) {
			try {
				this.mDatabase.beginTransaction();
				cursor = this.mDatabase.rawQuery(sql, whereArgs);
				if ((cursor != null) && cursor.moveToFirst()) {
					if (cursor.getInt(0) > 0) { // Cannot insert the duplicated
						// record
						result = 0;
					} else {
						this.mDatabase.insert(tableName, null, values);
						result = 1;
					}
				}
				this.mDatabase.setTransactionSuccessful();
			} catch (Exception ex) {
				result = 0;
			} finally { // close the connection from database
				if (cursor != null)
					cursor.close(); // close the cursor
				this.mDatabase.endTransaction();
			}
		}
		return result;
	}

	/**
	 * Convenience method for deleting records in the database.
	 * 
	 * @param tableName
	 *            the table to delete from
	 * @param whereClause
	 *            the optional WHERE clause to apply when deleting. Passing null
	 *            will delete all records.
	 * @param whereArgs
	 *            To avoid SQL Injection attack, you must include ?s in
	 *            whereClause, which will be replaced by the values from
	 *            whereArgs, in order that they appear in the selection. The
	 *            values will be bound as Strings.
	 * @return the number of records affected if a whereClause is passed in, 0
	 *         otherwise. To remove all records and get a count pass "1" as the
	 *         whereClause.
	 */
	public int delete(String tableName, String whereClause, String[] whereArgs) {
		int result = 0;
		if ((this.mDatabase == null) || (tableName == null)
				|| (tableName.length() == 0)) {
			return result;
		}

		synchronized (mDatabase) {
			try {
				this.mDatabase.beginTransaction();
				result = this.mDatabase.delete(tableName, whereClause,
						whereArgs);
				this.mDatabase.setTransactionSuccessful();
			} catch (Exception ex) {
				Log.d("Database", "Cannot delete record(s) in the table "
						+ tableName);
				result = 0;
			} finally { // close the connection from database
				this.mDatabase.endTransaction();
			}
		}
		return result;
	}

	/**
	 * Convenience method for updating records in the database.
	 * 
	 * @param tableName
	 *            the table to update in
	 * @param values
	 *            a map from column names to new column values. null is a valid
	 *            value that will be translated to NULL.
	 * @param whereClause
	 *            the optional WHERE clause to apply when updating. Passing null
	 *            will update all records.
	 * @param whereArgs
	 *            To avoid SQL Injection attack, you must include ?s in
	 *            whereClause, which will be replaced by the values from
	 *            whereArgs, in order that they appear in the selection. The
	 *            values will be bound as Strings.
	 * @return the number of records affected
	 */
	public int update(String tableName, ContentValues values,
			String whereClause, String[] whereArgs) {
		int result = 0;
		if ((this.mDatabase == null) || (tableName == null)
				|| (tableName.length() == 0) || (values == null)
				|| (values.size() == 0)) {
			return result;
		}

		synchronized (mDatabase) {
			try {
				this.mDatabase.beginTransaction();
				result = this.mDatabase.update(tableName, values, whereClause,
						whereArgs);
				this.mDatabase.setTransactionSuccessful();
			} catch (Exception ex) {
				Log.d("Database", "Cannot update the table " + tableName);
				result = 0;
			} finally { // close the connection from database
				this.mDatabase.endTransaction();
			}
		}
		return result;
	}

	/**
	 * Convenience method for insert a record (if it does not exist) or updating
	 * corresponding record(s) matched the whereClause in the database.
	 * 
	 * @param tableName
	 *            the table to update in
	 * @param values
	 *            a map from column names to new column values. null is a valid
	 *            value that will be translated to NULL.
	 * @param whereClause
	 *            the optional WHERE clause to apply when updating. Passing null
	 *            will update all records.
	 * @param whereArgs
	 *            To avoid SQL Injection attack, you must include ?s in
	 *            whereClause, which will be replaced by the values from
	 *            whereArgs, in order that they appear in the selection. The
	 *            values will be bound as Strings.
	 * @return the number of records affected
	 */
	public int insertOrUpdate(String tableName, ContentValues values,
			String whereClause, String[] whereArgs) {
		int result = 0;
		if ((this.mDatabase == null) || (tableName == null)
				|| (tableName.length() == 0) || (values == null)
				|| (values.size() == 0)) {
			return result;
		}
		// create a cursor to contain the result returned
		Cursor cursor = null;

		// Construct the SQL statement
		String sql = "SELECT count(*) FROM " + tableName;
		if ((whereClause != null) && (whereClause.length() > 0)) {
			sql += " WHERE " + whereClause;
		}

		// Execute the query
		synchronized (mDatabase) {
			try {
				this.mDatabase.beginTransaction();
				cursor = this.mDatabase.rawQuery(sql, whereArgs);
				if ((cursor != null) && cursor.moveToFirst()) {
					if (cursor.getInt(0) > 0) { // some record exists
						// update the existing record
						result = this.mDatabase.update(tableName, values,
								whereClause, whereArgs);
					} else {
						this.mDatabase.insert(tableName, null, values);
						result = 1; // one record is inserted
					}
				}
				this.mDatabase.setTransactionSuccessful();
			} catch (Exception ex) {
				result = 0;
			} finally { // close the connection from database
				if (cursor != null)
					cursor.close(); // close the cursor
				this.mDatabase.endTransaction();
			}
		}
		return result;
	}

	/**
	 * Runs the provided SQL and returns a {@link Cursor} over the result set.
	 * 
	 * @param sql
	 *            the SQL query. The SQL string must not be ; terminated
	 * @param selectionArgs
	 *            To avoid SQL Injection attack, you must include ?s in where
	 *            clause in the query, which will be replaced by the values from
	 *            selectionArgs. The values will be bound as Strings.
	 * @return A {@link Cursor} object, which is positioned before the first
	 *         entry. Note that {@link Cursor}s are not synchronized, see the
	 *         documentation for more details.
	 */
	public Cursor rawQuery(String sql, String[] selectionArgs) {
		Cursor result = null;
		if ((this.mDatabase == null) || (sql == null) || (sql.length() == 0)) {
			return result;
		}

		synchronized (mDatabase) {
			try {
				this.mDatabase.beginTransaction();
				result = this.mDatabase.rawQuery(sql, selectionArgs);
				this.mDatabase.setTransactionSuccessful();
			} catch (Exception ex) {
				result = null;
			} finally { // close the connection from database
				this.mDatabase.endTransaction();
			}
		}
		return result;
	}

	/**
	 * Query some records in a table by some where-condition in the database
	 * stored in this control
	 * 
	 * @param tableName
	 *            The table to search for some record
	 * @param whereClause
	 *            A filter declaring which records to return, formatted as an
	 *            SQL WHERE clause (excluding the WHERE itself). Passing null
	 *            will return all records for the given table.
	 * @param whereArgs
	 *            To avoid SQL Injection attack, you must include ?s in
	 *            whereClause, which will be replaced by the values from
	 *            whereArgs, in order that they appear in the selection. The
	 *            values will be bound as Strings.
	 * @return True if some record exists in the table by some where-condition;
	 *         false otherwise
	 */
	public Cursor queryWithCondition(String tableName, String whereClause,
			String[] whereArgs) {
		Cursor result = null;
		if ((this.mDatabase == null) || (tableName == null)
				|| (tableName.length() == 0)) {
			return result;
		}
		// Construct the SQL statement
		String sql = "SELECT * FROM " + tableName;
		if ((whereClause != null) && (whereClause.length() > 0)) {
			sql += " WHERE " + whereClause;
		}
		// Execute the query
		synchronized (mDatabase) {
			try {
				this.mDatabase.beginTransaction();
				result = this.mDatabase.rawQuery(sql, whereArgs);
				this.mDatabase.setTransactionSuccessful();
			} catch (Exception ex) {
				result = null;
			} finally { // close the connection from database
				this.mDatabase.endTransaction();
			}
		}
		return result;
	}

	/**
	 * Execute a single SQL statement that is NOT a SELECT or any other SQL
	 * statement that returns data.
	 * 
	 * @param sql
	 *            the SQL query. The SQL string must not be ; terminated
	 * @throws Exception
	 */
	public void execSQL(String sql) throws Exception {
		if ((this.mDatabase == null) || (sql == null) || (sql.length() == 0)) {
			return;
		}

		synchronized (mDatabase) {
			try {
				this.mDatabase.beginTransaction();
				this.mDatabase.execSQL(sql);
				this.mDatabase.setTransactionSuccessful();
			} catch (Exception ex) {
				throw new Exception(ex.getMessage());
			} finally { // close the connection from database
				this.mDatabase.endTransaction();
			}
		}
	}

	/**
	 * Add by Charles
	 */
	/**
	 * Check database if not exist then copy into application
	 */
	public void checkAndCopyDatabase() {
		try {

			boolean dbExist = checkDataBase();
			if (dbExist) {
				Log.d("Database", DatabaseTable.DATABASE_NAME
						+ " already exist!");
			} else {
				try {
					mTDSQLiteOpenHelper.getReadableDatabase();
					copyDataBase();
				} catch (IOException e) {
					Log.d("Database", "Error copying database");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check database
	 * 
	 * @return true if database exist false if database not exist
	 */
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DatabaseTable.DATABASE_PATH
					+ DatabaseTable.DATABASE_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		if (checkDB != null)
			checkDB.close();
		return checkDB != null ? true : false;
	}

	/**
	 * Copy database into application
	 * 
	 * @throws IOException
	 */
	private void copyDataBase() throws IOException {
		InputStream myInput = this.mContext.getAssets().open(
				DatabaseTable.DATABASE_NAME);
		String outFileName = DatabaseTable.DATABASE_PATH
				+ DatabaseTable.DATABASE_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}
}
