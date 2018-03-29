/*
 * Copyright (C) 2016 Lusfold
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.soar.soarlibrary.db.core

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.soar.soarlibrary.db.exception.KVStoreKeyNullException
import com.soar.soarlibrary.db.exception.KVStoreValueNullException
import com.soar.soarlibrary.db.utils.CursorUtils
import com.soar.soarlibrary.db.utils.DataBaseStringUtils
import java.util.*

class KVManagerImpl(
        /**
         * Get database.
         *
         * @return database.
         */
        override val database: SQLiteDatabase) : KVManger {

    /**
     * @return if DataManager table exists return true else return false
     */
    private val isTableExist: Boolean
        get() {
            val cursor = execQuery(SQL_QUERY_TABLE, arrayOf(TABLE_NAME))
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    val count = cursor.getInt(0)
                    if (count > 0) {
                        return true
                    }
                }
            }
            return false
        }

    init {
        if (!isTableExist) {
            createKVTable()
        }
    }

    /**
     * create DataManager table if not exists
     */
    private fun createKVTable() {
        execNonQuery(SQL_CREATE_TABLE, arrayOf(TABLE_NAME, COLUMN_KEY, COLUMN_VALUE))
    }

    /**
     * delete DataManager table
     */
    private fun deleteKVTable() {
        execNonQuery(SQL_DELETE_TABLE, arrayOf(TABLE_NAME))
    }

    /**
     * Execute query sql with selectionArgs,will replace ?s with selectionArgs.
     *
     * @param sql
     * @param selectionArgs
     * @return The result.
     */
    override fun execQuery(sql: String, selectionArgs: Array<String>): Cursor {
        var sql = sql
        var i = 0
        val count = selectionArgs.size
        while (i < count) {
            sql = sql.replaceFirst("[?]".toRegex(), selectionArgs[i])
            i++
        }
        //        LogUtil.d(getClass().getSimpleName() , sql);
        return database.rawQuery(sql, null)
    }

    /**
     * Execute non query sql with selectionArgs,will replace ?s with selectionArgs.
     *
     * @param sql
     * @param selectionArgs
     */
    override fun execNonQuery(sql: String, selectionArgs: Array<String>) {
        var sql = sql
        var i = 0
        val count = selectionArgs.size
        while (i < count) {
            sql = sql.replaceFirst("[?]".toRegex(), selectionArgs[i])
            i++
        }
        //        LogUtil.d(getClass().getSimpleName() , sql);
        database.execSQL(sql)
    }

    /**
     * Check whether the key exists.
     *
     * @param key
     * @return If the key exists return true,else return false.
     */
    override fun keyExists(key: String): Boolean {
        if (DataBaseStringUtils.isNull(key))
            throw KVStoreKeyNullException()
        val cursor = execQuery(SQL_QUERY_DATA, arrayOf(TABLE_NAME, COLUMN_KEY, key))
        if (cursor != null) {
            try {
                if (cursor.moveToNext())
                    return true
            } finally {
                CursorUtils.closeCursorQuietly(cursor)
            }
        }
        return false
    }

    private fun checkKey(key: String) {
        if (DataBaseStringUtils.isNull(key))
            throw KVStoreKeyNullException()
    }

    private fun checkValue(value: String) {
        if (DataBaseStringUtils.isNull(value))
            throw KVStoreValueNullException()
    }

    private fun doInsert(key: String, value: String): Long {
        val contentValues = ContentValues()
        contentValues.put(COLUMN_KEY, key)
        contentValues.put(COLUMN_VALUE, value)
        return database.insert(TABLE_NAME, null, contentValues)
    }

    private fun doUpdate(key: String, value: String): Int {
        val contentValues = ContentValues()
        contentValues.put(COLUMN_VALUE, value)
        return database.update(TABLE_NAME, contentValues, COLUMN_KEY + " = ?", arrayOf(key))
    }


    /**
     * Insert data to DataManager table.
     *
     * @param key
     * @param value
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
    override fun insert(key: String, value: String): Long {
        checkKey(key)
        checkValue(value)
        return if (keyExists(key)) -1 else doInsert(key, value)
    }

    /**
     * Insert data to DataManager table,if the key exists will update the key value.
     *
     * @param key
     * @param value
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
    override fun insertOrUpdate(key: String, value: String): Long {
        checkKey(key)
        checkValue(value)
        return if (keyExists(key)) doUpdate(key, value).toLong() else doInsert(key, value)
    }

    /**
     * Update data.
     *
     * @param key
     * @param value
     * @return the number of rows affected.
     */
    override fun update(key: String, value: String): Int {
        checkKey(key)
        checkValue(value)
        return if (!keyExists(key)) -1 else doUpdate(key, value)
    }

    /**
     * Delete data.
     *
     * @param key
     * @return the number of rows affected.
     */
    override fun delete(key: String): Int {
        checkKey(key)
        return database.delete(TABLE_NAME, COLUMN_KEY + " = ?", arrayOf(key))
    }


    /**
     * Query value according the key.
     *
     * @param key
     * @return result.
     */
    override fun get(key: String): String {
        checkKey(key)
        val cursor = execQuery(SQL_QUERY_DATA, arrayOf(TABLE_NAME, COLUMN_KEY, key))
        var result: String? = null
        if (cursor != null) {
            if (cursor.moveToNext()) {
                result = cursor.getString(1)
            }
            CursorUtils.closeCursorQuietly(cursor)
        }
        return result!!
    }

    /**
     * Query values according the key prefix string.
     *
     * @param keyPrefix
     * @return result.
     */
    override fun getByPrefix(keyPrefix: String): Map<String, String> {
        checkKey(keyPrefix)
        val cursor = execQuery(SQL_QUERY_DATA_PREFIX, arrayOf(TABLE_NAME, COLUMN_KEY, keyPrefix))
        var result: MutableMap<String, String>? = null
        if (cursor != null) {
            result = HashMap()
            while (cursor.moveToNext()) {
                result.put(cursor.getString(0), cursor.getString(1))
            }
            CursorUtils.closeCursorQuietly(cursor)
        }
        return result!!
    }

    /**
     * Query values according the key contains string.
     *
     * @param keyContains
     * @return result.
     */
    override fun getByContains(keyContains: String): Map<String, String> {
        checkKey(keyContains)
        val cursor = execQuery(SQL_QUERY_DATA_CONTAINS, arrayOf(TABLE_NAME, COLUMN_KEY, keyContains))
        var result: MutableMap<String, String>? = null
        if (cursor != null) {
            result = HashMap()
            while (cursor.moveToNext()) {
                result.put(cursor.getString(0), cursor.getString(1))
            }
            CursorUtils.closeCursorQuietly(cursor)
        }
        return result!!
    }


    /**
     * Clear the DataManager table.
     */
    override fun clearTable() {
        deleteKVTable()
        createKVTable()
    }

    companion object {
        private val TAG = "KVManagerImpl"
        //key column name
        val COLUMN_KEY = "Key"
        //value column name
        val COLUMN_VALUE = "Value"
        //table name
        val TABLE_NAME = "DataManager"
        //table sql
        private val SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ? (? TEXT PRIMARY KEY NOT NULL,? TEXT NOT NULL)"
        private val SQL_QUERY_TABLE = "SELECT COUNT(*) AS c FROM sqlite_master WHERE type='table' AND name= '?'"
        private val SQL_DELETE_TABLE = "DELETE FROM ?"
        //data sql
        private val SQL_QUERY_DATA = "SELECT * FROM ? WHERE ? = '?'"
        private val SQL_QUERY_DATA_PREFIX = "SELECT * FROM ? WHERE ? LIKE '?%'"
        private val SQL_QUERY_DATA_CONTAINS = "SELECT * FROM ? WHERE ? LIKE '%?%'"
    }

}
