package android.soar.com.testapplication.net

import com.soar.soarlibrary.net.ResponseCode


/**
 *----------------------------------------------------
 *※ Author :  GaoFei
 *※ Date : 2018/3/14
 *※ Time : 9:41
 *※ Project : ylb-kotlin
 *※ Package : com.yonglibao.android.net
 *----------------------------------------------------
 */
object NetResponseCode{

    /**
     * 请求成功
     */
    val  RESPONSE_SUCCESS = ResponseCode.RESPONSE_SUCCESS

    /**
     * token失效或是过期
     */
    val TOKEN_OUT_OF_DATE = "4604"

    /**
     * 单点登陆
     */
    val SINGLE_LOGIN = "4614"

}