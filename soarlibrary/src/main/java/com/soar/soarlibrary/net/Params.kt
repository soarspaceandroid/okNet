package com.soar.soarlibrary.net

import java.util.*

/**
 *----------------------------------------------------
 *※ Author :  GaoFei
 *※ Date : 2018/3/12
 *※ Time : 17:25
 *※ Project : ylb-kotlin
 *※ Package : com.yonglibao.corelibrary.http
 *----------------------------------------------------
 */
class Params{

    private val map: HashMap<String, String> = HashMap()


    companion object {

        fun create():Params{
            return lazy { Params() }.value
        }
    }

    fun put(key:String , value:String):Params{
        map.put(key , value)
        return this
    }

    fun build():Map<String,String>{
        return map
    }

}