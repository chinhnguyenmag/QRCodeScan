package com.magrabbit.lib;

/**
 * This class contains standardized methods for basic CRUD operations on SQLite databases.
 *
 * @Changes: Add functions to insert list of items.
 */
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * This class provide methods to query data, insert, update from a table. All
 * table will be inherited from this class.
 * 
 * @param <T>
 *            Data type of an item in table.
 */
public abstract class LibTableQuery<T> {

	/**
	 * convert an item of type T to pairs of <fieldName, fieldValue> of the
	 * corresponding table for T.
	 * 
	 * @param item
	 *            an instance of type T.
	 * @return pairs of <fieldName, fieldValue> of the corresponding table for
	 *         T.
	 */
	protected abstract ContentValues convertToContentValues(T item);

	/**
	 * convert data from database to list of objects.
	 * 
	 * @param cursor
	 * @return list of data.
	 */
	protected abstract ArrayList<T> convertToItems(Cursor cursor);

	/**
	 * Search for the existence of some record in a table by some
	 * where-condition in the database stored in this control
	 * 
	 * @param dbControl
	 *            the control of the opening SQLite database
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
	public boolean doesExist(LibSQLiteDBControl dbControl, String tableName,
			String whereClause, String[] whereArgs) {
		return dbControl.doesExist(tableName, whereClause, whereArgs);
	}

	/**
	 * Convenience method for inserting a record into the database (no checking
	 * on the duplication).
	 * 
	 * @param item
	 *            an instance of type T with the mapped values to insert
	 * @param dbControl
	 *            the control of the opening SQLite database
	 * @param tableName
	 *            the table to insert the record into
	 * @return 1 if a new record is successfully inserted, or 0 if an error
	 *         occurred
	 */
	public int insert(T item, LibSQLiteDBControl dbControl, String tableName) {
		ContentValues values = convertToContentValues(item);
		return dbControl.insert(tableName, values);
	}

	/**
	 * Convenience method for inserting a record into the database (no checking
	 * on the duplication).
	 * 
	 * @param items
	 *            list instances of type T with the mapped values to insert
	 * @param dbControl
	 *            the control of the opening SQLite database
	 * @param tableName
	 *            the table to insert the record into
	 * @return number records that have been inserted.
	 */
	public int insert(ArrayList<T> items, LibSQLiteDBControl dbControl,
			String tableName) {
		if (items == null || items.size() == 0) {
			return 0;
		}
		// convert to list of ContentValues.
		ArrayList<ContentValues> listValues = new ArrayList<ContentValues>();
		for (int i = 0; i < items.size(); i++) {
			ContentValues values = convertToContentValues(items.get(i));
			listValues.add(values);
		}
		// insert to database.
		return dbControl.insert(tableName, listValues);
	}

	/**
	 * Convenience method for inserting a record into the database without
	 * duplication.
	 * 
	 * @param item
	 *            an instance of type T with the mapped values to insert
	 * @param dbControl
	 *            the control of the opening SQLite database
	 * @param tableName
	 *            the table to insert the record into
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
	public int insertNotDuplicate(T item, LibSQLiteDBControl dbControl,
			String tableName, String whereClause, String[] whereArgs) {
		ContentValues values = convertToContentValues(item);
		return dbControl.insertNotDuplicate(tableName, values, whereClause,
				whereArgs);
	}

	/**
	 * Convenience method for deleting records in the database.
	 * 
	 * @param dbControl
	 *            the control of the opening SQLite database
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
	public int delete(LibSQLiteDBControl dbControl, String tableName,
			String whereClause, String[] whereArgs) {
		return dbControl.delete(tableName, whereClause, whereArgs);
	}

	/**
	 * Convenience method for updating records in the database.
	 * 
	 * @param item
	 *            an instance of type T with the mapped values to insert
	 * @param dbControl
	 *            the control of the opening SQLite database
	 * @param tableName
	 *            the table to update in
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
	public int update(T item, LibSQLiteDBControl dbControl, String tableName,
			String whereClause, String[] whereArgs) {
		ContentValues values = convertToContentValues(item);
		return dbControl.update(tableName, values, whereClause, whereArgs);
	}

	/**
	 * Convenience method for insert a record (if it does not exist) or updating
	 * corresponding record(s) matched the whereClause in the database.
	 * 
	 * @param item
	 *            an instance of type T with the mapped values to insert or
	 *            update
	 * @param dbControl
	 *            the control of the opening SQLite database
	 * @param tableName
	 *            the table to update in
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
	public int insertOrUpdate(T item, LibSQLiteDBControl dbControl,
			String tableName, String whereClause, String[] whereArgs) {
		ContentValues values = convertToContentValues(item);
		return dbControl.insertOrUpdate(tableName, values, whereClause,
				whereArgs);
	}

	/**
	 * Runs the provided SQL and returns a {@link Cursor} over the result set.
	 * 
	 * @param dbControl
	 *            the control of the opening SQLite database
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
	public Cursor rawQuery(LibSQLiteDBControl dbControl, String sql,
			String[] selectionArgs) {
		return dbControl.rawQuery(sql, selectionArgs);
	}

	/**
	 * Query some records in a table by some where-condition in the database
	 * stored in this control
	 * 
	 * @param dbControl
	 *            the control of the opening SQLite database
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
	public Cursor queryWithCondition(LibSQLiteDBControl dbControl,
			String tableName, String whereClause, String[] whereArgs) {
		return dbControl.queryWithCondition(tableName, whereClause, whereArgs);
	}

	/**
	 * Retrieve some records in a table by some where-condition in the database
	 * stored in a control
	 * 
	 * @param dbControl
	 *            the control of the opening SQLite database
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
	 * @return list of items.
	 */
	public ArrayList<T> getRecords(LibSQLiteDBControl dbControl,
			String tableName, String whereClause, String[] whereArgs) {
		ArrayList<T> items = null;
		// create a cursor to contain the result returned
		Cursor cursor = dbControl.queryWithCondition(tableName, whereClause,
				whereArgs);
		if (cursor != null) {
			items = convertToItems(cursor);
			cursor.close();
			cursor = null;
		}
		return items;
	}

	/**
	 * Execute a single SQL statement that is NOT a SELECT or any other SQL
	 * statement that returns data.
	 * 
	 * @param sql
	 *            the SQL query. The SQL string must not be ; terminated
	 * @throws Exception
	 */
	public void execSQL(LibSQLiteDBControl dbControl, String sql)
			throws Exception {
		dbControl.execSQL(sql);
	}
}
