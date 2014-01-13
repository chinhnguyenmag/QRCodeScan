package com.magrabbit.lib;

/**
 * This helper class extends SQLiteOpenHelper with customized onCreate, onUpgrade methods on SQLite databases.
 *
 */
import java.util.HashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LibSQLiteOpenHelper extends SQLiteOpenHelper {
	private HashMap<String, String> mCreateTableStmts;
	private HashMap<String, String> mModifyTableStmts;
	private HashMap<String, String> mDropTableStmts;

	/**
	 * Create a helper object to create, open, and/or manage a database. The
	 * database is not actually created or opened until one of
	 * {@link #getWritableDatabase} or {@link #getReadableDatabase} is called.
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

	public LibSQLiteOpenHelper(Context context, String dbName, int dbVersion,
			HashMap<String, String> createTableStmts,
			HashMap<String, String> modifyTableStmts,
			HashMap<String, String> dropTableStmts) {
		super(context, dbName, null, dbVersion);
		this.mCreateTableStmts = createTableStmts;
		this.mModifyTableStmts = modifyTableStmts;
		this.mDropTableStmts = dropTableStmts;
	}

	public LibSQLiteOpenHelper(Context context, String dbName, int dbVersion) {
		super(context, dbName, null, dbVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String tableName = "";
		String tableStmt = "";
		try {
			// Create tables from the list mCreateTableStmts of <tableName,
			// tableStmt>
			for (HashMap.Entry<String, String> entry : this.mCreateTableStmts
					.entrySet()) {
				tableName = entry.getKey();
				tableStmt = entry.getValue();
				db.execSQL(tableStmt);
			}
		} catch (Exception ex) {
			Log.d("Database", "Cannot create the table " + tableName);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String tableName = "";
		String tableStmt = "";
		String tableOperation = "";
		try {
			// Create tables from the list mCreateTableStmts of <tableName,
			// tableStmt>
			tableOperation = "create";
			for (HashMap.Entry<String, String> entry : this.mCreateTableStmts
					.entrySet()) {
				tableName = entry.getKey();
				tableStmt = entry.getValue();
				db.execSQL(tableStmt);
			}

			// Modify tables from the list mModifyTableStmts of <tableName,
			// tableStmt>
			tableOperation = "modify";
			for (HashMap.Entry<String, String> entry : this.mModifyTableStmts
					.entrySet()) {
				tableName = entry.getKey();
				tableStmt = entry.getValue();
				db.execSQL(tableStmt);
			}

			// Drop tables from the list mDropTableStmts of <tableName,
			// tableStmt>
			tableOperation = "drop";
			for (HashMap.Entry<String, String> entry : this.mDropTableStmts
					.entrySet()) {
				tableName = entry.getKey();
				tableStmt = entry.getValue();
				db.execSQL(tableStmt);
			}
		} catch (Exception ex) {
			Log.d("Database", "Cannot " + tableOperation + " the table "
					+ tableName);
		}

	}
}
