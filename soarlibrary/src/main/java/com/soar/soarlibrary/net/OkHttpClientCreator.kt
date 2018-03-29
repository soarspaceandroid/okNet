package com.soar.soarlibrary.net

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit



/**
 *----------------------------------------------------
 *※ Author :  GaoFei
 *※ Date : 2018/3/9
 *※ Time : 15:14
 *※ Project : ylb-kotlin
 *※ Package : com.yonglibao.corelibrary.http
 *----------------------------------------------------
 */
object OkHttpClientCreator {

        private val CONNECTION_TIMEOUT = 30000L
        private val READ_TIMEOUT = 30000L
        private val WRITE_TIMEOUT = 30000L

        private var okHttpClient:OkHttpClient? = null
        fun getClient():OkHttpClient{
            if(okHttpClient == null){
                synchronized(OkHttpClient::class.java){
                    if(okHttpClient == null){
                        okHttpClient = OkHttpClient().newBuilder()
                                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                                .addInterceptor(NetInterceptor())
                                .build()
                    }
                }
            }
            return okHttpClient!!
        }
}