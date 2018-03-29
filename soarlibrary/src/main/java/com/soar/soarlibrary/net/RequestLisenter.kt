package com.soar.soarlibrary.net

import java.lang.Exception

/**
 *----------------------------------------------------
 *※ Author :  GaoFei
 *※ Date : 2018/3/9
 *※ Time : 16:23
 *※ Project : ylb-kotlin
 *※ Package : com.yonglibao.corelibrary.http
 *----------------------------------------------------
 */
open interface RequestLisenter<T> : OkLissenter {

    fun onBindData(t:T)

    fun onFail(t:T)

    fun onError(e: Exception?)
}