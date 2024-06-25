/**
 * Student ID: 2940038
 */

package com.example.diaryapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * The database adapter helps in the diary entry functions
 * like adding, deleting, updating and getting the entries
 */
class DatabaseAdapter(context: Context) {
    private val dbHelper: DatabaseHelper = DatabaseHelper(context)
    private var db: SQLiteDatabase

    companion object {
        // sets the information including the columns and table name
        const val KEY_ID = "id" // the id of the note is the primary key
        const val KEY_DATE = "date" // here date also acts like a primary key since only one note is allowed for a given date
        const val KEY_NOTE = "note"
        const val DATABASE_NAME = "DiaryApp"
        const val DATABASE_TABLE = "diary_entries"
        const val DATABASE_VERSION = 1
    }

    init {
        db = dbHelper.writableDatabase
    }

    // opens the database connection
    fun open(): DatabaseAdapter {
        db = dbHelper.writableDatabase
        return this
    }

    // closes the database connection
    fun close() {
        dbHelper.close()
    }

    /**
     * Adds a note in the database by entering the date and note
     * @param date, the date of diary entry
     * @param note, the content for the entry
     * @return returns a unique row id after entering the diary entry in the database
     */
    fun addNote(date: String, note: String): Long {
        val contentValues = ContentValues()
        contentValues.put(KEY_DATE, date)
        contentValues.put(KEY_NOTE, note)
        return db.insert(DATABASE_TABLE, null, contentValues)
    }

    /**
     * Gets the note in ascending order of date
     * @return cursor object that holds all the notes, with its id, date and note itself
     */
    fun getAllNotes(): Cursor {
        val columns = arrayOf("$KEY_ID as _id", KEY_DATE, KEY_NOTE)
        val orderBy = "$KEY_DATE ASC" // Gets the notes in ascending order of date

        return db.query(DATABASE_TABLE, columns, null, null, null, null, orderBy)
    }

    /**
     * Deletes all the notes from the database
     * @return a boolean on successful deletion of the entries from the database
     */
    fun deleteAllNotes(): Boolean {
        return db.delete(DATABASE_TABLE, null, null) > 0
    }

    /**
     * Deletes a note in the database by a specific date
     * In this case the date acts as a primary key since only one entry exists for a given date
     * @param selectedDate, the date of the note that needs to be deleted
     * @return a boolean to check successful deletion of note
     */
    fun deleteNoteByDate(selectedDate: String): Boolean {
        val whereClause = "$KEY_DATE = ?"
        val whereArgs = arrayOf(selectedDate)

        return db.delete(DATABASE_TABLE, whereClause, whereArgs) > 0
    }

    /**
     * Update a note in the database by date
     * @param date, finds the note by date
     * @param newNote, a new text for the note to be updated
     * @return a boolean to check successful updating of note
     */
    fun updateNoteByDate(date: String, newNote: String): Boolean {
        val values = ContentValues()
        values.put(KEY_NOTE, newNote)

        val whereClause = "$KEY_DATE = ?"
        val whereArgs = arrayOf(date)

        return db.update(DATABASE_TABLE, values, whereClause, whereArgs) > 0
    }

    /**
     * Check if a note with the given date already exists
     * @param date, finds the note by the given date
     * @return a boolean, true if the note exists and false if the note for the date doesn't exist
     */
    fun doesNoteExist(date: String): Boolean {
        val columns = arrayOf(KEY_ID)
        val selection = "$KEY_DATE = ?"
        val selectionArgs = arrayOf(date)

        val cursor = db.query(DATABASE_TABLE, columns, selection, selectionArgs, null, null, null)

        // checks if the cursor holds the row/entry with the specific date fetched using the sql query
        val exists = cursor.count > 0

        // close the cursor
        cursor.close()

        return exists
    }

    /**
     * Gets the note by specific date
     * @param selectedDate, finds the note by the given date
     * @return the note is returned
     */
    fun getNoteByDate(selectedDate: String): String {
        val columns = arrayOf(KEY_NOTE)
        val selection = "$KEY_DATE = ?"
        val selectionArgs = arrayOf(selectedDate)

        val cursor = db.query(DATABASE_TABLE, columns, selection, selectionArgs, null, null, null)

        // checks if the cursor holds any note or there are no notes for the selected date
        if (cursor.count > 0) {
            // moves to the first row
            cursor.moveToFirst()

            // gets the index of the note
            val noteIndex = cursor.getColumnIndex(KEY_NOTE)

            // retrieves the note
            val noteData = cursor.getString(noteIndex)

            // close the cursor
            cursor.close()

            // returns the note
            return noteData
        } else {
            // close the cursor
            cursor.close()
            return ""
        }
    }

    /**
     * Retrieves the note by date filter
     * @param selectedDate, finds the note by the given date
     * @return the cursor containing the result of the query
     */
    fun filterNotesByDate(selectedDate: String): Cursor {
        val columns = arrayOf("$KEY_ID as _id", KEY_DATE, KEY_NOTE)
        val selection = "$KEY_DATE = ?"
        val selectionArgs = arrayOf(selectedDate)
        return db.query(DATABASE_TABLE, columns, selection, selectionArgs, null, null, null)
    }

    /**
     * This method is responsible for creating and deleting the database
     */
    private inner class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            // the primary key if is set on auto increment
            db.execSQL("CREATE TABLE $DATABASE_TABLE ($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, $KEY_DATE TEXT, $KEY_NOTE TEXT);")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $DATABASE_TABLE")
            onCreate(db)
        }
    }
}
