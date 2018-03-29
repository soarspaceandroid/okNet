package com.soar.soarlibrary.net

import android.text.TextUtils
import android.util.Log
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.Response
import java.util.*

/**
 *----------------------------------------------------
 *※ Author :  GaoFei
 *※ Date : 2018/3/9
 *※ Time : 14:49
 *※ Project : ylb-kotlin
 *※ Package : com.yonglibao.corelibrary.http
 *----------------------------------------------------
 */
class NetInterceptor : Interceptor {
    private var url:String = ""
    private var token:String = ""
    private var channel:String = ""

    private val PART_FORM = 0 //表单系列参数

    override fun intercept(chain: Interceptor.Chain?): Response {

        val originalRequest = chain?.request()
        if(originalRequest?.tag() == RequestHelper.TAG_DOWN_FILE){
            return chain!!.proceed(originalRequest)
        }

        this.url = RequestPartsInter.getRequestParts().getUrl()
        this.token = RequestPartsInter.getRequestParts().getToken()
        this.channel = RequestPartsInter.getRequestParts().getChannel()

        val requestBuilder = originalRequest?.newBuilder()
        if (originalRequest?.body() is MultipartBody) {
            val oldBody = originalRequest.body() as MultipartBody
            var oldFormBody = oldBody.part(PART_FORM).body() as FormBody
            val newFormBody = FormBody.Builder()
            var originParams = HashMap<String, String>()
            var paramsString = originalRequest.url().toString()
            for (i in 0 until oldFormBody.size()) {
                newFormBody.addEncoded(oldFormBody.encodedName(i), oldFormBody.encodedValue(i))
                originParams.put(oldFormBody.encodedName(i), oldFormBody.encodedValue(i))
                paramsString = "${paramsString} \n" + oldFormBody.encodedName(i) + "=" + oldFormBody.encodedValue(i) + "  "
            }
            var params = getParams(originParams, originalRequest.url().toString().replace(url, ""))
            var iters = params.entries.iterator()
            while (iters.hasNext()) {
                var item = iters.next()
                newFormBody.add(item.key, item.value)
                paramsString = "${paramsString}" + item.key + "=" + item.value + "  "
            }
            Log.d("Request", paramsString)
            requestBuilder?.method(originalRequest.method(), newFormBody.build())
        }
        val newRequest = requestBuilder!!.addHeader("X-Wap-Proxy-Cookie", "none")
                .addHeader("Cookie", "cookie")
                .addHeader("User-Agent", "Android")
        if (!TextUtils.isEmpty(token)) {
            newRequest.addHeader("Authorization", token)
        }
        return chain!!.proceed(newRequest.build())

    }



    /**
     * 公共参数参数
     */
    fun getParams(map: MutableMap<String, String>, completeApi:String): Map<String, String> {
        map.put("auth_key", "soar")
        map.put("auth_timestamp", (System.currentTimeMillis() / 1000).toString() + "")
        map.put("auth_version", "1.0.0")
        map.put("device_type", "2")//代表android
        return map
    }


    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    fun sortMapByKey(map: Map<String, String>?): Map<String, String>? {
        if (map == null || map.isEmpty()) {
            return null
        }
        val sortMap = TreeMap<String, String>(
                MapKeyComparator())
        sortMap.putAll(map)
        return sortMap
    }

    private class MapKeyComparator : Comparator<String> {

        override fun compare(str1: String, str2: String): Int {

            return str1.compareTo(str2)
        }
    }




}