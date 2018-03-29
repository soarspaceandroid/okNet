package com.soar.soarlibrary.net

import java.lang.reflect.Proxy

/**
 *----------------------------------------------------
 *※ Author :  GaoFei
 *※ Date : 2018/3/9
 *※ Time : 17:16
 *※ Project : ylb-kotlin
 *※ Package : com.yonglibao.corelibrary.http
 *----------------------------------------------------
 */
open interface RequestPartsInter {

    fun getToken():String

    fun getChannel():String

    fun getUrl():String

    companion object {
        private var requestPartsInter:RequestPartsInter? = null
        fun getRequestParts():RequestPartsInter{
            if (requestPartsInter == null){
                synchronized(RequestPartsInter::class.java){
                    if(requestPartsInter == null){
                        requestPartsInter = createReqeustParts()
                    }
                }
            }
            return requestPartsInter!!
        }



        private fun createReqeustParts():RequestPartsInter{
            val requestPartsClass = RequestPartsInter::class.java
            var requestPartsInter =  Proxy.newProxyInstance(requestPartsClass!!.getClassLoader(), arrayOf(requestPartsClass)) { proxy, method, args ->
                val cls = Class.forName("android.soar.com.testapplication.net.RequestPartsIntermediary")
               method.invoke(cls.newInstance())
            } as RequestPartsInter
            return requestPartsInter
        }

    }


}