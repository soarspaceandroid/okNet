package com.soar.soarlibrary.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import android.util.Log
import com.soar.soarlibrary.db.core.KVManagerImpl
import com.soar.soarlibrary.db.core.KVManger
import com.soar.soarlibrary.utils.GsonFactory

/**
 *----------------------------------------------------
 *※ Author :  GaoFei
 *※ Date : 2018/3/13
 *※ Time : 16:02
 *※ Project : ylb-kotlin
 *※ Package : com.yonglibao.corelibrary.db
 *----------------------------------------------------
 */
class DataCacheManager {


    companion object {

        val TAG = "DataManager"
        private var instance: KVManger? = null
        private var dataManager: DataCacheManager? = null


        /**
         * Init component.
         *
         * @param context      used to open or create the database
         * @param databaseName database name for opening or creating
         */
        fun init(context: Context, databaseName: String): KVManger {
            val database = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null)
            return init(database)
        }

        /**
         * Init component.
         *
         * @param database
         * @return KVManager instance.
         */
        fun init(database: SQLiteDatabase): KVManger {
            if (instance == null) {
                synchronized(KVManger::class.java) {
                    if (instance == null) {
                        instance = KVManagerImpl(database)
                    }
                }
            }
            return instance!!
        }


        /**
         * get instance
         * @return
         */
        @Synchronized
        fun getInstance(): DataCacheManager {
            if (dataManager == null) {
                synchronized(DataCacheManager::class.java) {
                    if (dataManager == null) {
                        dataManager = DataCacheManager()
                    }
                }
            }
            return dataManager!!
        }


        /**
         * Get KVManager instance.
         *
         * @return KVManager instance.
         */
        private fun getClient(): KVManger? {
            return instance
        }

        /**
         * Must be called when this app is finishing.
         */
        fun destroy() {
            if (instance != null) {
                instance!!.database.close()
            }
        }
    }

    /**
     *   -------------------------------------------insert updata ------------------------------------
     */


    /**
     * insert boolean
     * @param key
     * @param value
     */
    fun insertBoolean(key: String, value: Boolean) {
        if (!TextUtils.isEmpty(key)) {
            try {
                getClient()!!.insertOrUpdate(key, value.toString())
            } catch (e: Exception) {
                Log.d(TAG, "insert boolean fail")
            }

        }
    }


    /**
     * get boolean
     * @param key
     * @return
     */
    fun getBoolean(key: String): Boolean {
        if (TextUtils.isEmpty(key)) {
            return false
        }
        return if (getClient()!!.keyExists(key)) {
            try {
                java.lang.Boolean.parseBoolean(getClient()!!.get(key))
            } catch (e: Exception) {
                Log.d(TAG, "get boolean fail")
                false
            }

        } else {
            false
        }

    }

    /**
     * 检查是否存在key
     * @param key
     * @return
     */
    fun isExistKey(key: String): Boolean {
        try {
            return getClient()!!.keyExists(key)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }


    /**
     * insert object
     * @param t
     * @param <T>
    </T> */
    fun <T : Any> insertObject(t: T?) {
        if (t == null) {
            return
        }
        getClient()!!.insertOrUpdate(t.javaClass.simpleName, GsonFactory.create().toJson(t))
    }


    /**
     * insert object
     * @param t
     * @param <T>
    </T> */
    fun <T> insertObject(key: String, t: T?) {
        if (t == null) {
            return
        }
        if (!TextUtils.isEmpty(key)) {
            getClient()!!.insertOrUpdate(key, GsonFactory.create().toJson(t))
        }
    }

    /**
     * get object
     * @param tClass
     * @param <T>
     * @return
    </T> */
    fun <T> getObject(tClass: Class<T>?): T? {
        if (tClass == null) {
            return null
        }
        return if (getClient()!!.keyExists(tClass.simpleName)) {
            GsonFactory.create().fromJson(getClient()!!.get(tClass.simpleName), tClass)
        } else {
            null
        }
    }


    /**
     * get object
     * @param tClass
     * @param <T>
     * @return
    </T> */
    fun <T> getObject(key: String, tClass: Class<T>?): T? {
        if (TextUtils.isEmpty(key) || tClass == null) {
            return null
        }
        return if (getClient()!!.keyExists(key)) {
            GsonFactory.create().fromJson(getClient()!!.get(key), tClass)
        } else {
            null
        }
    }

    /**
     * insert string
     * @param key
     * @param value
     */
    fun insertString(key: String, value: String) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            getClient()!!.insertOrUpdate(key, value)
        }
    }


    /**
     * get string
     * @param key
     * @return
     */
    fun getString(key: String): String {
        if (TextUtils.isEmpty(key)) {
            return ""
        }
        return if (getClient()!!.keyExists(key)) {
            getClient()!!.get(key)
        } else {
            ""
        }
    }


    /**
     * insert integer
     * @param key
     * @param value
     */
    fun insertInteger(key: String, value: Int) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value.toString() + "")) {
            getClient()!!.insertOrUpdate(key, value.toString() + "")
        }
    }


    /**
     * get integer
     * @param key
     * @return
     */
    fun getInteger(key: String): Int {
        if (TextUtils.isEmpty(key)) {
            return -1
        }
        return if (getClient()!!.keyExists(key)) {
            try {
                Integer.parseInt(getClient()!!.get(key))
            } catch (e: Exception) {
                -1
            }

        } else {
            -1
        }
    }

    /**
     * delete data
     * @param key
     */
    fun deleteValue(key: String) {
        if (TextUtils.isEmpty(key)) {
            Log.d(TAG, "delete data fail")
            return
        }
        if (getClient()!!.keyExists(key)) {
            getClient()!!.delete(key)
        }
    }


    fun closeDB() {
        destroy()
    }


}