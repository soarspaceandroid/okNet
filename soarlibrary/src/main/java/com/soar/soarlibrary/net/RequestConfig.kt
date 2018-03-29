package com.soar.soarlibrary.net

import com.soar.soarlibrary.model.BaseBean
import java.util.*

/**
 *----------------------------------------------------
 *※ Author :  GaoFei
 *※ Date : 2018/3/9
 *※ Time : 18:11
 *※ Project : ylb-kotlin
 *※ Package : com.yonglibao.corelibrary.http
 *----------------------------------------------------
 */
class RequestConfig {
    var api:String = ""
    var params:Map<String , String> = HashMap()
    var clz:Class<*> = BaseBean::class.java
    var firstCache = false

    fun setFirstCache(filed :Boolean): RequestConfig{
        firstCache = filed
        return this
    }

    fun setApi(filed :String): RequestConfig {
        api = filed
        return this
    }

    fun setParams(filed:Map<String ,String>): RequestConfig {
        params = filed
        return this
    }

    fun setClz(filed:Class<*>): RequestConfig {
        clz = filed
        return this
    }



}