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

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

interface KVManger {

    /**
     * Get database.
     *
     * @return database.
     */
    val database: SQLiteDatabase

    /**
     * Execute query sql with selectionArgs,will replace ?s with selectionArgs.
     *
     * @param sql
     * @param selectionArgs
     * @return The result.
     */
    fun execQuery(sql: String, selectionArgs: Array<String>): Cursor

    /**
     * Execute non query sql with selectionArgs,will replace ?s with selectionArgs.
     *
     * @param sql
     * @param selectionArgs
     */
    fun execNonQuery(sql: String, selectionArgs: Array<String>)

    /**
     * Check whether the key exists.
     *
     * @param key
     * @return If the key exists return true,else return false.
     */
    fun keyExists(key: String): Boolean

    /**
     * Insert data to DataManager table.
     *
     * @param key
     * @param value
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
    fun insert(key: String, value: String): Long

    /**
     * Insert data to DataManager table,if the key exists will update the key value.
     *
     * @param key
     * @param value
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
    fun insertOrUpdate(key: String, value: String): Long


    /**
     * Update data.
     *
     * @param key
     * @param value
     * @return the number of rows affected.
     */
    fun update(key: String, value: String): Int

    /**
     * Delete data.
     *
     * @param key
     * @return the number of rows affected.
     */
    fun delete(key: String): Int


    /**
     * Query value according the key.
     *
     * @param key
     * @return result.
     */
    operator fun get(key: String): String

    /**
     * Query values according the key prefix string.
     *
     * @param keyPrefix
     * @return result.
     */
    fun getByPrefix(keyPrefix: String): Map<String, String>

    /**
     * Query values according the key contains string.
     *
     * @param keyContains
     * @return result.
     */
    fun getByContains(keyContains: String): Map<String, String>

    /**
     * Clear the DataManager table.
     */
    fun clearTable()

}
