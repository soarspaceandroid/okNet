package com.soar.soarlibrary.model

import com.google.gson.annotations.SerializedName

/**
 *----------------------------------------------------
 *※ Author :  GaoFei
 *※ Date : 2018/3/9
 *※ Time : 16:36
 *※ Project : ylb-kotlin
 *※ Package : com.yonglibao.corelibrary.base
 *----------------------------------------------------
 */
open class BaseBean {

    @SerializedName(value = "code")
    var code: String = ""


    @SerializedName(value = "msg")
    var msg: String = ""

    var isCache: Boolean = false //  true 缓存 , false 从网络获取
}
