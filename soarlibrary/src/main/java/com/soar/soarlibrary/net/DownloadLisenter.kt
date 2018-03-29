package com.soar.soarlibrary.net

import java.io.File
import java.lang.Exception

/**
 *----------------------------------------------------
 *※ Author :  GaoFei
 *※ Date : 2018/3/27
 *※ Time : 17:41
 *※ Project : ylb-kotlin
 *※ Package : com.yonglibao.corelibrary.net
 *----------------------------------------------------
 */
abstract interface DownloadLisenter: OkLissenter {

    abstract fun getOutputFile():File

    fun onStart()

    fun onFail(e: Exception?)

    fun onSuccess(file: File)

    fun onProgress(progress:Int)

}