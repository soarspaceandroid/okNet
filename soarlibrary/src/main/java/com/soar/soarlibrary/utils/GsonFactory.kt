package com.soar.soarlibrary.utils

import com.google.gson.Gson

/**
 *----------------------------------------------------
 *※ Author :  GaoFei
 *※ Date : 2018/3/9
 *※ Time : 16:39
 *※ Project : ylb-kotlin
 *※ Package : com.yonglibao.corelibrary.utils
 *
 *----------------------------------------------------
 */
object GsonFactory {

    private var gson: Gson? = null

    fun create(): Gson {
        if (gson == null) {
            synchronized(GsonFactory::class.java) {
                if (gson == null) {
                    gson = Gson()
                }
            }
        }
        return gson!!
    }

}